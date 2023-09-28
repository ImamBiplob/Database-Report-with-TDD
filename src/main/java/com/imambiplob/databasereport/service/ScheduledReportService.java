package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.EmailDetails;
import com.imambiplob.databasereport.dto.ScheduledReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.ScheduledReport;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.repository.ReportRepository;
import com.imambiplob.databasereport.repository.ScheduledReportRepository;
import com.imambiplob.databasereport.repository.UserRepository;
import com.imambiplob.databasereport.util.Converter;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.imambiplob.databasereport.util.Converter.convertScheduledReportDTOToScheduledReport;
import static com.imambiplob.databasereport.util.Converter.convertScheduledReportToScheduledReportDTO;

@Service
public class ScheduledReportService {
    private final ReportRepository reportRepository;
    private final ScheduledReportRepository scheduledReportRepository;
    private final UserRepository userRepository;
    private final ReportService reportService;
    private final EmailService emailService;

    public ScheduledReportService(ReportRepository reportRepository, ScheduledReportRepository scheduledReportRepository, UserRepository userRepository, ReportService reportService, EmailService emailService) {
        this.reportRepository = reportRepository;
        this.scheduledReportRepository = scheduledReportRepository;
        this.userRepository = userRepository;
        this.reportService = reportService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "${interval-in-cron}")
    @Transactional
    public void runReport() {

        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for(ScheduledReport report : reports) {
            String filePath = "scheduledReports/" + "#" + report.getId() + " - " + report.getReportName() + ".csv";

            reportService.performExecution(report, filePath);

            EmailDetails emailDetails = new EmailDetails();
            emailDetails.setAttachment(filePath);
            emailDetails.setSubject("Report of " + report.getReportName());
            emailDetails.setRecipient("imamhbiplob@gmail.com");
            emailDetails.setMsgBody("Hello,\n\nReport file of this month is attached with this email.\n\nThanks,\nImam Hossain\nSquare Health Ltd.");
            emailService.sendMailWithAttachment(emailDetails);
        }

    }

    @Transactional
    public ScheduledReportDTO addReport(ScheduledReportDTO reportDTO, String username) {

        User user = userRepository.findUserByUsername(username);

        return convertScheduledReportToScheduledReportDTO(scheduledReportRepository.save(convertScheduledReportDTOToScheduledReport(reportDTO, user)));

    }

    public List<ScheduledReportDTO> getReports() {

        return scheduledReportRepository.findAll().stream()
                .map(Converter::convertScheduledReportToScheduledReportDTO)
                .collect(Collectors.toList());

    }

    public ScheduledReportDTO getReportById(long id) {

        return convertScheduledReportToScheduledReportDTO(scheduledReportRepository.findScheduledReportById(id));

    }

    public ScheduledReportDTO updateReport(ScheduledReportDTO reportDTO, long id) {

        ScheduledReport scheduledReport = new ScheduledReport();

        if(scheduledReportRepository.findScheduledReportById(id) == null) {

            Report savedReport = reportRepository.findReportById(id);

            scheduledReport.setReportCreator(savedReport.getReportCreator());
            scheduledReport.setCreationTime(savedReport.getCreationTime());
        }
        else {
            scheduledReport = scheduledReportRepository.findScheduledReportById(id);
        }

        scheduledReport.setReportName(reportDTO.getReportName());
        scheduledReport.setQuery(reportDTO.getQuery());
        scheduledReport.setColumns(reportDTO.getColumns());
        scheduledReport.setParamsMap(reportDTO.getParamsMap());
        scheduledReport.setLastUpdateTime(new Date());
        scheduledReport.setTime(reportDTO.getTime());
        scheduledReport.setDaily(reportDTO.isDaily());
        scheduledReport.setWeekly(reportDTO.isWeekly());
        scheduledReport.setMonthly(reportDTO.isMonthly());
        scheduledReport.setYearly(reportDTO.isYearly());
        scheduledReport.getParamsMap().remove("");

        return convertScheduledReportToScheduledReportDTO(scheduledReportRepository.save(scheduledReport));

    }

    public ScheduledReportDTO deleteReport(long id) {

        ScheduledReportDTO reportDTO = convertScheduledReportToScheduledReportDTO(scheduledReportRepository.findScheduledReportById(id));

        scheduledReportRepository.deleteById(id);

        return reportDTO;

    }
}
