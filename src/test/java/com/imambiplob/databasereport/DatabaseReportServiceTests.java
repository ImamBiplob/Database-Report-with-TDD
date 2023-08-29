package com.imambiplob.databasereport;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.repository.ReportRepository;
import com.imambiplob.databasereport.service.ReportService;
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

    @BeforeEach
    void setup() {
        reportRepository.deleteAll();
    }

    @Test
    @DisplayName("Test for Successful Add of a Report")
    public void addReportWithSuccess() {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        Report report = Report.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        ReportDTO addedReport = reportService.addReport(report);

        Assertions.assertNotNull(addedReport, "Report should be added");
        Assertions.assertEquals("All Employees Where Salary is Getter Than 100000", addedReport.getReportName());
    }

    // get report success
    @Test
    @DisplayName("Test for Successful Retrieve of a Report")
    public void retrieveReportWithSuccess() {
        Report report = Report.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        reportRepository.save(report);

        ReportDTO retrievedReport = reportService.getReport(report.getId());

        Assertions.assertNotNull(retrievedReport, "Report should be retrieved");
        Assertions.assertEquals("All Employees", retrievedReport.getReportName());
    }

    // get report fail
    @Test
    @DisplayName("Test for Unsuccessful Retrieve of a Report with Non-existing ID")
    public void retrieveReportWithInvalidId() {

        ReportDTO retrievedReport = reportService.getReport(100000L);

        Assertions.assertNull(retrievedReport, "Report of Id 100000 should not exist");

    }

    // get reports success
    @Test
    @DisplayName("Test for Successful Retrieve of Reports")
    public void retrieveReportsWithSuccess() {

        List<ReportDTO> retrievedReports = reportService.getReports();

        Assertions.assertNotNull(retrievedReports);  // If there is no reports, it will return empty list.
                                                    // That's why it's always not null.
    }

    // get result success
    @Test
    @DisplayName("Test for Successful Run of a Report")
    public void runReportWithValidId() {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        Report report = Report.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        reportRepository.save(report);

        List<Object[]> results = reportService.getResultForQuery(report.getId());

        Assertions.assertNotNull(results);    // If report exists and sql is valid, result will not be null
    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Non-existing ID")
    public void runReportWithInvalidId() {
        List<Object[]> results = reportService.getResultForQuery(10000000L);

        Assertions.assertNull(results);    // If report doesn't exist, result will be null
    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Invalid SQL")
    public void runReportOfInvalidSQL() {
        Report report = Report.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employee")  // No table named employee in database
                .columns("first_name,job_title,salary")
                .build();

        reportRepository.save(report);

        //List<Object[]> results = reportService.getResultForQuery(report.getId());  // It will throw an exception due to invalid SQL

        Assertions.assertThrows(SQLGrammarException.class, () -> reportService.getResultForQuery(report.getId()));

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Malformed SQL")
    public void runReportOfMalformedSQL() {
        Report report = Report.builder().reportName("All Employees")
                .query("slect first_name, job_title, salary from employees")  // No statement named slect in SQL
                .columns("first_name,job_title,salary")
                .build();

        reportRepository.save(report);

        //List<Object[]> results = reportService.getResultForQuery(report.getId());  // It will throw an exception due to malformed SQL

        Assertions.assertThrows(GenericJDBCException.class, () -> reportService.getResultForQuery(report.getId()));

    }

    // edit success
    // edit fail
    // delete success
    // delete fail
}
