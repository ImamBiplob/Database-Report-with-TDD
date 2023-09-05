package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.event.ReportExecutionEvent;
import com.imambiplob.databasereport.repository.ReportRepository;
import com.imambiplob.databasereport.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @PersistenceContext
    private EntityManager entityManager;
    private final ApplicationEventPublisher publisher;
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CsvExportService csvExportService;

    public ReportService(ApplicationEventPublisher publisher, ReportRepository reportRepository, UserRepository userRepository, CsvExportService csvExportService) {
        this.publisher = publisher;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.csvExportService = csvExportService;
    }

    public static ReportDTO convertReportToReportDTO(Report report) {

        if(report == null) {
            return null;
        }

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(report.getId());
        reportDTO.setReportName(report.getReportName());
        reportDTO.setQuery(report.getQuery());
        reportDTO.setColumns(report.getColumns());
        reportDTO.setParamsMap(report.getParamsMap());
        reportDTO.setReportCreatorName(report.getReportCreator().getUsername());
        reportDTO.setCreationTime(report.getCreationTime());
        reportDTO.setLastUpdateTime(report.getLastUpdateTime());

        return reportDTO;

    }

    public static Report convertReportDTOToReport(ReportDTO reportDTO, User user) {

        if(reportDTO == null) {
            return null;
        }

        Report report = new Report();
        report.setId(reportDTO.getId());
        report.setReportName(reportDTO.getReportName());
        report.setQuery(reportDTO.getQuery());
        report.setColumns(reportDTO.getColumns());
        report.setParamsMap(reportDTO.getParamsMap());
        report.setReportCreator(user);

        return report;

    }

    @Transactional
    public ReportDTO addReport(ReportDTO reportDTO) {

        User user = userRepository.findUserByUsername("admin");
        
        return convertReportToReportDTO(reportRepository.save(convertReportDTOToReport(reportDTO, user)));

    }

    public List<ReportDTO> getReports() {

        return reportRepository.findAll().stream()
                .map(ReportService::convertReportToReportDTO)
                .collect(Collectors.toList());

    }

    public List<ReportDTO> findReportsWithSorting(String field) {

        return  reportRepository.findAll(Sort.by(Sort.Direction.ASC, field)).stream()
                .map(ReportService::convertReportToReportDTO)
                .collect(Collectors.toList());

    }

    public List<ReportDTO> findReportsWithPagination(int offset, int pageSize) {

        return reportRepository.findAll(PageRequest.of(offset, pageSize))
                .getContent().stream()
                .map(ReportService::convertReportToReportDTO)
                .collect(Collectors.toList());

    }

    public List<ReportDTO> findReportsWithPaginationAndSorting(int offset, int pageSize, String field) {

        return reportRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)))
                .getContent().stream()
                .map(ReportService::convertReportToReportDTO)
                .collect(Collectors.toList());

    }

    public ReportDTO getReportById(long id) {

        return convertReportToReportDTO(reportRepository.findReportById(id));

    }

    @Transactional
    public List<Object[]> getResultForQuery(long id) {

        User user = userRepository.findUserByUsername("admin");  /* Current user who is executing query */

        Report report = reportRepository.findReportById(id);

        Object[] columns = Arrays.stream(report.getColumns().split(",")).toArray();
        String filePath = "reports/" + "#ID: " + report.getId() + " - " + report.getReportName() + ".csv";

        String sqlQuery = report.getQuery();
        Query query = entityManager.createNativeQuery(sqlQuery);

        for (String paramName : report.getParamsMap().keySet()) {
            query.setParameter(paramName, report.getParamsMap().get(paramName));
        }

        List<Object[]> results = query.getResultList();

        publisher.publishEvent(new ReportExecutionEvent(this, user, report));

        csvExportService.exportQueryResultToCsv(results, filePath, columns);

        return results;

    }

    public ReportDTO updateReport(ReportDTO reportDTO, long id) {

        Report report = reportRepository.findReportById(id);

        report.setReportName(reportDTO.getReportName());
        report.setQuery(reportDTO.getQuery());
        report.setColumns(reportDTO.getColumns());
        report.setParamsMap(reportDTO.getParamsMap());
        report.setLastUpdateTime(new Date());

        return convertReportToReportDTO(reportRepository.save(report));

    }

    public ReportDTO deleteReport(long id) {

        ReportDTO reportDTO = convertReportToReportDTO(reportRepository.findReportById(id));

        reportRepository.deleteById(id);

        return reportDTO;

    }

    public String deleteAllReports() {

        if(reportRepository.count() > 0) {
            reportRepository.deleteAll();
            return "All Reports Have Been DELETED!!!";
        }

        return "It's Already Empty!!!";

    }

}
