package com.imambiplob.databasereport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHistoryDTO implements Serializable {
    private long id;

    private long reportId;

    private String updatedBy;

    private Date updatedAt;

    private String previousReportName;

    private String updatedReportName;

    private String previousSqlQuery;

    private String updatedSqlQuery;

    private boolean previousScheduledStatus;

    private boolean updatedScheduledStatus;

    private Map<String, String> previousParamsMap;

    private Map<String, String> updatedParamsMap;
}
