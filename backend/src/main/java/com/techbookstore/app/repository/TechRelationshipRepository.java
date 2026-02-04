package com.techbookstore.app.repository;

import com.techbookstore.app.entity.TechRelationship;
import com.techbookstore.app.entity.TechCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for TechRelationship entity
 * 技術関連性エンティティのリポジトリ
 */
@Repository
public interface TechRelationshipRepository extends JpaRepository<TechRelationship, Long> {

    /**
     * Find relationships for a primary technology
     */
    @Query("SELECT tr FROM TechRelationship tr WHERE tr.primaryTech = :primaryTech ORDER BY tr.correlationStrength DESC")
    List<TechRelationship> findByPrimaryTech(@Param("primaryTech") TechCategory primaryTech);

    /**
     * Find relationships by type
     */
    @Query("SELECT tr FROM TechRelationship tr WHERE tr.relationshipType = :relationshipType ORDER BY tr.correlationStrength DESC")
    List<TechRelationship> findByRelationshipType(@Param("relationshipType") TechRelationship.RelationshipType relationshipType);

    /**
     * Find strong correlations above threshold
     */
    @Query("SELECT tr FROM TechRelationship tr WHERE ABS(tr.correlationStrength) >= :threshold ORDER BY ABS(tr.correlationStrength) DESC")
    List<TechRelationship> findStrongCorrelations(@Param("threshold") BigDecimal threshold);

    /**
     * Find relationships for specific categories
     */
    @Query("SELECT tr FROM TechRelationship tr WHERE tr.primaryTech.categoryCode = :categoryCode OR tr.relatedTech.categoryCode = :categoryCode")
    List<TechRelationship> findRelationshipsForCategory(@Param("categoryCode") String categoryCode);

    /**
     * Find complementary technologies
     */
    @Query("SELECT tr FROM TechRelationship tr WHERE tr.relationshipType = 'COMPLEMENTARY' AND tr.correlationStrength > 0 ORDER BY tr.correlationStrength DESC")
    List<TechRelationship> findComplementaryTechnologies();

    /**
     * Find competitive technologies
     */
    @Query("SELECT tr FROM TechRelationship tr WHERE tr.relationshipType = 'COMPETITIVE' ORDER BY ABS(tr.correlationStrength) DESC")
    List<TechRelationship> findCompetitiveTechnologies();

    /**
     * Find recent relationships analysis
     */
    @Query("SELECT tr FROM TechRelationship tr WHERE tr.analysisDate >= :fromDate ORDER BY tr.analysisDate DESC")
    List<TechRelationship> findRecentAnalysis(@Param("fromDate") LocalDate fromDate);
}