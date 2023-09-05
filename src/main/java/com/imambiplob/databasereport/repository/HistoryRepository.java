package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.entity.ReportExecutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<ReportExecutionHistory, Long> {

    List<ReportExecutionHistory> findReportExecutionHistoriesByReportIdIs(long reportId);

}
