package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.entity.ReportFile;
import com.imambiplob.databasereport.event.ReportExecutionEventForFile;
import com.imambiplob.databasereport.repository.ReportFileRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class ReportFileService {

    private final ReportFileRepository reportFileRepository;

    public ReportFileService(ReportFileRepository reportFileRepository) {
        this.reportFileRepository = reportFileRepository;
    }

    @EventListener
    void storeReportFile(ReportExecutionEventForFile event) throws IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(event.getFile().getOriginalFilename()));

        ReportFile reportFile = new ReportFile();
        reportFile.setReport(event.getReport());
        reportFile.setFileName(fileName);
        reportFile.setFileType(event.getFile().getContentType());
        reportFile.setData(event.getFile().getBytes());

        reportFileRepository.save(reportFile);

    }

    public ReportFile getFile(long id) {

        if(reportFileRepository.findById(id).isPresent())
            return reportFileRepository.findById(id).get();

        return null;

    }

    public Stream<ReportFile> getAllFiles() {

        return reportFileRepository.findAll().stream();

    }

}
