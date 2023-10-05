package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.UpdateHistoryDTO;
import com.imambiplob.databasereport.entity.ReportUpdateHistory;
import com.imambiplob.databasereport.event.ReportUpdateEventForHistory;
import com.imambiplob.databasereport.repository.UpdateHistoryRepository;
import com.imambiplob.databasereport.util.Converter;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UpdateHistoryService {
    private final UpdateHistoryRepository updateHistoryRepository;

    public UpdateHistoryService(UpdateHistoryRepository updateHistoryRepository) {
        this.updateHistoryRepository = updateHistoryRepository;
    }

    @EventListener
    void handleReportUpdate(ReportUpdateEventForHistory event) {

        ReportUpdateHistory history = new ReportUpdateHistory();

        history.setUpdatedBy(event.getUser());
        history.setReport(event.getReport());
        history.setPreviousReportName(event.getReport().getReportName());
        history.setUpdatedReportName(event.getReportDTO().getReportName());
        history.setPreviousSqlQuery(event.getReport().getQuery());
        history.setUpdatedSqlQuery(event.getReportDTO().getQuery());
        history.setPreviousScheduledStatus(event.getReport().isScheduled());
        history.setUpdatedScheduledStatus(event.getReportDTO().isScheduled());

        Map<String, String> previousParamsMap = new HashMap<>();
        for (String paramName : event.getReport().getParamsMap().keySet()) {
            previousParamsMap.put(paramName, event.getReport().getParamsMap().get(paramName));
        }
        history.setPreviousParamsMap(previousParamsMap);

        Map<String, String> updatedParamsMap = new HashMap<>();
        if(event.getReportDTO().getParamsMap() != null) {
            for (String paramName : event.getReportDTO().getParamsMap().keySet()) {
                updatedParamsMap.put(paramName, event.getReportDTO().getParamsMap().get(paramName));
            }
        }
        history.setUpdatedParamsMap(updatedParamsMap);

        updateHistoryRepository.save(history);

    }

    public List<UpdateHistoryDTO> getHistoryOfReport(long reportId) {
        return updateHistoryRepository.findAllByReportId(reportId)
                .stream()
                .map(Converter::convertUpdateHistoryToUpdateHistoryDTO)
                .toList();
    }

    public UpdateHistoryDTO getHistory(long id) {
        return updateHistoryRepository.findById(id).map(Converter::convertUpdateHistoryToUpdateHistoryDTO).orElse(null);
    }
}
