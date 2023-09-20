package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.EmailDetails;
import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.dto.ResponseMessage;
import com.imambiplob.databasereport.dto.RunResult;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.event.ReportExecutionEventForFile;
import com.imambiplob.databasereport.event.ReportExecutionEventForHistory;
import com.imambiplob.databasereport.repository.ReportRepository;
import com.imambiplob.databasereport.repository.UserRepository;
import com.imambiplob.databasereport.util.Converter;
import com.imambiplob.databasereport.util.MultipartFileImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.imambiplob.databasereport.util.Converter.convertReportDTOToReport;
import static com.imambiplob.databasereport.util.Converter.convertReportToReportDTO;

@Service
public class ReportService {

    @PersistenceContext
    private EntityManager entityManager;
    private final ApplicationEventPublisher publisher;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CsvExportService csvExportService;
    private final EmailService emailService;
    private final CsvExportServiceForSingleColumnResult csvExportServiceForSingleColumnResult;

    public ReportService(ApplicationEventPublisher publisher, ReportRepository reportRepository, UserRepository userRepository, CsvExportService csvExportService, EmailService emailService, CsvExportServiceForSingleColumnResult csvExportServiceForSingleColumnResult) {
        this.publisher = publisher;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.csvExportService = csvExportService;
        this.emailService = emailService;
        this.csvExportServiceForSingleColumnResult = csvExportServiceForSingleColumnResult;
    }

    @Transactional
    public ReportDTO addReport(ReportDTO reportDTO, String username) {

        User user = userRepository.findUserByUsername(username);
        
        return convertReportToReportDTO(reportRepository.save(convertReportDTOToReport(reportDTO, user)));

    }

    public List<ReportDTO> getReports() {

        return reportRepository.findAll().stream()
                .map(Converter::convertReportToReportDTO)
                .collect(Collectors.toList());

    }

    public List<ReportDTO> findReportsWithSorting(String field) {

        return  reportRepository.findAll(Sort.by(Sort.Direction.ASC, field)).stream()
                .map(Converter::convertReportToReportDTO)
                .collect(Collectors.toList());

    }

    public List<ReportDTO> findReportsWithPagination(int offset, int pageSize) {

        return reportRepository.findAll(PageRequest.of(offset, pageSize))
                .getContent().stream()
                .map(Converter::convertReportToReportDTO)
                .collect(Collectors.toList());

    }

    public List<ReportDTO> findReportsWithPaginationAndSorting(int offset, int pageSize, String field) {

        return reportRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)))
                .getContent().stream()
                .map(Converter::convertReportToReportDTO)
                .collect(Collectors.toList());

    }

    public ReportDTO getReportById(long id) {

        return convertReportToReportDTO(reportRepository.findReportById(id));

    }

    public ReportDTO updateReport(ReportDTO reportDTO, long id) {

        Report report = reportRepository.findReportById(id);

        report.setReportName(reportDTO.getReportName());
        report.setQuery(reportDTO.getQuery());
        report.setColumns(reportDTO.getColumns());
        report.setParamsMap(reportDTO.getParamsMap());
        report.setLastUpdateTime(new Date());
        report.getParamsMap().remove("");

        return convertReportToReportDTO(reportRepository.save(report));

    }

    public ReportDTO deleteReport(long id) {

        ReportDTO reportDTO = convertReportToReportDTO(reportRepository.findReportById(id));

        reportRepository.deleteById(id);

        return reportDTO;

    }

    public ResponseMessage deleteAllReports() {

        if(reportRepository.count() > 0) {
            reportRepository.deleteAll();
            return new ResponseMessage("All Reports Have Been DELETED!!!");
        }

        return new ResponseMessage("It's Already Empty!!!");

    }

    @Transactional
    public RunResult runReport(long id, String username) {

        User user = userRepository.findUserByUsername(username);  /* Current user who is executing query */

        Report report = reportRepository.findReportById(id);

        String filePath = "reports/" + "#" + report.getId() + " - " + report.getReportName() + ".csv";

        RunResult runResult = performExecution(report, filePath);

        publisher.publishEvent(new ReportExecutionEventForHistory(this, user, report));

        return runResult;

    }

    @Scheduled(cron = "${interval-in-cron}")
    @Transactional
    public void runReport() {

        Report report = reportRepository.findReportById(555);

        String filePath = "scheduledReports/" + "#" + report.getId() + " - " + report.getReportName() + ".csv";

        performExecution(report, filePath);

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setAttachment(filePath);
        emailDetails.setSubject("Report of " + report.getReportName());
        emailDetails.setRecipient("imamhbiplob@gmail.com");
        emailDetails.setMsgBody("Hello,\n\nReport file of this month is attached with this email.\n\nThanks,\nImam Hossain\nSquare Health Ltd.");
        emailService.sendMailWithAttachment(emailDetails);

    }

    public RunResult performExecution(Report report, String filePath) {

        Object[] columns = null;

        if(report.getColumns() != null)
            columns = Arrays.stream(report.getColumns().trim().split(",")).toArray();

        File file;
        List results;

        assert columns != null;
        if(columns.length > 1) {

            results = getResults(report);
            file = csvExportService.exportQueryResultToCsv(results, filePath, columns);

        }
        else {

            results = getResults(report);
            file = csvExportServiceForSingleColumnResult.exportQueryResultToCsv(results, filePath, columns);

        }

        MultipartFile multipartFile = new MultipartFileImpl(file);

        publisher.publishEvent(new ReportExecutionEventForFile(this, multipartFile, report));

        RunResult runResult = new RunResult();
        runResult.setColumns(columns);
        runResult.setResults(results);

        return runResult;

    }

    public List getResults(Report report) {

        Query query = entityManager.createNativeQuery(report.getQuery());

        for (String paramName : report.getParamsMap().keySet()) {
            query.setParameter(paramName, report.getParamsMap().get(paramName));
        }

        return query.getResultList();

    }

}
