package com.imambiplob.databasereport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportView implements Serializable {

    private long id;

    @NotBlank(message = "Report Name must not be blank")
    private String reportName;

    @NotBlank(message = "Query must not be blank")
    private String query;

    @NotBlank(message = "Columns must not be blank")
    private String columns;

    private List<ParamDTO> paramsList;

    private String reportCreatorName;

    private Date creationTime;

    private Date lastUpdateTime;

    private String downloadLink;

}
