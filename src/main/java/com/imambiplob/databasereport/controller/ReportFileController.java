package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ReportFileDTO;
import com.imambiplob.databasereport.entity.ReportFile;
import com.imambiplob.databasereport.service.ReportFileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/reportFiles")
public class ReportFileController {

    private final ReportFileService reportFileService;

    public ReportFileController(ReportFileService reportFileService) {
        this.reportFileService = reportFileService;
    }

    public static ReportFileDTO convertReportFileToReportFileDTO(ReportFile reportFile) {

        String fileDownloadUri = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/files/")
                .path(reportFile.getFileName())
                .toUriString();

        ReportFileDTO reportFileDTO = new ReportFileDTO();
        reportFileDTO.setFileName(reportFile.getFileName());
        reportFileDTO.setReportId(reportFile.getReport().getId());
        reportFileDTO.setUri(fileDownloadUri);
        reportFileDTO.setFiletype(reportFile.getFileType());
        reportFileDTO.setSize(reportFile.getData().length);

        return reportFileDTO;

    }

    @GetMapping
    public ResponseEntity<?> getAllReportFiles() {

        List<ReportFileDTO> files = reportFileService.getAllFiles().map(ReportFileController::convertReportFileToReportFileDTO).toList();

        return new ResponseEntity<>(files, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportFile(@PathVariable long id) {

        ReportFileDTO reportFileDTO = convertReportFileToReportFileDTO(reportFileService.getFile(id));

        return new ResponseEntity<>(reportFileDTO, HttpStatus.OK);

    }

}
