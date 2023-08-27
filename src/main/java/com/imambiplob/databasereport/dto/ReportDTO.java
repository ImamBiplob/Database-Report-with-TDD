package com.imambiplob.databasereport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportDTO {

    private long id;

    private String reportName;

    private String query;

    private String columns;

    private Map<String, String> paramsMap;
}
