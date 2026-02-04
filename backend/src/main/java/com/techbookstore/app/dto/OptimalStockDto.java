package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for optimal stock information
 * 最適在庫情報DTO
 */
public class OptimalStockDto {

    private Long bookId;
    private String bookTitle;
    private Integer currentStock;
    private Integer optimalStockLevel;
    private Integer reorderPoint;
    private Integer safetyStock;
    private Integer economicOrderQuantity;
    private BigDecimal obsolescenceFactor;
    private BigDecimal trendFactor;
    private BigDecimal seasonalityFactor;
    private LocalDate validFrom;
    private LocalDate validTo;
    private String stockStatus; // "OPTIMAL", "OVERSTOCK", "UNDERSTOCK", "REORDER_NEEDED"
    private Integer recommendedOrderQuantity;
    private BigDecimal estimatedCost;
    private BigDecimal estimatedRevenue;

    // Constructors
    public OptimalStockDto() {}

    public OptimalStockDto(Long bookId, String bookTitle, Integer currentStock, 
                          Integer optimalStockLevel, Integer reorderPoint, Integer safetyStock) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.currentStock = currentStock;
        this.optimalStockLevel = optimalStockLevel;
        this.reorderPoint = reorderPoint;
        this.safetyStock = safetyStock;
    }

    // Getters and Setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getOptimalStockLevel() { return optimalStockLevel; }
    public void setOptimalStockLevel(Integer optimalStockLevel) { this.optimalStockLevel = optimalStockLevel; }

    public Integer getReorderPoint() { return reorderPoint; }
    public void setReorderPoint(Integer reorderPoint) { this.reorderPoint = reorderPoint; }

    public Integer getSafetyStock() { return safetyStock; }
    public void setSafetyStock(Integer safetyStock) { this.safetyStock = safetyStock; }

    public Integer getEconomicOrderQuantity() { return economicOrderQuantity; }
    public void setEconomicOrderQuantity(Integer economicOrderQuantity) { this.economicOrderQuantity = economicOrderQuantity; }

    public BigDecimal getObsolescenceFactor() { return obsolescenceFactor; }
    public void setObsolescenceFactor(BigDecimal obsolescenceFactor) { this.obsolescenceFactor = obsolescenceFactor; }

    public BigDecimal getTrendFactor() { return trendFactor; }
    public void setTrendFactor(BigDecimal trendFactor) { this.trendFactor = trendFactor; }

    public BigDecimal getSeasonalityFactor() { return seasonalityFactor; }
    public void setSeasonalityFactor(BigDecimal seasonalityFactor) { this.seasonalityFactor = seasonalityFactor; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidTo() { return validTo; }
    public void setValidTo(LocalDate validTo) { this.validTo = validTo; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public Integer getRecommendedOrderQuantity() { return recommendedOrderQuantity; }
    public void setRecommendedOrderQuantity(Integer recommendedOrderQuantity) { this.recommendedOrderQuantity = recommendedOrderQuantity; }

    public BigDecimal getEstimatedCost() { return estimatedCost; }
    public void setEstimatedCost(BigDecimal estimatedCost) { this.estimatedCost = estimatedCost; }

    public BigDecimal getEstimatedRevenue() { return estimatedRevenue; }
    public void setEstimatedRevenue(BigDecimal estimatedRevenue) { this.estimatedRevenue = estimatedRevenue; }
}