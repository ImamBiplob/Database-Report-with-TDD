package com.imambiplob.databasereport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportFileDTO {

    private long reportId;

    private String fileName;

    private String uri;

    private String filetype;

    private long size;

}
