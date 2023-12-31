package com.imambiplob.databasereport.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank(message = "Report Name must not be blank")
    private String reportName;

    @NotBlank(message = "Query must not be blank")
    private String query;

    @NotBlank(message = "Columns must not be blank")
    private String columns;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "report_paramsMapping",
            joinColumns = {@JoinColumn(name = "report_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> paramsMap;

    @ManyToOne
    @JoinColumn(name = "creator_user_id")
    @NotNull
    private User reportCreator;

    private Date creationTime = new Date();

    private Date lastUpdateTime;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "report")
    private List<ReportExecutionHistory> executionHistoryList;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "report")
    private List<ReportUpdateHistory> updateHistoryList;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "report")
    private ReportFile reportFile;

    private String downloadLink;

    private boolean scheduled = false;

}
