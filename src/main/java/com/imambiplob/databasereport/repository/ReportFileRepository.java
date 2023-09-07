package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.entity.ReportFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportFileRepository extends JpaRepository<ReportFile, Long> {
}
