package com.imambiplob.databasereport.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
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

    private String columns;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "paramName_paramValue_mapping",
            joinColumns = {@JoinColumn(name = "report_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> paramsMap;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime creationTime = LocalDateTime.now();

    private LocalDateTime lastUpdateTime;

}
