package com.imambiplob.databasereport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.repository.ReportRepository;
import com.imambiplob.databasereport.repository.UserRepository;
import com.imambiplob.databasereport.service.ReportService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class DatabaseReportControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {

        userRepository.deleteAll();
        reportRepository.deleteAll();

        userRepository.save(User.builder()
                .id(1L)
                .username("admin")
                .email("admin@gmail.com")
                .password("admin")
                .phone("01521559190").build());

    }

    @Test
    @DisplayName("Test for Successful POST Operation of Report")
    public void saveReportWithSuccess() throws Exception {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        ReportDTO reportDTO = ReportDTO.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        ResultActions response = mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportDTO)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.creationTime").isNotEmpty())
                .andExpect(jsonPath("$.lastUpdateTime").isEmpty());

    }

    @Test
    @DisplayName("Test for Unsuccessful POST Operation of Report with No SQL Query")
    public void saveReportWithFailure() throws Exception {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        ReportDTO reportDTO = ReportDTO.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("")                                                       /* No SQL Query */
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        ResultActions response = mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportDTO)));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    }

    @Test
    @DisplayName("Test for Unsuccessful POST Operation of Report with DROP SQL Statement")
    public void saveReportWithDropStatement() throws Exception {

        ReportDTO reportDTO = ReportDTO.builder().reportName("DROP this fu*king Database")
                .query("Drop database")
                .build();

        ResultActions response = mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reportDTO)));

        response.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("You Have Given Prohibited Query!!!")))
                .andExpect(jsonPath("$.details", is("DON'T YOU DARE DROP THAT!!!")));

    }

    @Test
    @DisplayName("Test for Successful GET Operation of Reports")
    public void getReportsWithSuccess() throws Exception {

        ReportDTO report1 = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        reportService.addReport(report1);

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        ReportDTO report2 = ReportDTO.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        reportService.addReport(report2);

        ResultActions response = mockMvc.perform(get("/api/reports"));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

    }

    @Test
    @DisplayName("Test for Successful GET of a Report")
    public void getReportWithSuccess() throws Exception {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report);

        ResultActions response = mockMvc.perform(get("/api/reports/{id}", savedReport.getId()));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.reportName", is(report.getReportName())));

    }

    @Test
    @DisplayName("Test for Unsuccessful GET of a Report with Non-existing ID")
    public void getReportWithInvalidId() throws  Exception {

        ResultActions response = mockMvc.perform(get("/api/reports/{id}", 100000L));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    }

    @Test
    @DisplayName("Test for Successful Run of a Report")
    public void runReportWithSuccess() throws Exception {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report);

        ResultActions response = mockMvc.perform(get("/api/reports/run/{id}", savedReport.getId()));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Non-existing ID")
    public void runReportWithInvalidId() throws Exception {

        ResultActions response = mockMvc.perform(get("/api/reports/run/{id}", 1010000L));

        response.andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Invalid SQL")
    public void runReportWithInvalidSQL() throws Exception {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, kob_title, salary from employees")     /* No column named 'kob_title' in employees table */
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report);

        ResultActions response = mockMvc.perform(get("/api/reports/run/{id}", savedReport.getId()));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Invalid SQL!!! Edit Report with Valid SQL and Try Again")));

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Wrong SQL Syntax")
    public void runReportWithWrongSQLSyntax() throws Exception {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary frm employees")     /* Wrong SQl keyword 'frm' */
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report);

        ResultActions response = mockMvc.perform(get("/api/reports/run/{id}", savedReport.getId()));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Invalid SQL!!! Edit Report with Valid SQL and Try Again")));

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Malformed SQL Statement")
    public void runReportWithMalformedSQLStatement() throws Exception {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("slect first_name, job_title, salary from employees")    /* Malformed SQl statement 'slect' */
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report);

        ResultActions response = mockMvc.perform(get("/api/reports/run/{id}", savedReport.getId()));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Malformed SQL Statement!!! Edit Report with Correct SQL Statement and Try Again")));

    }

    @Test
    @DisplayName("Test for Unsuccessful Run of a Report with Parameter Error")
    public void runReportWithMismatchedParameterNames() throws Exception {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salry", "100000");                                       /* salry doesn't match with param salary */

        ReportDTO report = ReportDTO.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        ReportDTO savedReport = reportService.addReport(report);

        ResultActions response = mockMvc.perform(get("/api/reports/run/{id}", savedReport.getId()));

        response.andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.message", is("Parameter Error!!! Edit Report with Valid Parameter Name and Value Before Trying Again")));

    }

    @Test
    @DisplayName("Test  for Successful Update of a Report")
    public void updateReportWithValidId() throws Exception {

        ReportDTO report = ReportDTO.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        ReportDTO savedReport = reportService.addReport(report);

        ReportDTO updatedReport = ReportDTO.builder()
                .reportName("All Junior Executives")
                .query("select first_name, job_title, salary from employees where job_title = \"Junior Executive\"")
                .columns("first_name,job_title,salary")
                .build();

        ResultActions response = mockMvc.perform(put("/api/reports/{id}", savedReport.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedReport)));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.reportName", is("All Junior Executives")))
                .andExpect(jsonPath("$.lastUpdateTime").isNotEmpty());

    }

    @Test
    @DisplayName("Test for Successful Delete of a Report")
    public void deleteReportWithValidId() throws Exception {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        ReportDTO report = ReportDTO.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        ReportDTO toBeDeleted = reportService.addReport(report);

        ResultActions response = mockMvc.perform(delete("/api/reports/{id}", toBeDeleted.getId()));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

        Assertions.assertNull(reportRepository.findReportById(toBeDeleted.getId()), "Report should not exist since it's deleted");

    }

    @Test
    @DisplayName("Test for Successful Delete of All reports")
    public void deleteAllReportsWithSuccess() throws Exception {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        Report report = Report.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        Report toBeDeleted = reportRepository.save(report);

        ResultActions response = mockMvc.perform(delete("/api/reports/deleteAll"));

        response.andExpect(status().isOk())
                .andDo(print());

        Assertions.assertNull(reportRepository.findReportById(toBeDeleted.getId()), "Report should not exist since it's deleted");

    }

}
