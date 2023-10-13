package com.imambiplob.databasereport.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledReportDTO extends ReportDTO implements Serializable {

    private boolean scheduled = true;

    private boolean daily;

    private boolean weekly;

    private boolean monthly;

    private boolean yearly;

    private LocalTime time;

    private String emailAddress;

    private String cronExpression;

}
