package com.imambiplob.databasereport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDTO {

    private long id;

    private String reportExecutorName;

    private String reportName;

    private String sqlQuery;

    private Map<String, String> paramsMap;

    private Date executionTime;

}
