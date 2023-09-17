package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ParamDTO;
import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.dto.ReportView;
import com.imambiplob.databasereport.dto.RunResult;
import com.imambiplob.databasereport.exception.IllegalQueryException;
import com.imambiplob.databasereport.exception.ReportNotFoundException;
import com.imambiplob.databasereport.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.imambiplob.databasereport.util.Converter.convertReportDTOToReportView;
import static com.imambiplob.databasereport.util.Converter.convertReportViewToReportDTO;

@Controller
//@RestController
@RequestMapping("api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/saveReport")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public String saveReport(@ModelAttribute ReportView reportView) {

        ReportDTO reportDTO = convertReportViewToReportDTO(reportView);
        reportService.addReport(reportDTO);

        return "redirect:/api/reports/view";

    }

    @PostMapping("/updateReport")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public String updateReport(@ModelAttribute ReportView reportView) {

        ReportDTO reportDTO = convertReportViewToReportDTO(reportView);
        reportService.updateReport(reportDTO, reportDTO.getId());

        return "redirect:/api/reports/view";

    }

    @GetMapping("/view")
    public ModelAndView getReportsView() {

        List<ReportDTO> reports = reportService.getReports();
        ModelAndView mav = new ModelAndView("list-reports");
        mav.addObject("reports", reports);

        return mav;

    }

    @GetMapping("/view/addReportForm")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ModelAndView addReportForm() {

        ModelAndView mav = new ModelAndView("add-report-form");
        ReportView newReport = new ReportView();
        newReport.setParamsList(List.of(new ParamDTO()));
        mav.addObject("report", newReport);

        return mav;

    }

    @GetMapping("/view/editReportForm")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ModelAndView editReportForm(@RequestParam long reportId) throws ReportNotFoundException {

        if(reportService.getReportById(reportId) == null)
            throw new ReportNotFoundException("Report with ID: " + reportId + " doesn't exist");

        ModelAndView mav = new ModelAndView("edit-report-form");
        ReportView report = convertReportDTOToReportView(reportService.getReportById(reportId));
        if(report.getParamsList().isEmpty())
            report.setParamsList(List.of(new ParamDTO()));
        mav.addObject("report", report);

        return mav;

    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public String deleteReportById(@PathVariable long id) throws ReportNotFoundException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        reportService.deleteReport(id);

        return "redirect:/api/reports/view";

    }

    @GetMapping("/view/runResult/{id}")
    public ModelAndView runAndView(@PathVariable long id) throws ReportNotFoundException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        RunResult runResult = reportService.runReport(id);

        ModelAndView mav = new ModelAndView("list-run-result");
        mav.addObject("runResult", runResult);
        mav.addObject("report", reportService.getReportById(id));

        return mav;

    }

    /* REST APIs Start from Here... */

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
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

        return new ResponseEntity<>(reportService.runReport(id), HttpStatus.OK);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ResponseEntity<?> updateReport(@Valid @RequestBody ReportDTO reportDTO, @PathVariable long id) throws ReportNotFoundException, IllegalQueryException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        if(reportDTO.getQuery().toLowerCase().contains("drop"))
            throw new IllegalQueryException("DON'T YOU DARE DROP THAT!!!");

        return new ResponseEntity<>(reportService.updateReport(reportDTO, id), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public ResponseEntity<?> deleteReport(@PathVariable long id) throws ReportNotFoundException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        return new ResponseEntity<>(reportService.deleteReport(id), HttpStatus.OK);

    }

    @DeleteMapping("/deleteAll")
    @PreAuthorize("hasAuthority('SYS_ROOT')")
    public ResponseEntity<?> deleteAllReports() {

        return new ResponseEntity<>(reportService.deleteAllReports(), HttpStatus.OK);

    }

}
