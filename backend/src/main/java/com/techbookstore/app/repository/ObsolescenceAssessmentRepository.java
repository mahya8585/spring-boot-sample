package com.techbookstore.app.repository;

import com.techbookstore.app.entity.ObsolescenceAssessment;
import com.techbookstore.app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ObsolescenceAssessmentRepository extends JpaRepository<ObsolescenceAssessment, Long> {

    /**
     * Find latest assessment for a specific book
     */
    Optional<ObsolescenceAssessment> findTopByBookOrderByAssessmentDateDesc(Book book);

    /**
     * Find assessments by risk level
     */
    @Query("SELECT o FROM ObsolescenceAssessment o WHERE o.riskLevel = :riskLevel AND o.assessmentDate = :assessmentDate ORDER BY o.riskScore DESC")
    List<ObsolescenceAssessment> findByRiskLevelAndAssessmentDate(@Param("riskLevel") String riskLevel, @Param("assessmentDate") LocalDate assessmentDate);

    /**
     * Find high risk items requiring immediate action
     */
    @Query("SELECT o FROM ObsolescenceAssessment o WHERE o.riskLevel = 'HIGH' AND o.monthsToObsolescence <= :months ORDER BY o.riskScore DESC")
    List<ObsolescenceAssessment> findHighRiskItemsWithinMonths(@Param("months") Integer months);

    /**
     * Find assessments by date range
     */
    @Query("SELECT o FROM ObsolescenceAssessment o WHERE o.assessmentDate BETWEEN :startDate AND :endDate ORDER BY o.assessmentDate DESC")
    List<ObsolescenceAssessment> findByAssessmentDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find all assessments for a specific date
     */
    List<ObsolescenceAssessment> findByAssessmentDateOrderByRiskScoreDesc(LocalDate assessmentDate);

    /**
     * Delete old assessment data
     */
    void deleteByAssessmentDateBefore(LocalDate cutoffDate);
}