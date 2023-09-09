package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.ReportFileDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.ReportFile;
import com.imambiplob.databasereport.event.ReportExecutionEventForFile;
import com.imambiplob.databasereport.repository.ReportFileRepository;
import com.imambiplob.databasereport.repository.ReportRepository;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
public class ReportFileService {

    private final ReportFileRepository reportFileRepository;

    private final ReportRepository reportRepository;

    public ReportFileService(ReportFileRepository reportFileRepository, ReportRepository reportRepository) {
        this.reportFileRepository = reportFileRepository;
        this.reportRepository = reportRepository;
    }

    public static ReportFileDTO convertReportFileToReportFileDTO(ReportFile reportFile) {

        String fileDownloadUri = reportFile.getReport().getDownloadLink();

        ReportFileDTO reportFileDTO = new ReportFileDTO();
        reportFileDTO.setFileName(reportFile.getFileName());
        reportFileDTO.setReportId(reportFile.getReport().getId());
        reportFileDTO.setUri(fileDownloadUri);
        reportFileDTO.setFiletype(reportFile.getFileType());
        reportFileDTO.setSize(reportFile.getData().length);

        return reportFileDTO;

    }

    @EventListener
    void storeReportFile(ReportExecutionEventForFile event) throws IOException {

        if(reportFileRepository.findReportFileByReportId(event.getReport().getId()) == null) {

            ReportFile reportFile = new ReportFile();
            reportFile.setReport(event.getReport());
            reportFile.setFileName(StringUtils.cleanPath(Objects.requireNonNull(event.getFile().getOriginalFilename())));
            reportFile.setFileType(event.getFile().getContentType());
            reportFile.setData(event.getFile().getBytes());

            reportFileRepository.save(reportFile);

            Report report = reportFile.getReport();
            report.setDownloadLink(ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("api/reportFiles/download/")
                    .path(reportFile.getId())
                    .toUriString());

            reportRepository.save(report);
        }

        else {

            ReportFile reportFile = reportFileRepository.findReportFileByReportId(event.getReport().getId());

            reportFile.setReport(event.getReport());
            reportFile.setFileName(StringUtils.cleanPath(Objects.requireNonNull(event.getFile().getOriginalFilename())));
            reportFile.setFileType(event.getFile().getContentType());
            reportFile.setData(event.getFile().getBytes());

            reportFileRepository.save(reportFile);

        }



    }

    public ReportFile getReportFile(String id) {

        if(reportFileRepository.findById(id).isPresent())
            return reportFileRepository.findById(id).get();

        return null;

    }

    public ReportFileDTO getFile(String id) {

        if(reportFileRepository.findById(id).isPresent())
            return convertReportFileToReportFileDTO(reportFileRepository.findById(id).get());

        return null;

    }

    public List<ReportFileDTO> getAllFiles() {

        return reportFileRepository.findAll().stream()
                .map(ReportFileService::convertReportFileToReportFileDTO)
                .toList();

    }

}
