package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.ExecutionHistoryDTO;
import com.imambiplob.databasereport.entity.ReportExecutionHistory;
import com.imambiplob.databasereport.event.ReportExecutionEventForHistory;
import com.imambiplob.databasereport.repository.ExecutionHistoryRepository;
import com.imambiplob.databasereport.util.Converter;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.imambiplob.databasereport.util.Converter.convertHistoryToHistoryDTO;

@Service
public class ExecutionHistoryService {

    private final ExecutionHistoryRepository executionHistoryRepository;

    public ExecutionHistoryService(ExecutionHistoryRepository executionHistoryRepository) {
        this.executionHistoryRepository = executionHistoryRepository;
    }

    @EventListener
    void handleReportExecution(ReportExecutionEventForHistory event) {

        ReportExecutionHistory history = new ReportExecutionHistory();

        history.setReport(event.getReport());
        history.setReportName(event.getReport().getReportName());
        history.setReportExecutor(event.getUser());
        history.setSqlQuery(event.getReport().getQuery());

        Map<String, String> paramsMap = new HashMap<>();
        for (String paramName : event.getReport().getParamsMap().keySet()) {
            paramsMap.put(paramName, event.getReport().getParamsMap().get(paramName));
        }
        history.setParamsMap(paramsMap);

        executionHistoryRepository.save(history);

    }

    public List<ExecutionHistoryDTO> getHistories() {

        return executionHistoryRepository.findAll().stream().map(Converter::convertHistoryToHistoryDTO).toList();

    }

    public List<ExecutionHistoryDTO> findHistoriesWithSorting(String field) {

        return  executionHistoryRepository.findAll(Sort.by(Sort.Direction.ASC, field))
                .stream()
                .map(Converter::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public List<ExecutionHistoryDTO> findHistoriesWithPagination(int offset, int pageSize) {

        return executionHistoryRepository.findAll(PageRequest.of(offset, pageSize))
                .getContent()
                .stream()
                .map(Converter::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public List<ExecutionHistoryDTO> findHistoriesWithPaginationAndSorting(int offset, int pageSize, String field) {

        return executionHistoryRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)))
                .getContent()
                .stream()
                .map(Converter::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public ExecutionHistoryDTO getHistory(long id) {

        if(executionHistoryRepository.findById(id).isPresent())
            return convertHistoryToHistoryDTO(executionHistoryRepository.findById(id).get());

        return null;

    }

    public List<ExecutionHistoryDTO> getHistoryOfReport(long reportId) {

        return executionHistoryRepository.findReportExecutionHistoriesByReportIdIs(reportId)
                .stream().map(Converter::convertHistoryToHistoryDTO)
                .toList();

    }

}
