package com.imambiplob.databasereport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO implements Serializable {

    private long id;

    @NotBlank(message = "Report Name must not be blank")
    private String reportName;

    @NotBlank(message = "Query must not be blank")
    private String query;

    private String columns;

    private Map<String, String> paramsMap;

    private String reportCreatorName;

    private Date creationTime;

    private Date lastUpdateTime;

    private String downloadLink;

}
