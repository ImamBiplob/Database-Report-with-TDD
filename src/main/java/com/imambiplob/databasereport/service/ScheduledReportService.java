package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.ScheduledReportDTO;
import com.imambiplob.databasereport.entity.ScheduledReport;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.repository.ScheduledReportRepository;
import com.imambiplob.databasereport.repository.UserRepository;
import com.imambiplob.databasereport.util.Converter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.imambiplob.databasereport.util.Converter.convertScheduledReportDTOToScheduledReport;
import static com.imambiplob.databasereport.util.Converter.convertScheduledReportToScheduledReportDTO;

@Service
public class ScheduledReportService {

    @PersistenceContext
    private EntityManager entityManager;
    private final ApplicationEventPublisher publisher;
    private final ScheduledReportRepository reportRepository;
    private final UserRepository userRepository;
    private final CsvExportService csvExportService;
    private final EmailService emailService;
    private final CsvExportServiceForSingleColumnResult csvExportServiceForSingleColumnResult;

    public ScheduledReportService(ApplicationEventPublisher publisher, ScheduledReportRepository reportRepository, UserRepository userRepository, CsvExportService csvExportService, EmailService emailService, CsvExportServiceForSingleColumnResult csvExportServiceForSingleColumnResult) {
        this.publisher = publisher;
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.csvExportService = csvExportService;
        this.emailService = emailService;
        this.csvExportServiceForSingleColumnResult = csvExportServiceForSingleColumnResult;
    }

    @Transactional
    public ScheduledReportDTO addReport(ScheduledReportDTO reportDTO, String username) {

        User user = userRepository.findUserByUsername(username);

        return convertScheduledReportToScheduledReportDTO(reportRepository.save(convertScheduledReportDTOToScheduledReport(reportDTO, user)));

    }

    public List<ScheduledReportDTO> getReports() {

        return reportRepository.findAll().stream()
                .map(Converter::convertScheduledReportToScheduledReportDTO)
                .collect(Collectors.toList());

    }

    public ScheduledReportDTO getReportById(long id) {

        return convertScheduledReportToScheduledReportDTO(reportRepository.findScheduledReportById(id));

    }

    public ScheduledReportDTO updateReport(ScheduledReportDTO reportDTO, long id) {

        ScheduledReport report = reportRepository.findScheduledReportById(id);

        report.setReportName(reportDTO.getReportName());
        report.setQuery(reportDTO.getQuery());
        report.setColumns(reportDTO.getColumns());
        report.setParamsMap(reportDTO.getParamsMap());
        report.setLastUpdateTime(new Date());
        report.setTime(reportDTO.getTime());
        report.setDaily(reportDTO.isDaily());
        report.setWeekly(reportDTO.isWeekly());
        report.setMonthly(reportDTO.isMonthly());
        report.setYearly(reportDTO.isYearly());
        report.getParamsMap().remove("");

        return convertScheduledReportToScheduledReportDTO(reportRepository.save(report));

    }

    public ScheduledReportDTO deleteReport(long id) {

        ScheduledReportDTO reportDTO = convertScheduledReportToScheduledReportDTO(reportRepository.findScheduledReportById(id));

        reportRepository.deleteById(id);

        return reportDTO;

    }
}
