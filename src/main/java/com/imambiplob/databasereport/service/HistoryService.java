package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.HistoryDTO;
import com.imambiplob.databasereport.entity.ReportExecutionHistory;
import com.imambiplob.databasereport.event.ReportExecutionEvent;
import com.imambiplob.databasereport.repository.HistoryRepository;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public static HistoryDTO convertHistoryToHistoryDTO(ReportExecutionHistory history) {

        if(history == null)
            return null;

        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setId(history.getId());
        historyDTO.setReportName(history.getReport().getReportName());
        historyDTO.setReportExecutorName(history.getReportExecutor().getUsername());
        historyDTO.setSqlQuery(history.getSqlQuery());
        historyDTO.setParamsMap(history.getParamsMap());
        historyDTO.setExecutionTime(history.getExecutionTime());

        return historyDTO;

    }

    @EventListener
    void handleReportExecution(ReportExecutionEvent event) {

        ReportExecutionHistory history = new ReportExecutionHistory();

        history.setReport(event.getReport());
        history.setReportExecutor(event.getUser());
        history.setSqlQuery(event.getReport().getQuery());

        Map<String, String> paramsMap = new HashMap<>();
        for (String paramName : event.getReport().getParamsMap().keySet()) {
            paramsMap.put(paramName, event.getReport().getParamsMap().get(paramName));
        }
        history.setParamsMap(paramsMap);

        historyRepository.save(history);

    }

    public List<HistoryDTO> getHistories() {

        return historyRepository.findAll().stream().map(HistoryService::convertHistoryToHistoryDTO).toList();

    }

    public List<HistoryDTO> findHistoriesWithSorting(String field) {

        return  historyRepository.findAll(Sort.by(Sort.Direction.ASC, field)).stream()
                .map(HistoryService::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public List<HistoryDTO> findHistoriesWithPagination(int offset, int pageSize) {

        return historyRepository.findAll(PageRequest.of(offset, pageSize))
                .getContent().stream()
                .map(HistoryService::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public List<HistoryDTO> findHistoriesWithPaginationAndSorting(int offset, int pageSize, String field) {

        return historyRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)))
                .getContent().stream()
                .map(HistoryService::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public HistoryDTO getHistory(long id) {

        if(historyRepository.findById(id).isPresent())
            return convertHistoryToHistoryDTO(historyRepository.findById(id).get());

        return null;

    }

}
