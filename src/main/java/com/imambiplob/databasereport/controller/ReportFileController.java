package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ReportFileDTO;
import com.imambiplob.databasereport.dto.ResponseMessage;
import com.imambiplob.databasereport.entity.ReportFile;
import com.imambiplob.databasereport.service.ReportFileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/reportFiles")
public class ReportFileController {

    private final ReportFileService reportFileService;

    public ReportFileController(ReportFileService reportFileService) {
        this.reportFileService = reportFileService;
    }

    @GetMapping
    public ResponseEntity<?> getAllReportFiles() {

        List<ReportFileDTO> files = reportFileService.getAllFiles();

        return new ResponseEntity<>(files, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportFileDTO(@PathVariable String id) {

        ReportFileDTO reportFileDTO = reportFileService.getReportFileDTO(id);

        if(reportFileDTO != null)
            return new ResponseEntity<>(reportFileDTO, HttpStatus.OK);

        return new ResponseEntity<>(new ResponseMessage("No Report File Created!!!"), HttpStatus.NOT_FOUND);

    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable String id) {

        ReportFile reportFile = reportFileService.getReportFile(id);

        if(reportFile != null) {

            byte[] fileData = reportFile.getData();

            if (fileData != null) {

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", reportFile.getFileName());

                return new ResponseEntity<>(fileData, headers, HttpStatus.OK);

            } else {
                return new ResponseEntity<>(new ResponseMessage("File Not Found!!!"), HttpStatus.NOT_FOUND);
            }

        }

        else {
            return new ResponseEntity<>(new ResponseMessage("No Report File Created!!!"), HttpStatus.NOT_FOUND);
        }

    }

}
