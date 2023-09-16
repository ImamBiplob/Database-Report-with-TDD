package com.imambiplob.databasereport.util;

import com.imambiplob.databasereport.dto.*;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.entity.ReportExecutionHistory;
import com.imambiplob.databasereport.entity.ReportFile;
import com.imambiplob.databasereport.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {

    public static ReportView convertReportDTOToReportView(ReportDTO reportDTO) {

        ReportView reportView = new ReportView();
        reportView.setId(reportDTO.getId());
        reportView.setReportName(reportDTO.getReportName());
        reportView.setColumns(reportDTO.getColumns());
        reportView.setQuery(reportDTO.getQuery());
        reportView.setReportCreatorName(reportDTO.getReportCreatorName());
        reportView.setCreationTime(reportDTO.getCreationTime());
        reportView.setLastUpdateTime(reportDTO.getLastUpdateTime());
        reportView.setDownloadLink(reportDTO.getDownloadLink());
        List<ParamDTO> paramsList = new ArrayList<>();

        for(String paramName : reportDTO.getParamsMap().keySet()) {
            ParamDTO param = new ParamDTO();
            param.setParamName(paramName);
            param.setParamValue(reportDTO.getParamsMap().get(paramName));
            paramsList.add(param);
        }

        reportView.setParamsList(paramsList);

        return reportView;

    }

    public static ReportDTO convertReportViewToReportDTO(ReportView reportView) {

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(reportView.getId());
        reportDTO.setReportName(reportView.getReportName());
        reportDTO.setQuery(reportView.getQuery());
        reportDTO.setColumns(reportView.getColumns());
        reportDTO.setReportCreatorName(reportView.getReportCreatorName());
        reportDTO.setCreationTime(reportView.getCreationTime());
        reportDTO.setLastUpdateTime(reportView.getLastUpdateTime());
        reportDTO.setDownloadLink(reportView.getDownloadLink());
        Map<String, String> paramsMap = new HashMap<>();

        if(reportView.getParamsList() != null) {
            if(!reportView.getParamsList().isEmpty()) {
                for (ParamDTO paramDTO : reportView.getParamsList()) {
                    paramsMap.put(paramDTO.getParamName(), paramDTO.getParamValue());
                }
            }
        }

        reportDTO.setParamsMap(paramsMap);
        reportDTO.getParamsMap().remove("");

        return reportDTO;

    }

    public static ReportDTO convertReportToReportDTO(Report report) {

        if(report == null) {
            return null;
        }

        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(report.getId());
        reportDTO.setReportName(report.getReportName());
        reportDTO.setQuery(report.getQuery());
        reportDTO.setColumns(report.getColumns());
        reportDTO.setParamsMap(report.getParamsMap());
        reportDTO.setReportCreatorName(report.getReportCreator().getUsername());
        reportDTO.setCreationTime(report.getCreationTime());
        reportDTO.setLastUpdateTime(report.getLastUpdateTime());
        reportDTO.setDownloadLink(report.getDownloadLink());

        return reportDTO;

    }

    public static Report convertReportDTOToReport(ReportDTO reportDTO, User user) {

        if(reportDTO == null) {
            return null;
        }

        Report report = new Report();
        report.setId(reportDTO.getId());
        report.setReportName(reportDTO.getReportName());
        report.setQuery(reportDTO.getQuery());
        report.setColumns(reportDTO.getColumns());
        report.setParamsMap(reportDTO.getParamsMap());
        report.setReportCreator(user);
        report.getParamsMap().remove("");

        return report;

    }

    public static HistoryDTO convertHistoryToHistoryDTO(ReportExecutionHistory history) {

        if(history == null)
            return null;

        HistoryDTO historyDTO = new HistoryDTO();
        historyDTO.setId(history.getId());
        historyDTO.setReportId(history.getReport().getId());
        historyDTO.setReportName(history.getReportName());
        historyDTO.setReportExecutorName(history.getReportExecutor().getUsername());
        historyDTO.setSqlQuery(history.getSqlQuery());
        historyDTO.setParamsMap(history.getParamsMap());
        historyDTO.setExecutionTime(history.getExecutionTime());

        return historyDTO;

    }

    public static ReportFileDTO convertReportFileToReportFileDTO(ReportFile reportFile) {

        String fileDownloadUri = reportFile.getReport().getDownloadLink();

        ReportFileDTO reportFileDTO = new ReportFileDTO();
        reportFileDTO.setFileName(reportFile.getFileName());
        reportFileDTO.setReportId(reportFile.getReport().getId());
        reportFileDTO.setUri(fileDownloadUri);
        reportFileDTO.setFiletype(reportFile.getFileType());
        reportFileDTO.setSize(reportFile.getData().length);

        return reportFileDTO;

    }

}
