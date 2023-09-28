package com.imambiplob.databasereport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ReportUpdateHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User updatedBy;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    private String previousReportName;

    private String updatedReportName;

    private String previousSqlQuery;

    private String updatedSqlQuery;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "update_history_previous_paramsMapping",
            joinColumns = {@JoinColumn(name = "history_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> previousParamsMap;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "update_history_updated_paramsMapping",
            joinColumns = {@JoinColumn(name = "history_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> updatedParamsMap;

    private Date updatedAt = new Date();
}
