package com.imambiplob.databasereport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.repository.ReportRepository;
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
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @BeforeEach
    void setup() {
        reportRepository.deleteAll();
    }

    @Test
    @DisplayName("Test for Successful POST Operation of Report")
    public void saveReportWithSuccess() throws Exception {

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        Report report = Report.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        ResultActions response = mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(report)));

        response.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

    }

    @Test
    @DisplayName("Test for Unsuccessful POST Operation of Report with DROP SQL Statement")
    public void saveReportWithDropStatement() throws Exception {

        Report report = Report.builder().reportName("DROP this fu*king Database")
                .query("Drop database")
                .build();

        ResultActions response = mockMvc.perform(post("/api/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(report)));

        response.andDo(print()).
                andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("Test for Successful GET Operation of Reports")
    public void getReportsWithSuccess() throws Exception {

        Report report1 = Report.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("salary", "100000");

        Report report2 = Report.builder().reportName("All Employees Where Salary is Getter Than 100000")
                .query("select first_name, job_title, salary from employees where salary > :salary")
                .columns("first_name,job_title,salary")
                .paramsMap(paramsMap)
                .build();

        List<Report> reportList = List.of(report1, report2);

        reportRepository.saveAll(reportList);

        ResultActions response = mockMvc.perform(get("/api/reports"));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print());

    }

    // get report success
    @Test
    @DisplayName("Test for Successful GET of a Report")
    public void getReportWithSuccess() throws Exception {
        Report report = Report.builder().reportName("All Employees")
                .query("select first_name, job_title, salary from employees")
                .columns("first_name,job_title,salary")
                .build();

        reportRepository.save(report);

        ResultActions response = mockMvc.perform(get("/api/reports/{id}", report.getId()));

        response.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(jsonPath("$.reportName", is(report.getReportName())));
    }

    // get report fail
    @Test
    @DisplayName("Test for Unsuccessful GET of a Report with Non-existing ID")
    public void getReportWithInvalidId() throws  Exception {
        ResultActions response = mockMvc.perform(get("/api/reports/{id}", 100000L));

        response.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
    }
    // run report success
    // run report fail*3
    // edit success
    // edit fail
    // delete success
    // delete fail

}
