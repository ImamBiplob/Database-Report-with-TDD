package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findReportById(long id);
}
