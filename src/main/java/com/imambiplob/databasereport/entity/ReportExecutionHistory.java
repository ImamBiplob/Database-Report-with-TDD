package com.imambiplob.databasereport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ReportExecutionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User reportExecutor;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    private String reportName;

    private String sqlQuery;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "history_paramsMapping",
            joinColumns = {@JoinColumn(name = "history_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> paramsMap;

    private Date executionTime = new Date();

}
