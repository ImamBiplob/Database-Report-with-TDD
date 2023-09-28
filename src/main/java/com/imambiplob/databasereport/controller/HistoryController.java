package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.HistoryDTO;
import com.imambiplob.databasereport.dto.ResponseMessage;
import com.imambiplob.databasereport.service.HistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("api/histories")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ResponseEntity<?> getReportExecutionHistories() {

        return new ResponseEntity<>(historyService.getHistories(), HttpStatus.OK);

    }

    @GetMapping("/sort/{field}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    private List<HistoryDTO> getHistoriesWithSorting(@PathVariable String field) {

        return historyService.findHistoriesWithSorting(field);

    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    private List<HistoryDTO> getHistoriesWithPagination(@PathVariable int offset, @PathVariable int pageSize) {

        return historyService.findHistoriesWithPagination(offset, pageSize);

    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    private List<HistoryDTO> getHistoriesWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {

        return historyService.findHistoriesWithPaginationAndSorting(offset, pageSize, field);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ResponseEntity<?> getReportExecutionHistoryWithId(@PathVariable long id) {

        HistoryDTO historyDTO = historyService.getHistory(id);

        if(historyDTO != null)
            return new ResponseEntity<>(historyDTO, HttpStatus.OK);

        return new ResponseEntity<>(new ResponseMessage("No Execution History Found!!!"), HttpStatus.NOT_FOUND);

    }

    @GetMapping("/ofReport/{reportId}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ResponseEntity<?> getReportExecutionHistoryOfSpecificReport(@PathVariable long reportId) {

        return new ResponseEntity<>(historyService.getHistoryOfReport(reportId), HttpStatus.OK);

    }

    /* Thymeleaf APIs start from here */

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ModelAndView getHistoryView(@RequestParam long reportId) {

        List<HistoryDTO> history = historyService.getHistoryOfReport(reportId);
        ModelAndView mav = new ModelAndView("list-history");
        mav.addObject("history", history);

        return mav;

    }

    @GetMapping("/view/details")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ModelAndView getHistoryDetails(@RequestParam long id) {

        HistoryDTO history = historyService.getHistory(id);
        ModelAndView mav = new ModelAndView("details-history");
        mav.addObject("history", history);

        return mav;

    }

}
