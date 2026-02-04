package com.techbookstore.app.dto;

import java.math.BigDecimal;

/**
 * DTO for optimal stock level recommendations
 * Contains the optimal stock level calculation for a specific book
 */
public class OptimalStockLevel {
    
    private Long bookId;
    private String bookTitle;
    private Integer currentStock;
    private Integer optimalStock;
    private Integer safetyStock;
    private Integer reorderPoint;
    private BigDecimal forecastedDemand;
    private Integer leadTimeDays;
    private String calculationMethod; // EOQ, STATISTICAL, etc.
    private String recommendation; // INCREASE, DECREASE, MAINTAIN
    
    // Constructors
    public OptimalStockLevel() {}
    
    public OptimalStockLevel(Long bookId, String bookTitle, Integer currentStock, 
                           Integer optimalStock, Integer safetyStock, Integer reorderPoint) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.currentStock = currentStock;
        this.optimalStock = optimalStock;
        this.safetyStock = safetyStock;
        this.reorderPoint = reorderPoint;
    }
    
    // Getters and setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    
    public Integer getOptimalStock() { return optimalStock; }
    public void setOptimalStock(Integer optimalStock) { this.optimalStock = optimalStock; }
    
    public Integer getSafetyStock() { return safetyStock; }
    public void setSafetyStock(Integer safetyStock) { this.safetyStock = safetyStock; }
    
    public Integer getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(Integer reorderPoint) { this.reorderPoint = reorderPoint; }
    
    public BigDecimal getForecastedDemand() { return forecastedDemand; }
    public void setForecastedDemand(BigDecimal forecastedDemand) { this.forecastedDemand = forecastedDemand; }
    
    public Integer getLeadTimeDays() { return leadTimeDays; }
    public void setLeadTimeDays(Integer leadTimeDays) { this.leadTimeDays = leadTimeDays; }
    
    public String getCalculationMethod() { return calculationMethod; }
    public void setCalculationMethod(String calculationMethod) { this.calculationMethod = calculationMethod; }
    
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}