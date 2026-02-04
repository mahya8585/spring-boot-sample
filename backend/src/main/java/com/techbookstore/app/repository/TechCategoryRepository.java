package com.techbookstore.app.repository;

import com.techbookstore.app.entity.TechCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for TechCategory entity
 * 技術カテゴリエンティティのリポジトリ
 */
@Repository
public interface TechCategoryRepository extends JpaRepository<TechCategory, Long> {

    /**
     * Find tech category by category code
     */
    Optional<TechCategory> findByCategoryCode(String categoryCode);

    /**
     * Find top-level categories (no parent)
     */
    @Query("SELECT tc FROM TechCategory tc WHERE tc.parent IS NULL ORDER BY tc.displayOrder")
    List<TechCategory> findTopLevelCategories();

    /**
     * Find subcategories by parent
     */
    @Query("SELECT tc FROM TechCategory tc WHERE tc.parent = :parent ORDER BY tc.displayOrder")
    List<TechCategory> findByParent(@Param("parent") TechCategory parent);

    /**
     * Find categories by level
     */
    @Query("SELECT tc FROM TechCategory tc WHERE tc.categoryLevel = :level ORDER BY tc.displayOrder")
    List<TechCategory> findByCategoryLevel(@Param("level") Integer level);

    /**
     * Find categories with books
     */
    @Query("SELECT DISTINCT tc FROM TechCategory tc JOIN tc.bookCategories bc")
    List<TechCategory> findCategoriesWithBooks();
}