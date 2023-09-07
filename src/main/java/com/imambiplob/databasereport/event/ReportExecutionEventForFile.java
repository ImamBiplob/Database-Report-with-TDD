package com.imambiplob.databasereport.event;

import com.imambiplob.databasereport.entity.Report;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class ReportExecutionEventForFile extends ApplicationEvent {

    private final Report report;

    private final MultipartFile file;

    public ReportExecutionEventForFile(Object source, MultipartFile file, Report report) {

        super(source);
        this.file = file;
        this.report = report;

    }

}