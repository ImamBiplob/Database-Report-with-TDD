package com.imambiplob.databasereport.event;

import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ReportExecutionEvent extends ApplicationEvent {

    private final User user;

    private final Report report;

    public ReportExecutionEvent(Object source, User user, Report report) {

        super(source);
        this.user = user;
        this.report = report;

    }

}
