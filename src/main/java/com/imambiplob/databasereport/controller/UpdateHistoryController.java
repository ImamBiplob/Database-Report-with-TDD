package com.imambiplob.databasereport.controller;

import com.imambiplob.databasereport.dto.ResponseMessage;
import com.imambiplob.databasereport.dto.UpdateHistoryDTO;
import com.imambiplob.databasereport.service.UpdateHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("api/updateHistories")
public class UpdateHistoryController {
    private final UpdateHistoryService updateHistoryService;

    public UpdateHistoryController(UpdateHistoryService updateHistoryService) {
        this.updateHistoryService = updateHistoryService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ResponseEntity<?> getReportUpdateHistoryWithId(@PathVariable long id) {

        UpdateHistoryDTO updateHistoryDTO = updateHistoryService.getHistory(id);

        if(updateHistoryDTO != null)
            return new ResponseEntity<>(updateHistoryDTO, HttpStatus.OK);

        return new ResponseEntity<>(new ResponseMessage("No Update History Found!!!"), HttpStatus.NOT_FOUND);

    }

    @GetMapping("/ofReport/{reportId}")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ResponseEntity<?> getReportUpdateHistoryOfSpecificReport(@PathVariable long reportId) {

        return new ResponseEntity<>(updateHistoryService.getHistoryOfReport(reportId), HttpStatus.OK);

    }

    /* Thymeleaf APIs start from here */

    @GetMapping("/view")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ModelAndView getHistoryView(@RequestParam long reportId) {

        List<UpdateHistoryDTO> history = updateHistoryService.getHistoryOfReport(reportId);
        ModelAndView mav = new ModelAndView("list-update-history");
        mav.addObject("history", history);

        return mav;

    }

    @GetMapping("/view/details")
    @PreAuthorize("hasAnyAuthority('SYS_ROOT')")
    public ModelAndView getHistoryDetails(@RequestParam long id) {

        UpdateHistoryDTO history = updateHistoryService.getHistory(id);
        ModelAndView mav = new ModelAndView("details-update-history");
        mav.addObject("history", history);

        return mav;

    }
}
