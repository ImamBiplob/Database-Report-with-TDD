package com.imambiplob.databasereport.event;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReportUpdateEventForHistory extends ApplicationEvent {
    private final Report report;

    private final ReportDTO reportDTO;

    private final User user;

    public ReportUpdateEventForHistory(Object source, Report report, ReportDTO reportDTO, User user) {

        super(source);
        this.user = user;
        this.report = report;
        this.reportDTO = reportDTO;

    }
}