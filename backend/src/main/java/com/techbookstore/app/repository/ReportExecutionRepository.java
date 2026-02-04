package com.techbookstore.app.repository;

import com.techbookstore.app.entity.ReportExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReportExecutionRepository extends JpaRepository<ReportExecution, Long> {
    
    /**
     * Find all report executions by status.
     */
    List<ReportExecution> findByStatus(ReportExecution.ExecutionStatus status);
    
    /**
     * Find all report executions for a specific report config.
     */
    List<ReportExecution> findByReportConfigIdOrderByExecutedAtDesc(Long reportConfigId);
    
    /**
     * Find all report executions by user.
     */
    List<ReportExecution> findByExecutedByOrderByExecutedAtDesc(String executedBy);
    
    /**
     * Find recent report executions within a time period.
     */
    @Query("SELECT re FROM ReportExecution re WHERE re.executedAt >= :startDate ORDER BY re.executedAt DESC")
    List<ReportExecution> findRecentExecutions(@Param("startDate") LocalDateTime startDate);
    
    /**
     * Find paginated report executions.
     */
    Page<ReportExecution> findAllByOrderByExecutedAtDesc(Pageable pageable);
}