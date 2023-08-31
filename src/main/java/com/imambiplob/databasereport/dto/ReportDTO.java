package com.imambiplob.databasereport.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO {

    private long id;

    @NotBlank
    private String reportName;

    @NotBlank
    private String query;

    private String columns;

    private Map<String, String> paramsMap;

}
