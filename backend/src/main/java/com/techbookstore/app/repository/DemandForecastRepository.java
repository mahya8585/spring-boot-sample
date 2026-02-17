package com.techbookstore.app.repository;

import com.techbookstore.app.entity.DemandForecast;
import com.techbookstore.app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DemandForecastRepository extends JpaRepository<DemandForecast, Long> {

    /**
     * Find forecasts for a specific book and date range
     */
    @Query("SELECT d FROM DemandForecast d WHERE d.book = :book AND d.forecastDate BETWEEN :startDate AND :endDate ORDER BY d.forecastDate")
    List<DemandForecast> findByBookAndForecastDateBetween(@Param("book") Book book, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find latest forecast for a specific book
     */
    Optional<DemandForecast> findTopByBookOrderByForecastDateDesc(Book book);

    /**
     * Find forecasts by algorithm type
     */
    @Query("SELECT d FROM DemandForecast d WHERE d.algorithm = :algorithm AND d.forecastDate = :forecastDate")
    List<DemandForecast> findByAlgorithmAndForecastDate(@Param("algorithm") String algorithm, @Param("forecastDate") LocalDate forecastDate);

    /**
     * Find forecasts for a specific date
     */
    List<DemandForecast> findByForecastDateOrderByPredictedDemandDesc(LocalDate forecastDate);

    /**
     * Find high confidence forecasts
     */
    @Query("SELECT d FROM DemandForecast d WHERE d.confidence >= :minConfidence AND d.forecastDate = :forecastDate ORDER BY d.confidence DESC")
    List<DemandForecast> findHighConfidenceForecasts(@Param("minConfidence") java.math.BigDecimal minConfidence, @Param("forecastDate") LocalDate forecastDate);

    /**
     * Delete old forecast data
     */
    void deleteByForecastDateBefore(LocalDate cutoffDate);
}