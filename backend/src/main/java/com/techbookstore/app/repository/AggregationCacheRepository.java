package com.techbookstore.app.repository;

import com.techbookstore.app.entity.AggregationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AggregationCacheRepository extends JpaRepository<AggregationCache, Long> {
    
    /**
     * Find cached aggregation by cache key.
     */
    Optional<AggregationCache> findByCacheKey(String cacheKey);
    
    /**
     * Find cached aggregation by key name (alias for findByCacheKey).
     */
    default Optional<AggregationCache> findByKeyName(String keyName) {
        return findByCacheKey(keyName);
    }
    
    /**
     * Find all cached aggregations by type and date.
     */
    List<AggregationCache> findByAggregationTypeAndAggregationDate(String aggregationType, LocalDate aggregationDate);
    
    /**
     * Find all cached aggregations that are not expired.
     */
    @Query("SELECT ac FROM AggregationCache ac WHERE ac.expiresAt > :currentTime")
    List<AggregationCache> findNonExpiredCache(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Delete expired cache entries.
     */
    @Modifying
    @Query("DELETE FROM AggregationCache ac WHERE ac.expiresAt <= :currentTime")
    void deleteExpiredCache(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * Delete cache entries by aggregation type.
     */
    @Modifying
    @Query("DELETE FROM AggregationCache ac WHERE ac.aggregationType = :aggregationType")
    void deleteByAggregationType(@Param("aggregationType") String aggregationType);
    
    /**
     * Find all cache entries that will expire within the specified time.
     */
    @Query("SELECT ac FROM AggregationCache ac WHERE ac.expiresAt <= :expirationTime")
    List<AggregationCache> findExpiringBefore(@Param("expirationTime") LocalDateTime expirationTime);
}