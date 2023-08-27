package com.imambiplob.databasereport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String reportName;

    private String query;

    private String columns;

    @ElementCollection
    @CollectionTable(name = "paramName_paramValue_mapping",
            joinColumns = {@JoinColumn(name = "report_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "param_name")
    @Column(name = "param_value")
    private Map<String, String> paramsMap;

}
