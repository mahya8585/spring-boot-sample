package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class InventoryReportDto {
    
    private LocalDate reportDate;
    private Integer totalProducts;
    private Integer lowStockCount;
    private Integer outOfStockCount;
    private BigDecimal totalInventoryValue;
    private List<InventoryItem> items;
    private List<ReorderSuggestion> reorderSuggestions;
    private InventoryTurnoverSummary turnoverSummary;
    
    // Phase 1 enhancement - additional analytics fields
    private Integer deadStockItems;
    private BigDecimal deadStockValue;
    private Double averageTurnoverRate;
    private Double obsolescenceRiskIndex;
    
    // Constructors
    public InventoryReportDto() {}
    
    public InventoryReportDto(LocalDate reportDate, Integer totalProducts, Integer lowStockCount, 
                             Integer outOfStockCount, BigDecimal totalInventoryValue) {
        this.reportDate = reportDate;
        this.totalProducts = totalProducts;
        this.lowStockCount = lowStockCount;
        this.outOfStockCount = outOfStockCount;
        this.totalInventoryValue = totalInventoryValue;
    }
    
    // Inner classes
    public static class InventoryItem {
        private Long bookId;
        private String title;
        private String category;
        private Integer currentStock;
        private Integer reorderLevel;
        private String stockStatus;
        private BigDecimal unitValue;
        private BigDecimal totalValue;
        
        public InventoryItem() {}
        
        public InventoryItem(Long bookId, String title, String category, Integer currentStock, 
                           Integer reorderLevel, String stockStatus, BigDecimal unitValue, BigDecimal totalValue) {
            this.bookId = bookId;
            this.title = title;
            this.category = category;
            this.currentStock = currentStock;
            this.reorderLevel = reorderLevel;
            this.stockStatus = stockStatus;
            this.unitValue = unitValue;
            this.totalValue = totalValue;
        }
        
        // Getters and Setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public Integer getReorderLevel() { return reorderLevel; }
        public void setReorderLevel(Integer reorderLevel) { this.reorderLevel = reorderLevel; }
        public String getStockStatus() { return stockStatus; }
        public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
        public BigDecimal getUnitValue() { return unitValue; }
        public void setUnitValue(BigDecimal unitValue) { this.unitValue = unitValue; }
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
    }
    
    public static class ReorderSuggestion {
        private Long bookId;
        private String title;
        private Integer currentStock;
        private Integer suggestedOrder;
        private String urgency;
        private Integer daysUntilStockout;
        
        public ReorderSuggestion() {}
        
        public ReorderSuggestion(Long bookId, String title, Integer currentStock, Integer suggestedOrder, 
                               String urgency, Integer daysUntilStockout) {
            this.bookId = bookId;
            this.title = title;
            this.currentStock = currentStock;
            this.suggestedOrder = suggestedOrder;
            this.urgency = urgency;
            this.daysUntilStockout = daysUntilStockout;
        }
        
        // Getters and Setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public Integer getSuggestedOrder() { return suggestedOrder; }
        public void setSuggestedOrder(Integer suggestedOrder) { this.suggestedOrder = suggestedOrder; }
        public String getUrgency() { return urgency; }
        public void setUrgency(String urgency) { this.urgency = urgency; }
        public Integer getDaysUntilStockout() { return daysUntilStockout; }
        public void setDaysUntilStockout(Integer daysUntilStockout) { this.daysUntilStockout = daysUntilStockout; }
    }
    
    public static class InventoryTurnoverSummary {
        private Double averageTurnoverRate;
        private String fastestMovingCategory;
        private String slowestMovingCategory;
        
        public InventoryTurnoverSummary() {}
        
        public InventoryTurnoverSummary(Double averageTurnoverRate, String fastestMovingCategory, String slowestMovingCategory) {
            this.averageTurnoverRate = averageTurnoverRate;
            this.fastestMovingCategory = fastestMovingCategory;
            this.slowestMovingCategory = slowestMovingCategory;
        }
        
        // Getters and Setters
        public Double getAverageTurnoverRate() { return averageTurnoverRate; }
        public void setAverageTurnoverRate(Double averageTurnoverRate) { this.averageTurnoverRate = averageTurnoverRate; }
        public String getFastestMovingCategory() { return fastestMovingCategory; }
        public void setFastestMovingCategory(String fastestMovingCategory) { this.fastestMovingCategory = fastestMovingCategory; }
        public String getSlowestMovingCategory() { return slowestMovingCategory; }
        public void setSlowestMovingCategory(String slowestMovingCategory) { this.slowestMovingCategory = slowestMovingCategory; }
    }
    
    // Main class getters and setters
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public Integer getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }
    public Integer getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(Integer lowStockCount) { this.lowStockCount = lowStockCount; }
    public Integer getOutOfStockCount() { return outOfStockCount; }
    public void setOutOfStockCount(Integer outOfStockCount) { this.outOfStockCount = outOfStockCount; }
    public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
    public List<InventoryItem> getItems() { return items; }
    public void setItems(List<InventoryItem> items) { this.items = items; }
    public List<ReorderSuggestion> getReorderSuggestions() { return reorderSuggestions; }
    public void setReorderSuggestions(List<ReorderSuggestion> reorderSuggestions) { this.reorderSuggestions = reorderSuggestions; }
    
    public InventoryTurnoverSummary getTurnoverSummary() { return turnoverSummary; }
    public void setTurnoverSummary(InventoryTurnoverSummary turnoverSummary) { this.turnoverSummary = turnoverSummary; }
    
    // Phase 1 enhancement - getters and setters for new analytics fields
    public Integer getDeadStockItems() { return deadStockItems; }
    public void setDeadStockItems(Integer deadStockItems) { this.deadStockItems = deadStockItems; }
    
    public BigDecimal getDeadStockValue() { return deadStockValue; }
    public void setDeadStockValue(BigDecimal deadStockValue) { this.deadStockValue = deadStockValue; }
    
    public Double getAverageTurnoverRate() { return averageTurnoverRate; }
    public void setAverageTurnoverRate(Double averageTurnoverRate) { this.averageTurnoverRate = averageTurnoverRate; }
    
    public Double getObsolescenceRiskIndex() { return obsolescenceRiskIndex; }
    public void setObsolescenceRiskIndex(Double obsolescenceRiskIndex) { this.obsolescenceRiskIndex = obsolescenceRiskIndex; }
}