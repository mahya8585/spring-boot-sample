package com.techbookstore.app.repository;

import com.techbookstore.app.entity.TechPrediction;
import com.techbookstore.app.entity.TechCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for TechPrediction entity
 * 技術予測エンティティのリポジトリ
 */
@Repository
public interface TechPredictionRepository extends JpaRepository<TechPrediction, Long> {

    /**
     * Find predictions for a specific tech category
     */
    @Query("SELECT tp FROM TechPrediction tp WHERE tp.techCategory = :techCategory ORDER BY tp.predictionForDate ASC")
    List<TechPrediction> findByTechCategory(@Param("techCategory") TechCategory techCategory);

    /**
     * Find predictions for a date range
     */
    @Query("SELECT tp FROM TechPrediction tp WHERE tp.predictionForDate BETWEEN :startDate AND :endDate ORDER BY tp.predictionForDate ASC")
    List<TechPrediction> findByPredictionForDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find latest predictions for all categories
     */
    @Query("SELECT tp FROM TechPrediction tp WHERE tp.predictionDate = (SELECT MAX(tp2.predictionDate) FROM TechPrediction tp2 WHERE tp2.techCategory = tp.techCategory)")
    List<TechPrediction> findLatestPredictionsForAllCategories();

    /**
     * Find predictions by category code
     */
    @Query("SELECT tp FROM TechPrediction tp WHERE tp.techCategory.categoryCode = :categoryCode ORDER BY tp.predictionForDate ASC")
    List<TechPrediction> findByTechCategoryCode(@Param("categoryCode") String categoryCode);

    /**
     * Find predictions by model type
     */
    @Query("SELECT tp FROM TechPrediction tp WHERE tp.predictionModel = :model ORDER BY tp.predictionForDate DESC")
    List<TechPrediction> findByPredictionModel(@Param("model") String model);

    /**
     * Find accurate predictions
     */
    @Query("SELECT tp FROM TechPrediction tp WHERE tp.modelAccuracy >= :accuracyThreshold ORDER BY tp.modelAccuracy DESC")
    List<TechPrediction> findAccuratePredictions(@Param("accuracyThreshold") java.math.BigDecimal accuracyThreshold);

    /**
     * Find future predictions
     */
    @Query("SELECT tp FROM TechPrediction tp WHERE tp.predictionForDate > :currentDate ORDER BY tp.predictionForDate ASC")
    List<TechPrediction> findFuturePredictions(@Param("currentDate") LocalDate currentDate);
}