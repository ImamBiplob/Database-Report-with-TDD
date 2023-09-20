package com.imambiplob.databasereport.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(unique = true)
    @NotBlank(message = "Username must not be blank")
    private String username;

    @Column(unique = true)
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;

    private String phone;

    @NotBlank(message = "Role must not be blank")
    private String roles;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reportExecutor")
    private List<ReportExecutionHistory> executionHistoryList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reportCreator")
    private List<Report> reportList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "reportCreator")
    private List<ScheduledReport> scheduledReportList;

}
