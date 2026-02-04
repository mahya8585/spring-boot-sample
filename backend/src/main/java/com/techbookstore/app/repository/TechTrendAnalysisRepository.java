package com.techbookstore.app.repository;

import com.techbookstore.app.entity.TechTrendAnalysis;
import com.techbookstore.app.entity.TechCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TechTrendAnalysis entity
 * 技術トレンド分析エンティティのリポジトリ
 */
@Repository
public interface TechTrendAnalysisRepository extends JpaRepository<TechTrendAnalysis, Long> {

    /**
     * Find trend analysis by tech category and analysis date
     */
    Optional<TechTrendAnalysis> findByTechCategoryAndAnalysisDate(TechCategory techCategory, LocalDate analysisDate);

    /**
     * Find latest trend analysis for a tech category
     */
    @Query("SELECT t FROM TechTrendAnalysis t WHERE t.techCategory = :techCategory ORDER BY t.analysisDate DESC")
    List<TechTrendAnalysis> findLatestByTechCategory(@Param("techCategory") TechCategory techCategory);

    /**
     * Find trend analysis within date range
     */
    @Query("SELECT t FROM TechTrendAnalysis t WHERE t.analysisDate BETWEEN :startDate AND :endDate ORDER BY t.analysisDate DESC")
    List<TechTrendAnalysis> findByAnalysisDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find emerging technologies based on lifecycle stage
     */
    @Query("SELECT t FROM TechTrendAnalysis t WHERE t.lifecycleStage = 'EMERGING' AND t.analysisDate >= :fromDate ORDER BY t.emergingScore DESC")
    List<TechTrendAnalysis> findEmergingTechnologies(@Param("fromDate") LocalDate fromDate);

    /**
     * Find declining technologies at risk
     */
    @Query("SELECT t FROM TechTrendAnalysis t WHERE t.lifecycleStage = 'DECLINE' OR t.obsolescenceRisk > :riskThreshold ORDER BY t.obsolescenceRisk DESC")
    List<TechTrendAnalysis> findDecliningTechnologies(@Param("riskThreshold") java.math.BigDecimal riskThreshold);

    /**
     * Find latest analysis for all categories
     */
    @Query("SELECT t FROM TechTrendAnalysis t WHERE t.analysisDate = (SELECT MAX(t2.analysisDate) FROM TechTrendAnalysis t2 WHERE t2.techCategory = t.techCategory)")
    List<TechTrendAnalysis> findLatestAnalysisForAllCategories();

    /**
     * Find trend analysis by category code
     */
    @Query("SELECT t FROM TechTrendAnalysis t WHERE t.techCategory.categoryCode = :categoryCode ORDER BY t.analysisDate DESC")
    List<TechTrendAnalysis> findByTechCategoryCode(@Param("categoryCode") String categoryCode);
}