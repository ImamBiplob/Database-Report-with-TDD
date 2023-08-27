package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.repository.CustomRepository;
import com.imambiplob.databasereport.repository.ReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final CustomRepository customRepository;

    public ReportService(ReportRepository reportRepository, CustomRepository customRepository) {
        this.reportRepository = reportRepository;
        this.customRepository = customRepository;
    }

    public static ReportDTO convertReportToReportDTO(Report report) {
        ReportDTO reportDTO = new ReportDTO();
        reportDTO.setId(report.getId());
        reportDTO.setReportName(report.getReportName());
        reportDTO.setQuery(report.getQuery());
        reportDTO.setColumns(report.getColumns());
        reportDTO.setParamsMap(report.getParamsMap());

        return reportDTO;
    }

    public ReportDTO addReport(Report report) {
        return convertReportToReportDTO(reportRepository.save(report));
    }

    public ReportDTO getReport(long id) {
        return convertReportToReportDTO(reportRepository.findById(id).get());
    }

    public List<Object[]> getResultForQuery(long id) {
        ReportDTO reportDTO = getReport(id);
        return customRepository.executeQuery(reportDTO);
    }

    public List<ReportDTO> getReports() {

        return reportRepository.findAll().stream()
                .map(ReportService::convertReportToReportDTO)
                .collect(Collectors.toList());
    }
}
