package com.techbookstore.app.repository;

import com.techbookstore.app.entity.ReportConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportConfigRepository extends JpaRepository<ReportConfig, Long> {
    
    /**
     * Find all report configurations by report type.
     */
    List<ReportConfig> findByReportType(ReportConfig.ReportType reportType);
    
    /**
     * Find report configuration by report name.
     */
    Optional<ReportConfig> findByReportName(String reportName);
    
    /**
     * Find all report configurations ordered by report type and creation date.
     */
    @Query("SELECT rc FROM ReportConfig rc ORDER BY rc.reportType, rc.createdAt DESC")
    List<ReportConfig> findAllOrderedByTypeAndDate();
}