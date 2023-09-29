package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.entity.ReportUpdateHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UpdateHistoryRepository extends JpaRepository<ReportUpdateHistory, Long> {
    List<ReportUpdateHistory> findAllByReportId(long id);
}
