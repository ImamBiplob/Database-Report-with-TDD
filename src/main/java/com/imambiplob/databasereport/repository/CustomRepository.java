package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.dto.ReportDTO;
import com.imambiplob.databasereport.service.CsvExportService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

@Repository
public class CustomRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private final CsvExportService csvExportService;

    public CustomRepository(CsvExportService csvExportService) {
        this.csvExportService = csvExportService;
    }

    public List<Object[]> executeQuery(ReportDTO reportDTO) {

        Object[] columns = Arrays.stream(reportDTO.getColumns().split(",")).toArray();
        String filePath = "reports/" + reportDTO.getReportName() + ".csv";

        String sqlQuery = reportDTO.getQuery();
        Query query = entityManager.createNativeQuery(sqlQuery);

        for(String paramName : reportDTO.getParamsMap().keySet()) {
            query.setParameter(paramName, reportDTO.getParamsMap().get(paramName));
        }

        List<Object[]> results = query.getResultList();

        csvExportService.exportQueryResultToCsv(results, filePath, columns);

        return results;
    }
}
