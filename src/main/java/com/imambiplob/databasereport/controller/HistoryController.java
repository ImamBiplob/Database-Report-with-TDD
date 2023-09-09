package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.HistoryDTO;
import com.imambiplob.databasereport.dto.ResponseMessage;
import com.imambiplob.databasereport.service.HistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/histories")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<?> getReportExecutionHistories() {

        return new ResponseEntity<>(historyService.getHistories(), HttpStatus.OK);

    }

    @GetMapping("/sort/{field}")
    private List<HistoryDTO> getHistoriesWithSorting(@PathVariable String field) {

        return historyService.findHistoriesWithSorting(field);

    }

    @GetMapping("/pagination/{offset}/{pageSize}")
    private List<HistoryDTO> getHistoriesWithPagination(@PathVariable int offset, @PathVariable int pageSize) {

        return historyService.findHistoriesWithPagination(offset, pageSize);

    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    private List<HistoryDTO> getHistoriesWithPaginationAndSorting(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field) {

        return historyService.findHistoriesWithPaginationAndSorting(offset, pageSize, field);

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getReportExecutionHistoryWithId(@PathVariable long id) {

        HistoryDTO historyDTO = historyService.getHistory(id);

        if(historyDTO != null)
            return new ResponseEntity<>(historyDTO, HttpStatus.OK);

        return new ResponseEntity<>(new ResponseMessage("No Execution History Found!!!"), HttpStatus.NOT_FOUND);

    }

    @GetMapping("/ofReport/{reportId}")
    public ResponseEntity<?> getReportExecutionHistoryOfSpecificReport(@PathVariable long reportId) {

        return new ResponseEntity<>(historyService.getHistoryOfReport(reportId), HttpStatus.OK);

    }

}
