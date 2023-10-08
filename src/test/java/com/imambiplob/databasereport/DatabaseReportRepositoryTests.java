package com.imambiplob.databasereport;

import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.User;
import com.imambiplob.databasereport.repository.ReportRepository;
import com.imambiplob.databasereport.repository.UserRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class DatabaseReportRepositoryTests {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
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
    @DisplayName("Test for Successful Save of a Report")
    public void saveReportWithSuccess() {

        Report report = Report.builder().reportName("All Employees")
                .query("select first_name, gender, hire_date from employees")
                .columns("first_name,gender,hire_date")
                .reportCreator(userRepository.findUserByUsername("admin"))
                .build();

        Report savedReport = reportRepository.save(report);

        Assertions.assertNotNull(savedReport, "Report should be saved");

    }

    @Test
    @DisplayName("Test for Unsuccessful Save of a Report Not Fulfilling Constraint")
    public void saveReportWithFailure() {

        Report report = Report.builder().reportName("Unnamed")
                .reportCreator(userRepository.findUserByUsername("admin"))
                .query("")
                .build();

        Assertions.assertThrows(ConstraintViolationException.class, () -> reportRepository.save(report), "Report should not be saved due to constraint violation");

    }

    @Test
    @DisplayName("Test for Successful Find of a Report with Existing ID")
    public void findReportWithValidId() {

        Report report = Report.builder().reportName("All Employees")
                .query("select first_name, gender, hire_date from employees")
                .columns("first_name,gender,hire_date")
                .reportCreator(userRepository.findUserByUsername("admin"))
                .build();

        Report savedReport = reportRepository.save(report);

        Optional<Report> retrievedReport = reportRepository.findById(savedReport.getId());

        Assertions.assertNotNull(retrievedReport, "Report should be retrieved");

    }

    @Test
    @DisplayName("Test for Unsuccessful Find of a Report with Non-existing ID")
    public void findReportWithInvalidId() {

        Report retrievedReport = reportRepository.findReportById(100000L);

        Assertions.assertNull(retrievedReport, "Report of Id 100000 should not exist");

    }

    @Test
    @DisplayName("Test for Successful Update of a Report")
    public void updateReportWithValidId() {

        Report report = Report.builder().reportName("All Employees")
                .query("select first_name, gender, hire_date from employees")
                .columns("first_name,gender,hire_date")
                .reportCreator(userRepository.findUserByUsername("admin"))
                .build();

        reportRepository.save(report);

        Report savedReport = reportRepository.findReportById(report.getId());

        savedReport.setReportName("All Male Employees");
        savedReport.setQuery("select first_name, gender, hire_date from employees where gender = 'M'");

        Report updatedReport = reportRepository.save(savedReport);

        Assertions.assertEquals(savedReport.getId(), updatedReport.getId());
        Assertions.assertEquals("All Male Employees", updatedReport.getReportName());

    }

    @Test
    @DisplayName("Test for Successful Delete of a Report")
    public void deleteReportWithValidId() {

        Report report = Report.builder().reportName("All Employees")
                .query("select first_name, gender, hire_date from employees")
                .columns("first_name,gender,hire_date")
                .reportCreator(userRepository.findUserByUsername("admin"))
                .build();

        reportRepository.save(report);

        reportRepository.deleteById(report.getId());

        Assertions.assertNull(reportRepository.findReportById(report.getId()), "Report should not exist since it's deleted");

    }

}
