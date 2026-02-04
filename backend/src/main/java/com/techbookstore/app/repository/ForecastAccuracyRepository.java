package com.techbookstore.app.repository;

import com.techbookstore.app.entity.ForecastAccuracy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for ForecastAccuracy entity
 * 予測精度エンティティのリポジトリ
 */
@Repository
public interface ForecastAccuracyRepository extends JpaRepository<ForecastAccuracy, Long> {

    /**
     * Find accuracy records by algorithm
     */
    List<ForecastAccuracy> findByAlgorithmOrderByCreatedAtDesc(String algorithm);

    /**
     * Find latest accuracy record for each algorithm
     */
    @Query("SELECT fa FROM ForecastAccuracy fa WHERE fa.createdAt = (SELECT MAX(fa2.createdAt) FROM ForecastAccuracy fa2 WHERE fa2.algorithm = fa.algorithm)")
    List<ForecastAccuracy> findLatestAccuracyForEachAlgorithm();

    /**
     * Find accuracy records within date range
     */
    @Query("SELECT fa FROM ForecastAccuracy fa WHERE fa.evaluationPeriodStart >= :startDate AND fa.evaluationPeriodEnd <= :endDate ORDER BY fa.mape ASC")
    List<ForecastAccuracy> findByEvaluationPeriodBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find most accurate algorithm
     */
    @Query("SELECT fa FROM ForecastAccuracy fa WHERE fa.mape = (SELECT MIN(fa2.mape) FROM ForecastAccuracy fa2 WHERE fa2.evaluationPeriodStart >= :startDate)")
    Optional<ForecastAccuracy> findMostAccurateAlgorithmSince(@Param("startDate") LocalDate startDate);

    /**
     * Delete old accuracy records
     */
    void deleteByCreatedAtBefore(java.time.LocalDateTime cutoffDate);
}