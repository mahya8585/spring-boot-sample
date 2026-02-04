package com.techbookstore.app.repository;

import com.techbookstore.app.entity.OptimalStockSettings;
import com.techbookstore.app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OptimalStockSettings entity
 * 最適在庫設定エンティティのリポジトリ
 */
@Repository
public interface OptimalStockSettingsRepository extends JpaRepository<OptimalStockSettings, Long> {

    /**
     * Find current optimal stock settings for a book
     */
    @Query("SELECT oss FROM OptimalStockSettings oss WHERE oss.book = :book AND oss.validFrom <= :currentDate AND (oss.validTo IS NULL OR oss.validTo >= :currentDate)")
    Optional<OptimalStockSettings> findCurrentByBook(@Param("book") Book book, @Param("currentDate") LocalDate currentDate);

    /**
     * Find all current optimal stock settings
     */
    @Query("SELECT oss FROM OptimalStockSettings oss WHERE oss.validFrom <= :currentDate AND (oss.validTo IS NULL OR oss.validTo >= :currentDate)")
    List<OptimalStockSettings> findAllCurrent(@Param("currentDate") LocalDate currentDate);

    /**
     * Find historical settings for a book
     */
    @Query("SELECT oss FROM OptimalStockSettings oss WHERE oss.book = :book ORDER BY oss.validFrom DESC")
    List<OptimalStockSettings> findHistoricalByBook(@Param("book") Book book);

    /**
     * Find settings that need review (near expiry)
     */
    @Query("SELECT oss FROM OptimalStockSettings oss WHERE oss.validTo IS NOT NULL AND oss.validTo BETWEEN :startDate AND :endDate")
    List<OptimalStockSettings> findNeedingReview(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * Find books with low stock levels based on optimal settings
     */
    @Query("SELECT oss FROM OptimalStockSettings oss JOIN Inventory i ON oss.book.id = i.book.id WHERE (i.storeStock + i.warehouseStock) <= oss.reorderPoint AND oss.validFrom <= :currentDate AND (oss.validTo IS NULL OR oss.validTo >= :currentDate)")
    List<OptimalStockSettings> findBooksNeedingReorder(@Param("currentDate") LocalDate currentDate);
}