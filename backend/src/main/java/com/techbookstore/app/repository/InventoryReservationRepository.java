package com.techbookstore.app.repository;

import com.techbookstore.app.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

    List<InventoryReservation> findByInventoryIdAndStatus(Long inventoryId, String status);

    List<InventoryReservation> findByOrderIdAndStatus(Long orderId, String status);

    List<InventoryReservation> findByCustomerIdAndStatus(Long customerId, String status);

    @Query("SELECT ir FROM InventoryReservation ir WHERE ir.status = 'ACTIVE' AND ir.reservedUntil < :currentTime")
    List<InventoryReservation> findExpiredReservations(@Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT SUM(ir.reservedQuantity) FROM InventoryReservation ir WHERE ir.inventory.id = :inventoryId AND ir.status = 'ACTIVE'")
    Integer getTotalReservedQuantityByInventoryId(@Param("inventoryId") Long inventoryId);
}