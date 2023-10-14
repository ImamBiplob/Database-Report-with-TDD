package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.EmailDetails;
import com.imambiplob.databasereport.dto.ScheduledReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.ScheduledReport;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.event.ReportUpdateEventForHistory;
import com.imambiplob.databasereport.repository.ReportRepository;
import com.imambiplob.databasereport.repository.ScheduledReportRepository;
import com.imambiplob.databasereport.repository.UserRepository;
import com.imambiplob.databasereport.util.Converter;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
    private final ApplicationEventPublisher publisher;
    private final TaskScheduler taskScheduler;

    public ScheduledReportService(ReportRepository reportRepository, ScheduledReportRepository scheduledReportRepository, UserRepository userRepository, ReportService reportService, EmailService emailService, ApplicationEventPublisher publisher, TaskScheduler taskScheduler) {
        this.reportRepository = reportRepository;
        this.scheduledReportRepository = scheduledReportRepository;
        this.userRepository = userRepository;
        this.reportService = reportService;
        this.emailService = emailService;
        this.publisher = publisher;
        this.taskScheduler = taskScheduler;
    }

//    @Transactional
//    public void delegateScheduledReportsToTaskScheduler() {
//        List<ScheduledReport> reports = scheduledReportRepository.findAll();
//
//        for (ScheduledReport report : reports) {
//            String cronExpression = report.getCronExpression();
//            taskScheduler.schedule(() -> runReport(report), new CronTrigger(cronExpression));
//        }
//        System.out.println("Delegated to Task Scheduler Successfully");
//    }

    @Scheduled(fixedRate = 3600000) // every hour
    //@Scheduled(fixedRate = 60000)
    @Transactional
    public void runDailyReports() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isDaily()) {
                LocalTime reportTime = report.getTime();
                LocalTime currentTime = LocalTime.now();
                if (reportTime.getHour() == currentTime.getHour()) {
                    runReport(report);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * ? * SAT") // every hour on saturday
    @Transactional
    public void runWeeklyReportsOfSaturday() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isWeekly()) {
                if (Objects.equals(report.getWeekDay(), "SATURDAY")) {
                    LocalTime reportTime = report.getTime();
                    LocalTime currentTime = LocalTime.now();
                    if (reportTime.getHour() == currentTime.getHour()) {
                        runReport(report);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * ? * SUN") // every hour on sunday
    @Transactional
    public void runWeeklyReportsOfSunday() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isWeekly()) {
                if (Objects.equals(report.getWeekDay(), "SUNDAY")) {
                    LocalTime reportTime = report.getTime();
                    LocalTime currentTime = LocalTime.now();
                    if (reportTime.getHour() == currentTime.getHour()) {
                        runReport(report);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * ? * MON") // every hour on monday
    @Transactional
    public void runWeeklyReportsOfMonday() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isWeekly()) {
                if (Objects.equals(report.getWeekDay(), "MONDAY")) {
                    LocalTime reportTime = report.getTime();
                    LocalTime currentTime = LocalTime.now();
                    if (reportTime.getHour() == currentTime.getHour()) {
                        runReport(report);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * ? * TUE") // every hour on tuesday
    @Transactional
    public void runWeeklyReportsOfTuesday() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isWeekly()) {
                if (Objects.equals(report.getWeekDay(), "TUESDAY")) {
                    LocalTime reportTime = report.getTime();
                    LocalTime currentTime = LocalTime.now();
                    if (reportTime.getHour() == currentTime.getHour()) {
                        runReport(report);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * ? * WED") // every hour on wednesday
    @Transactional
    public void runWeeklyReportsOfWednesday() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isWeekly()) {
                if (Objects.equals(report.getWeekDay(), "WEDNESDAY")) {
                    LocalTime reportTime = report.getTime();
                    LocalTime currentTime = LocalTime.now();
                    if (reportTime.getHour() == currentTime.getHour()) {
                        runReport(report);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * ? * THU") // every hour on thursday
    @Transactional
    public void runWeeklyReportsOfThursday() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isWeekly()) {
                if (Objects.equals(report.getWeekDay(), "THURSDAY")) {
                    LocalTime reportTime = report.getTime();
                    LocalTime currentTime = LocalTime.now();
                    if (reportTime.getHour() == currentTime.getHour()) {
                        runReport(report);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 * ? * FRI") // every hour on friday
    @Transactional
    public void runWeeklyReportsOfFriday() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isWeekly()) {
                if (Objects.equals(report.getWeekDay(), "FRIDAY")) {
                    LocalTime reportTime = report.getTime();
                    LocalTime currentTime = LocalTime.now();
                    if (reportTime.getHour() == currentTime.getHour()) {
                        runReport(report);
                    }
                }
            }
        }
    }

    @Scheduled(cron = "0 0 8 1 * ?") // every month
    @Transactional
    public void runMonthlyReports() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isMonthly()) {
                LocalTime reportTime = report.getTime();
                LocalTime currentTime = LocalTime.now();
                if (reportTime.getHour() == currentTime.getHour()) {
                    runReport(report);
                }
            }
        }
    }

    @Scheduled(cron = "0 0 8 1 1 *") // every year
    @Transactional
    public void runYearlyReports() {
        List<ScheduledReport> reports = scheduledReportRepository.findAll();

        for (ScheduledReport report : reports) {
            if (report.isYearly()) {
                LocalTime reportTime = report.getTime();
                LocalTime currentTime = LocalTime.now();
                if (reportTime.getHour() == currentTime.getHour()) {
                    runReport(report);
                }
            }
        }
    }

    //@Scheduled(cron = "${interval-in-cron}")
    @Transactional
    public void runReport(ScheduledReport report) {

        String filePath = "scheduledReports/" + "#" + report.getId() + " - " + report.getReportName() + ".csv";

        reportService.performExecution(report, filePath);

        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setAttachment(filePath);
        emailDetails.setSubject("Report of " + report.getReportName());
        emailDetails.setRecipient(report.getEmailAddress());
        emailDetails.setMsgBody("Hello,\n\nReport file is attached with this email.\n\nThanks,\nImam Hossain\nSquare Health Ltd.");
        emailService.sendMailWithAttachment(emailDetails);

        System.out.println("Scheduled Report Execution Completed of ID: " + report.getId());

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

    @Transactional
    public ScheduledReportDTO updateReport(ScheduledReportDTO reportDTO, long id, String username) {

        User user = userRepository.findUserByUsername(username);

        Report previousReport = reportRepository.findReportById(id);
        publisher.publishEvent(new ReportUpdateEventForHistory(this, previousReport, reportDTO, user));

        ScheduledReport scheduledReport = new ScheduledReport();

        if (scheduledReportRepository.findScheduledReportById(id) == null) {

            Report savedReport = reportRepository.findReportById(id);

            scheduledReport.setReportCreator(savedReport.getReportCreator());
            scheduledReport.setCreationTime(savedReport.getCreationTime());
        } else {
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
        scheduledReport.setWeekDay(reportDTO.getWeekDay());
        scheduledReport.setMonthly(reportDTO.isMonthly());
        scheduledReport.setYearly(reportDTO.isYearly());
        scheduledReport.setEmailAddress(reportDTO.getEmailAddress());
        scheduledReport.setCronExpression(reportDTO.getCronExpression());
        scheduledReport.getParamsMap().remove("");

        return convertScheduledReportToScheduledReportDTO(scheduledReportRepository.save(scheduledReport));

    }

    public ScheduledReportDTO deleteReport(long id) {

        ScheduledReportDTO reportDTO = convertScheduledReportToScheduledReportDTO(scheduledReportRepository.findScheduledReportById(id));

        scheduledReportRepository.deleteById(id);

        return reportDTO;

    }
}
