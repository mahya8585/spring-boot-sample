package com.techbookstore.app.dto;

import com.techbookstore.app.entity.InventoryReservation;

import java.time.LocalDateTime;

/**
 * DTO for Inventory Reservation information
 */
public class InventoryReservationDto {

    private Long id;
    private Long inventoryId;
    private String bookTitle;
    private Long orderId;
    private Long customerId;
    private Integer reservedQuantity;
    private String reservationType;
    private LocalDateTime reservedUntil;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime releasedAt;

    public InventoryReservationDto() {}

    public InventoryReservationDto(InventoryReservation reservation) {
        this.id = reservation.getId();
        this.inventoryId = reservation.getInventory().getId();
        this.bookTitle = reservation.getInventory().getBook().getTitle();
        this.orderId = reservation.getOrderId();
        this.customerId = reservation.getCustomerId();
        this.reservedQuantity = reservation.getReservedQuantity();
        this.reservationType = reservation.getReservationType();
        this.reservedUntil = reservation.getReservedUntil();
        this.status = reservation.getStatus();
        this.createdAt = reservation.getCreatedAt();
        this.releasedAt = reservation.getReleasedAt();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInventoryId() { return inventoryId; }
    public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Integer getReservedQuantity() { return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity) { this.reservedQuantity = reservedQuantity; }

    public String getReservationType() { return reservationType; }
    public void setReservationType(String reservationType) { this.reservationType = reservationType; }

    public LocalDateTime getReservedUntil() { return reservedUntil; }
    public void setReservedUntil(LocalDateTime reservedUntil) { this.reservedUntil = reservedUntil; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReleasedAt() { return releasedAt; }
    public void setReleasedAt(LocalDateTime releasedAt) { this.releasedAt = releasedAt; }
}