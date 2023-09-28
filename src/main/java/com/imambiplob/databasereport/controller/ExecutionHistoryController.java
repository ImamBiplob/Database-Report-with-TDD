package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ExecutionHistoryDTO;
import com.imambiplob.databasereport.dto.ResponseMessage;
import com.imambiplob.databasereport.service.ExecutionHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("api/histories")
public class ExecutionHistoryController {

    private final ExecutionHistoryService executionHistoryService;

    public ExecutionHistoryController(ExecutionHistoryService executionHistoryService) {
        this.executionHistoryService = executionHistoryService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ResponseEntity<?> getReportExecutionHistories() {

        return new ResponseEntity<>(executionHistoryService.getHistories(), HttpStatus.OK);

    }

    @GetMapping("/sort/{field}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    private List<ExecutionHistoryDTO> getHistoriesWithSorting(@PathVariable String field) {

        return executionHistoryService.findHistoriesWithSorting(field);

    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    private List<ExecutionHistoryDTO> getHistoriesWithPagination(@PathVariable int offset, @PathVariable int pageSize) {

        return executionHistoryService.findHistoriesWithPagination(offset, pageSize);

    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    private List<ExecutionHistoryDTO> getHistoriesWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {

        return executionHistoryService.findHistoriesWithPaginationAndSorting(offset, pageSize, field);

    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ResponseEntity<?> getReportExecutionHistoryWithId(@PathVariable long id) {

        ExecutionHistoryDTO executionHistoryDTO = executionHistoryService.getHistory(id);

        if(executionHistoryDTO != null)
            return new ResponseEntity<>(executionHistoryDTO, HttpStatus.OK);

        return new ResponseEntity<>(new ResponseMessage("No Execution History Found!!!"), HttpStatus.NOT_FOUND);

    }

    @GetMapping("/ofReport/{reportId}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ResponseEntity<?> getReportExecutionHistoryOfSpecificReport(@PathVariable long reportId) {

        return new ResponseEntity<>(executionHistoryService.getHistoryOfReport(reportId), HttpStatus.OK);

    }

    /* Thymeleaf APIs start from here */

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ModelAndView getHistoryView(@RequestParam long reportId) {

        List<ExecutionHistoryDTO> history = executionHistoryService.getHistoryOfReport(reportId);
        ModelAndView mav = new ModelAndView("list-history");
        mav.addObject("history", history);

        return mav;

    }

    @GetMapping("/view/details")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ModelAndView getHistoryDetails(@RequestParam long id) {

        ExecutionHistoryDTO history = executionHistoryService.getHistory(id);
        ModelAndView mav = new ModelAndView("details-history");
        mav.addObject("history", history);

        return mav;

    }

}
