package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.exception.IllegalQueryException;
import com.imambiplob.databasereport.exception.ReportNotFoundException;
import com.imambiplob.databasereport.service.ReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<?> addReport(@RequestBody Report report) throws IllegalQueryException {
        if(report.getQuery().toLowerCase().contains("drop"))
            throw new IllegalQueryException("DON'T YOU DARE DROP THAT!!!");

        return new ResponseEntity<>(reportService.addReport(report), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(@PathVariable long id) throws ReportNotFoundException {
        if(reportService.getReport(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        return new ResponseEntity<>(reportService.getReport(id), HttpStatus.OK);
    }

    @GetMapping
    public List<ReportDTO> getReports() {
        return reportService.getReports();
    }

    @GetMapping("/run/{id}")
    public ResponseEntity<?> getResult(@PathVariable long id) throws ReportNotFoundException {
        if(reportService.getReport(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        return new ResponseEntity<>(reportService.getResultForQuery(id), HttpStatus.OK);
    }
}
