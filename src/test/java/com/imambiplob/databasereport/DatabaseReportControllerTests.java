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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    public void saveReport() throws Exception {

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

        response.andDo(print()).
                andExpect(status().isCreated());

    }

    @Test
    @DisplayName("Test for Successful GET Operation of Reports")
    public void getReports() throws Exception {

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
                .andDo(print());

    }

}
