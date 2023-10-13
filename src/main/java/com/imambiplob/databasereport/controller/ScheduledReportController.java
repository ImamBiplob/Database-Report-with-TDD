package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ScheduledReportDTO;
import com.imambiplob.databasereport.exception.IllegalQueryException;
import com.imambiplob.databasereport.exception.ReportNotFoundException;
import com.imambiplob.databasereport.security.JwtAuthFilter;
import com.imambiplob.databasereport.service.ScheduledReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
//@RestController
@RequestMapping("api/reports/scheduled")
public class ScheduledReportController {

    private final ScheduledReportService reportService;
    private final JwtAuthFilter jwtAuthFilter;

    public ScheduledReportController(ScheduledReportService reportService, JwtAuthFilter jwtAuthFilter) {
        this.reportService = reportService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ResponseEntity<?> addReport(@Valid @RequestBody ScheduledReportDTO reportDTO) throws IllegalQueryException {

        if (reportDTO.getQuery().toLowerCase().contains("drop"))
            throw new IllegalQueryException("DON'T YOU DARE DROP THAT!!!");

        return new ResponseEntity<>(reportService.addReport(reportDTO, jwtAuthFilter.getCurrentUser()), HttpStatus.CREATED);

    }

    @GetMapping
    public List<ScheduledReportDTO> getReports() {

        return reportService.getReports();

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ResponseEntity<?> updateReport(@Valid @RequestBody ScheduledReportDTO reportDTO, @PathVariable long id) throws ReportNotFoundException, IllegalQueryException {

        if (reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        if (reportDTO.getQuery().toLowerCase().contains("drop"))
            throw new IllegalQueryException("DON'T YOU DARE DROP THAT!!!");

        return new ResponseEntity<>(reportService.updateReport(reportDTO, id, jwtAuthFilter.getCurrentUser()), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public ResponseEntity<?> deleteReport(@PathVariable long id) throws ReportNotFoundException {

        if (reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        return new ResponseEntity<>(reportService.deleteReport(id), HttpStatus.OK);

    }

    @GetMapping("/delegateReportsToTaskScheduler")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public String delegateReportsToTaskScheduler() {
        reportService.delegateScheduledReportsToTaskScheduler();

        return "redirect:/api/reports/scheduled/view";
    }

}
