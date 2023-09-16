package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.HistoryDTO;
import com.imambiplob.databasereport.entity.ReportExecutionHistory;
import com.imambiplob.databasereport.event.ReportExecutionEventForHistory;
import com.imambiplob.databasereport.repository.HistoryRepository;
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
public class HistoryService {

    private final HistoryRepository historyRepository;

    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
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

        historyRepository.save(history);

    }

    public List<HistoryDTO> getHistories() {

        return historyRepository.findAll().stream().map(Converter::convertHistoryToHistoryDTO).toList();

    }

    public List<HistoryDTO> findHistoriesWithSorting(String field) {

        return  historyRepository.findAll(Sort.by(Sort.Direction.ASC, field))
                .stream()
                .map(Converter::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public List<HistoryDTO> findHistoriesWithPagination(int offset, int pageSize) {

        return historyRepository.findAll(PageRequest.of(offset, pageSize))
                .getContent()
                .stream()
                .map(Converter::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public List<HistoryDTO> findHistoriesWithPaginationAndSorting(int offset, int pageSize, String field) {

        return historyRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)))
                .getContent()
                .stream()
                .map(Converter::convertHistoryToHistoryDTO)
                .collect(Collectors.toList());

    }

    public HistoryDTO getHistory(long id) {

        if(historyRepository.findById(id).isPresent())
            return convertHistoryToHistoryDTO(historyRepository.findById(id).get());

        return null;

    }

    public List<HistoryDTO> getHistoryOfReport(long reportId) {

        return historyRepository.findReportExecutionHistoriesByReportIdIs(reportId)
                .stream().map(Converter::convertHistoryToHistoryDTO)
                .toList();

    }

}
