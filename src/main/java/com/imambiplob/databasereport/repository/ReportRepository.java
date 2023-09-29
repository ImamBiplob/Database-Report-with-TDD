package com.imambiplob.databasereport.repository;

import com.imambiplob.databasereport.entity.Report;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findReportById(long id);

    @Query("select r from Report r where r.reportName like concat('%', :title, '%') ")
    List<Report> searchByTitle(@Param("title") String title, Pageable pageable);

}
