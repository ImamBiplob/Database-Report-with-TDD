package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.exception.IllegalQueryException;
import com.imambiplob.databasereport.exception.ReportNotFoundException;
import com.imambiplob.databasereport.service.ReportService;
import jakarta.validation.Valid;
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
    public ResponseEntity<?> addReport(@Valid @RequestBody ReportDTO reportDTO) throws IllegalQueryException {

        if(reportDTO.getQuery().toLowerCase().contains("drop"))
            throw new IllegalQueryException("DON'T YOU DARE DROP THAT!!!");

        return new ResponseEntity<>(reportService.addReport(reportDTO), HttpStatus.CREATED);

    }

    @GetMapping
    public List<ReportDTO> getReports() {

        return reportService.getReports();

    }

    @GetMapping("/sort/{field}")
    private List<ReportDTO> getReportsWithSorting(@PathVariable String field) {

        return reportService.findReportsWithSorting(field);

    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    private List<ReportDTO> getReportsWithPagination(@PathVariable int offset, @PathVariable int pageSize) {

        return reportService.findReportsWithPagination(offset, pageSize);

    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    private List<ReportDTO> getReportsWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {

        return reportService.findReportsWithPaginationAndSorting(offset, pageSize, field);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportById(@PathVariable long id) throws ReportNotFoundException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        return new ResponseEntity<>(reportService.getReportById(id), HttpStatus.OK);

    }

    @GetMapping("/run/{id}")
    public ResponseEntity<?> runReport(@PathVariable long id) throws ReportNotFoundException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        return new ResponseEntity<>(reportService.getResultForQuery(id), HttpStatus.OK);

    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReport(@Valid @RequestBody ReportDTO reportDTO, @PathVariable long id) throws ReportNotFoundException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        return new ResponseEntity<>(reportService.updateReport(reportDTO, id), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable long id) throws ReportNotFoundException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        return new ResponseEntity<>(reportService.deleteReport(id), HttpStatus.OK);

    }

    @DeleteMapping("/deleteAll")
    public ResponseEntity<?> deleteAllReports() {

        return new ResponseEntity<>(reportService.deleteAllReports(), HttpStatus.OK);

    }

}
