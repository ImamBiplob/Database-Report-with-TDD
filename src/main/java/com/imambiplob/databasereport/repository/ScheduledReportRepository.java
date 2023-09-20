package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.entity.ScheduledReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduledReportRepository extends JpaRepository<ScheduledReport, Long> {
    ScheduledReport findScheduledReportById(long id);
}
