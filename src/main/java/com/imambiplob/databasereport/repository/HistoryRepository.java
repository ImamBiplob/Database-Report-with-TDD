package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.entity.ReportExecutionHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<ReportExecutionHistory, Long> {
}
