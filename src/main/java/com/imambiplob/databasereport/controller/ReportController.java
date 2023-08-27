package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.service.ReportService;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public ReportDTO addReport(@RequestBody Report report) {
        return reportService.addReport(report);
    }

    @GetMapping("/{id}")
    public ReportDTO getReport(@PathVariable long id) {
        return reportService.getReport(id);
    }

    @GetMapping
    public List<ReportDTO> getReports() {
        return reportService.getReports();
    }

    @GetMapping("/run/{id}")
    public List<Object[]> getResult(@PathVariable long id) {
        return reportService.getResultForQuery(id);
    }
}
