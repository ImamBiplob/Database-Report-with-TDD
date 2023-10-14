package com.imambiplob.databasereport.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ScheduledReport extends Report {

    private boolean scheduled = true;

    private boolean daily;

    private boolean weekly;

    private boolean monthly;

    private String weekDay;

    private boolean yearly;

    private LocalTime time;

    private String emailAddress;

    private String cronExpression;

}
