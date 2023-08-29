package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
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
    public ResponseEntity<?> addReport(@RequestBody Report report) {
        if(report.getQuery().toLowerCase().contains("drop"))
            return new ResponseEntity<>("DON'T YOU DARE DROP THAT!!!", HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(reportService.addReport(report), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReport(@PathVariable long id) {
        if(reportService.getReport(id) == null)
            return new ResponseEntity<>("Report with ID: " + id + "doesn't exist", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(reportService.getReport(id), HttpStatus.OK);
    }

    @GetMapping
    public List<ReportDTO> getReports() {
        return reportService.getReports();
    }

    @GetMapping("/run/{id}")
    public ResponseEntity<?> getResult(@PathVariable long id) {
        List<Object[]> resultList = reportService.getResultForQuery(id);
        if(resultList == null)
            return new ResponseEntity<>("Report with ID: " + id + "doesn't exist", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(resultList, HttpStatus.OK);
    }
}
