package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.entity.Report;
import com.imambiplob.databasereport.repository.ReportRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @PersistenceContext
    private EntityManager entityManager;
    private final ReportRepository reportRepository;
    private final CsvExportService csvExportService;

    public ReportService(ReportRepository reportRepository, CsvExportService csvExportService) {
        this.reportRepository = reportRepository;
        this.csvExportService = csvExportService;
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

        return reportDTO;
    }

    @Transactional
    public ReportDTO addReport(Report report) {
        return convertReportToReportDTO(reportRepository.save(report));
    }

    public ReportDTO getReport(long id) {
        return convertReportToReportDTO(reportRepository.findReportById(id));
    }

    public List<ReportDTO> getReports() {
        return reportRepository.findAll().stream()
                .map(ReportService::convertReportToReportDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Object[]> getResultForQuery(long id) {
        Report report = reportRepository.findReportById(id);

        if(report == null)
            return null;     // Not found

        Object[] columns = Arrays.stream(report.getColumns().split(",")).toArray();
        String filePath = "reports/" + report.getReportName() + ".csv";

        String sqlQuery = report.getQuery();
        Query query = entityManager.createNativeQuery(sqlQuery);

        for (String paramName : report.getParamsMap().keySet()) {
            query.setParameter(paramName, report.getParamsMap().get(paramName));
        }

        List<Object[]> results = query.getResultList();

        csvExportService.exportQueryResultToCsv(results, filePath, columns);

        return results;
    }
}
