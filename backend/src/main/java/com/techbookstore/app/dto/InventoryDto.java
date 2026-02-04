package com.techbookstore.app.dto;

import com.techbookstore.app.entity.Inventory;
import java.time.LocalDate;

public class InventoryDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String isbn13;
    private Integer storeStock;
    private Integer warehouseStock;
    private Integer reservedCount;
    private String locationCode;
    private Integer reorderPoint;
    private Integer reorderQuantity;
    private LocalDate lastReceivedDate;
    private LocalDate lastSoldDate;
    private Integer totalStock;
    private Integer availableStock;
    private boolean lowStock;

    // Constructors
    public InventoryDto() {}

    public InventoryDto(Inventory inventory) {
        this.id = inventory.getId();
        this.bookId = inventory.getBook().getId();
        this.bookTitle = inventory.getBook().getTitle();
        this.isbn13 = inventory.getBook().getIsbn13();
        this.storeStock = inventory.getStoreStock();
        this.warehouseStock = inventory.getWarehouseStock();
        this.reservedCount = inventory.getReservedCount();
        this.locationCode = inventory.getLocationCode();
        this.reorderPoint = inventory.getReorderPoint();
        this.reorderQuantity = inventory.getReorderQuantity();
        this.lastReceivedDate = inventory.getLastReceivedDate();
        this.lastSoldDate = inventory.getLastSoldDate();
        this.totalStock = inventory.getTotalStock();
        this.availableStock = inventory.getAvailableStock();
        this.lowStock = inventory.isLowStock();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getIsbn13() { return isbn13; }
    public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }

    public Integer getStoreStock() { return storeStock; }
    public void setStoreStock(Integer storeStock) { this.storeStock = storeStock; }

    public Integer getWarehouseStock() { return warehouseStock; }
    public void setWarehouseStock(Integer warehouseStock) { this.warehouseStock = warehouseStock; }

    public Integer getReservedCount() { return reservedCount; }
    public void setReservedCount(Integer reservedCount) { this.reservedCount = reservedCount; }

    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }

    public Integer getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(Integer reorderPoint) { this.reorderPoint = reorderPoint; }

    public Integer getReorderQuantity() { return reorderQuantity; }
    public void setReorderQuantity(Integer reorderQuantity) { this.reorderQuantity = reorderQuantity; }

    public LocalDate getLastReceivedDate() { return lastReceivedDate; }
    public void setLastReceivedDate(LocalDate lastReceivedDate) { this.lastReceivedDate = lastReceivedDate; }

    public LocalDate getLastSoldDate() { return lastSoldDate; }
    public void setLastSoldDate(LocalDate lastSoldDate) { this.lastSoldDate = lastSoldDate; }

    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }

    public Integer getAvailableStock() { return availableStock; }
    public void setAvailableStock(Integer availableStock) { this.availableStock = availableStock; }

    public boolean isLowStock() { return lowStock; }
    public void setLowStock(boolean lowStock) { this.lowStock = lowStock; }
}