package com.imambiplob.databasereport;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.dto.RunResult;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.repository.*;
import com.imambiplob.databasereport.service.ReportService;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class DatabaseReportServiceTests {

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExecutionHistoryRepository executionHistoryRepository;

    @Autowired
    private UpdateHistoryRepository updateHistoryRepository;

    @Autowired
    private ReportFileRepository reportFileRepository;

    @BeforeEach
    void setup() {

//        executionHistoryRepository.deleteAll();
//        updateHistoryRepository.deleteAll();
//        reportFileRepository.deleteAll();
//        reportRepository.deleteAll();
        if (userRepository.findUserByUsername("admin") != null) {
            reportRepository.deleteAllByReportCreatorId(userRepository.findUserByUsername("admin").getId());
            userRepository.deleteById(userRepository.findUserByUsername("admin").getId());
        }

        userRepository.save(User.builder()
                .username("admin")
                .email("admin@gmail.com")
                .roles("DEVELOPER")
                .password("admin")
                .phone("01521559190").build());

    }

    @Test
    @DisplayName("Test for Successful Add of a Report")
    public void addReportWithSuccess() {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        ReportDTO reportDTO = ReportDTO.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, dept_name, salary from salaries s " +
                        "join employees e on e.emp_no = s.emp_no " +
                        "join dept_emp de on de.emp_no = e.emp_no " +
                        "join departments d on d.dept_no = de.dept_no where salary > :salary")
                .columns("first_name,department,salary")
                .paramsMap(paramsMap)
                .build();

        ReportDTO addedReport = reportService.addReport(reportDTO, "admin");

        Assertions.assertNotNull(addedReport, "Report should be added");
        Assertions.assertEquals(reportDTO.getReportName(), addedReport.getReportName());

    }

    @Test
    @DisplayName("Test for Unsuccessful Add of a Report with No Report Name")
    public void addReportWithFailure() {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        ReportDTO reportDTO = ReportDTO.builder().reportName("")            /* No report name */
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> reportService.addReport(reportDTO, "admin"));

    }

    @Test
    @DisplayName("Test for Successful Retrieve of a Report")
    public void retrieveReportWithSuccess() {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report, "admin");

        ReportDTO retrievedReport = reportService.getReportById(savedReport.getId());

        Assertions.assertNotNull(retrievedReport, "Report should be retrieved");
        Assertions.assertEquals(report.getReportName(), retrievedReport.getReportName());

    }

    @Test
    @DisplayName("Test for Unsuccessful Retrieve of a Report with Non-existing ID")
    public void retrieveReportWithInvalidId() {

        ReportDTO retrievedReport = reportService.getReportById(100000L);

        Assertions.assertNull(retrievedReport, "Report of Id 100000 should not exist");

    }

    @Test
    @DisplayName("Test for Successful Retrieve of Reports")
    public void retrieveReportsWithSuccess() {

        List<ReportDTO> retrievedReports = reportService.getReports();

        Assertions.assertNotNull(retrievedReports);

        /* If there is no reports, it will return empty list. That's why it's always not null. */

    }

    @Test
    @DisplayName("Test for Successful Run of a Report")
    public void runReportWithValidId() {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        ReportDTO report = ReportDTO.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, dept_name, salary from salaries s join employees e on e.emp_no = s.emp_no join dept_emp de on de.emp_no = e.emp_no join departments d on d.dept_no = de.dept_no where salary > :salary")
                .columns("first_name,department,salary")
                .paramsMap(paramsMap)
                .build();

        report = reportService.addReport(report, "admin");

        RunResult runResult = reportService.runReport(report.getId(), "admin");

        Assertions.assertNotNull(runResult);          /* If report exists and sql is valid, result will not be null */

        /* Successful run operation results in successful history creation */

        Assertions.assertEquals("All Employees Where Salary is Getter Than 100000", executionHistoryRepository.findReportExecutionHistoriesByReportIdIs(report.getId()).get(0).getReportName());

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Non-existing ID")
    public void runReportWithInvalidId() {

        /* If report doesn't exist, there will be null pointer exception and no history will be created */

        Assertions.assertThrows(NullPointerException.class, () -> reportService.runReport(10000000L, "admin"));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> executionHistoryRepository.findReportExecutionHistoriesByReportIdIs(10000000L).get(0));  /* No History */

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Invalid SQL")
    public void runReportOfInvalidSQL() {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employee")      /* No table named employee in database */
                .columns("first_name,job_title,salary")
                .build();

        report = reportService.addReport(report, "admin");

        /* It will throw an exception due to invalid SQL */

        final ReportDTO finalReport = report;
        Assertions.assertThrows(SQLGrammarException.class, () -> reportService.runReport(finalReport.getId(), "admin"));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> executionHistoryRepository.findReportExecutionHistoriesByReportIdIs(finalReport.getId()).get(0));  /* No History */

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Malformed SQL Statement")
    public void runReportOfMalformedSQLStatement() {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("slect first_name, job_title, salary from employees")      /* No statement named slect in SQL */
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO finalReport = reportService.addReport(report, "admin");

        /* It will throw an exception due to malformed SQL statement */

        Assertions.assertThrows(GenericJDBCException.class, () -> reportService.runReport(finalReport.getId(), "admin"));
        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> executionHistoryRepository.findReportExecutionHistoriesByReportIdIs(finalReport.getId()).get(0));  /* No History */

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Parameter Error")
    public void runReportOfWrongfulParameter() {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        ReportDTO report = ReportDTO.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salay")     /* salay doesn't match with paramName salary */
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        ReportDTO finalReport = reportService.addReport(report, "admin");

        Assertions.assertThrows(IllegalArgumentException.class, () -> reportService.runReport(finalReport.getId(), "admin"));

        /* Couldn't set parameter due to mismatched parameter names, hence threw an exception */

        Assertions.assertThrows(IndexOutOfBoundsException.class, () -> executionHistoryRepository.findReportExecutionHistoriesByReportIdIs(finalReport.getId()).get(0));  /* No History */

    }

    @Test
    @DisplayName("Test for Successful Update of a Report")
    public void updateReportWithValidId() {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report, "admin");

        savedReport.setReportName("All Junior Executives");
        savedReport.setQuery("select first_name, job_title, salary from employees where job_title = \"Junior Executive\"");

        ReportDTO updatedReport = reportService.updateReport(savedReport, savedReport.getId(), "admin");

        Assertions.assertEquals(savedReport.getId(), updatedReport.getId());
        Assertions.assertEquals("All Junior Executives", updatedReport.getReportName());

    }

    @Test
    @DisplayName("Test for Successful Delete of a Report")
    public void deleteReportWithValidId() {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report, "admin");

        ReportDTO deletedReport = reportService.deleteReport(savedReport.getId());

        Assertions.assertNull(reportRepository.findReportById(deletedReport.getId()), "Report should not exist since it's deleted");

    }

//    @Test
//    @DisplayName("Test for Successful Delete of All Reports")
//    public void deleteAllReportsWithSuccess() {
//
//        Report report = Report.builder().reportName("All Employees")
//                .query("select first_name, job_title, salary from employees")
//                .columns("first_name,job_title,salary")
//                .build();
//
//        reportRepository.save(report);
//
//        reportService.deleteAllReports();
//
//        Assertions.assertEquals(0, reportRepository.count());
//
//    }

}
