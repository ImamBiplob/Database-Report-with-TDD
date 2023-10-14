package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.*;
import com.imambiplob.databasereport.exception.IllegalQueryException;
import com.imambiplob.databasereport.exception.ReportNotFoundException;
import com.imambiplob.databasereport.security.JwtAuthFilter;
import com.imambiplob.databasereport.service.ReportService;
import com.imambiplob.databasereport.service.ScheduledReportService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static com.imambiplob.databasereport.util.Converter.*;

@Controller
//@RestController
@RequestMapping("api/reports")
public class ReportController {

    private final ReportService reportService;
    private final ScheduledReportService scheduledReportService;
    private final JwtAuthFilter jwtAuthFilter;

    public ReportController(ReportService reportService, ScheduledReportService scheduledReportService, JwtAuthFilter jwtAuthFilter) {
        this.reportService = reportService;
        this.scheduledReportService = scheduledReportService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @PostMapping("/saveReport")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public String saveReport(@ModelAttribute ReportView reportView) {

        if(!reportView.isScheduled()) {
            ReportDTO reportDTO = convertReportViewToReportDTO(reportView);
            reportService.addReport(reportDTO, jwtAuthFilter.getCurrentUser());
        }

        else {
            ScheduledReportDTO reportDTO = convertReportViewToScheduledReportDTO(reportView);
            scheduledReportService.addReport(reportDTO, jwtAuthFilter.getCurrentUser());
        }

        return "redirect:/api/reports/view";

    }

    @PostMapping("/updateReport")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public String updateReport(@ModelAttribute ReportView reportView) {

        if(!reportView.isScheduled()) {
            ReportDTO reportDTO = convertReportViewToReportDTO(reportView);
            reportService.updateReport(reportDTO, reportDTO.getId(), jwtAuthFilter.getCurrentUser());
        }

        else {
            ScheduledReportDTO reportDTO = convertReportViewToScheduledReportDTO(reportView);
            scheduledReportService.updateReport(reportDTO, reportDTO.getId(), jwtAuthFilter.getCurrentUser());
        }

        return "redirect:/api/reports/view/editReportForm/" + reportView.getId();

    }

    @PostMapping("/updateParam")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER','USER')")
    public String updateParam(@ModelAttribute ReportView reportView) {

        if(!reportView.isScheduled()) {
            ReportDTO reportDTO = convertReportViewToReportDTO(reportView);
            reportService.updateReport(reportDTO, reportDTO.getId(), jwtAuthFilter.getCurrentUser());
        }

        else {
            ScheduledReportDTO reportDTO = convertReportViewToScheduledReportDTO(reportView);
            scheduledReportService.updateReport(reportDTO, reportDTO.getId(), jwtAuthFilter.getCurrentUser());
        }

        return "redirect:/api/reports/view/editParamForm/" + reportView.getId();

    }

    @GetMapping("/view")
    public ModelAndView getReportsView() {

        List<ReportDTO> reports = reportService.getReports();
        ModelAndView mav = new ModelAndView("list-reports");
        mav.addObject("reports", reports.stream().filter(r -> !r.isScheduled()).toList());

        return mav;

    }

    @GetMapping("/scheduled/view")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ModelAndView getScheduledReportsView() {

        List<ReportDTO> reports = reportService.getReports();
        ModelAndView mav = new ModelAndView("list-scheduled-reports");
        mav.addObject("scheduledReports", reports.stream().filter(ReportDTO::isScheduled).toList());

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

    @GetMapping("/view/editReportForm/{reportId}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ModelAndView editReportForm(@PathVariable long reportId) throws ReportNotFoundException {

        if(reportService.getReportById(reportId) == null)
            throw new ReportNotFoundException("Report with ID: " + reportId + " doesn't exist");

        ModelAndView mav = new ModelAndView("edit-report-form");

        ReportView report;
        if(!reportService.getReportById(reportId).isScheduled()) {
            report = convertReportDTOToReportView(reportService.getReportById(reportId));
        }
        else {
            report = convertScheduledReportDTOToReportView(scheduledReportService.getReportById(reportId));
        }

        if(report.getParamsList().isEmpty())
            report.setParamsList(List.of(new ParamDTO()));

        mav.addObject("report", report);

        return mav;

    }

    @GetMapping("/view/editParamForm/{reportId}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER','USER')")
    public ModelAndView editParamForm(@PathVariable long reportId) throws ReportNotFoundException {

        if(reportService.getReportById(reportId) == null)
            throw new ReportNotFoundException("Report with ID: " + reportId + " doesn't exist");

        ModelAndView mav = new ModelAndView("edit-param-form");

        ReportView report;
        if(!reportService.getReportById(reportId).isScheduled()) {
            report = convertReportDTOToReportView(reportService.getReportById(reportId));
        }
        else {
            report = convertScheduledReportDTOToReportView(scheduledReportService.getReportById(reportId));
        }

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

        RunResult runResult = reportService.runReport(id, jwtAuthFilter.getCurrentUser());

        ModelAndView mav = new ModelAndView("list-run-result");
        if(runResult.getResults().size() > 10000){
            List results = runResult.getResults().subList(0, 10000);
            runResult.setResults(results);
            mav.addObject("runResult", runResult);
        }
        else {
            mav.addObject("runResult", runResult);
        }
        mav.addObject("report", reportService.getReportById(id));

        return mav;

    }

    /* REST APIs Start from Here... */

    @PostMapping
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ResponseEntity<?> addReport(@Valid @RequestBody ReportDTO reportDTO) throws IllegalQueryException {

        if(reportDTO.getQuery().toLowerCase().contains("drop"))
            throw new IllegalQueryException("DON'T YOU DARE DROP THAT!!!");

        return new ResponseEntity<>(reportService.addReport(reportDTO, jwtAuthFilter.getCurrentUser()), HttpStatus.CREATED);

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

        return new ResponseEntity<>(reportService.runReport(id, jwtAuthFilter.getCurrentUser()), HttpStatus.OK);

    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT','DEVELOPER')")
    public ResponseEntity<?> updateReport(@Valid @RequestBody ReportDTO reportDTO, @PathVariable long id) throws ReportNotFoundException, IllegalQueryException {

        if(reportService.getReportById(id) == null)
            throw new ReportNotFoundException("Report with ID: " + id + " doesn't exist");

        if(reportDTO.getQuery().toLowerCase().contains("drop"))
            throw new IllegalQueryException("DON'T YOU DARE DROP THAT!!!");

        return new ResponseEntity<>(reportService.updateReport(reportDTO, id, jwtAuthFilter.getCurrentUser()), HttpStatus.OK);

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

    @GetMapping("/searchBy/{title}")
    public List<ReportDTO> searchByTitle(@PathVariable String title,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return reportService.searchByTitle(title, PageRequest.of(page, size).withSort(Sort.by("reportName")));
    }

}