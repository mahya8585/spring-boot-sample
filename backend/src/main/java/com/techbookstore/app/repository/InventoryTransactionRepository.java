package com.techbookstore.app.repository;

import com.techbookstore.app.entity.InventoryTransaction;
import com.techbookstore.app.entity.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Long> {

    List<InventoryTransaction> findByInventoryIdOrderByExecutedAtDesc(Long inventoryId);

    List<InventoryTransaction> findByTypeOrderByExecutedAtDesc(TransactionType type);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.executedAt >= :fromDate ORDER BY it.executedAt DESC")
    List<InventoryTransaction> findRecentTransactions(@Param("fromDate") LocalDateTime fromDate);

    @Query("SELECT it FROM InventoryTransaction it WHERE it.inventory.id = :inventoryId AND it.type = :type ORDER BY it.executedAt DESC")
    List<InventoryTransaction> findByInventoryIdAndType(@Param("inventoryId") Long inventoryId, @Param("type") TransactionType type);
}