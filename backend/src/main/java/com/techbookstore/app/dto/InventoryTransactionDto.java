package com.techbookstore.app.dto;

import com.techbookstore.app.entity.InventoryTransaction;
import com.techbookstore.app.entity.TransactionStatus;
import com.techbookstore.app.entity.TransactionType;

import java.time.LocalDateTime;

/**
 * DTO for Inventory Transaction information
 */
public class InventoryTransactionDto {

    private Long id;
    private Long inventoryId;
    private String bookTitle;
    private TransactionType type;
    private Integer quantity;
    private Integer beforeQuantity;
    private Integer afterQuantity;
    private String reason;
    private String batchNumber;
    private String referenceNumber;
    private String executedByUsername;
    private String approvedByUsername;
    private LocalDateTime executedAt;
    private LocalDateTime approvedAt;
    private TransactionStatus status;

    public InventoryTransactionDto() {}

    public InventoryTransactionDto(InventoryTransaction transaction) {
        this.id = transaction.getId();
        this.inventoryId = transaction.getInventory().getId();
        this.bookTitle = transaction.getInventory().getBook().getTitle();
        this.type = transaction.getType();
        this.quantity = transaction.getQuantity();
        this.beforeQuantity = transaction.getBeforeQuantity();
        this.afterQuantity = transaction.getAfterQuantity();
        this.reason = transaction.getReason();
        this.batchNumber = transaction.getBatchNumber();
        this.referenceNumber = transaction.getReferenceNumber();
        this.executedByUsername = transaction.getExecutedBy() != null ? transaction.getExecutedBy().getUsername() : null;
        this.approvedByUsername = transaction.getApprovedBy() != null ? transaction.getApprovedBy().getUsername() : null;
        this.executedAt = transaction.getExecutedAt();
        this.approvedAt = transaction.getApprovedAt();
        this.status = transaction.getStatus();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getInventoryId() { return inventoryId; }
    public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getBeforeQuantity() { return beforeQuantity; }
    public void setBeforeQuantity(Integer beforeQuantity) { this.beforeQuantity = beforeQuantity; }

    public Integer getAfterQuantity() { return afterQuantity; }
    public void setAfterQuantity(Integer afterQuantity) { this.afterQuantity = afterQuantity; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getExecutedByUsername() { return executedByUsername; }
    public void setExecutedByUsername(String executedByUsername) { this.executedByUsername = executedByUsername; }

    public String getApprovedByUsername() { return approvedByUsername; }
    public void setApprovedByUsername(String approvedByUsername) { this.approvedByUsername = approvedByUsername; }

    public LocalDateTime getExecutedAt() { return executedAt; }
    public void setExecutedAt(LocalDateTime executedAt) { this.executedAt = executedAt; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
}