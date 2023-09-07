package com.imambiplob.databasereport.event;

import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReportExecutionEventForHistory extends ApplicationEvent {

    private final Report report;

    private final User user;

    public ReportExecutionEventForHistory(Object source, User user, Report report) {

        super(source);
        this.user = user;
        this.report = report;

    }

}
