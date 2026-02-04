package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for comprehensive inventory analysis - 在庫分析統合クラス
 */
public class InventoryAnalysisDto {
    
    private LocalDate analysisDate;
    private Integer totalItems;
    private BigDecimal totalInventoryValue;
    private BigDecimal averageInventoryTurnover;
    private Integer deadStockItems;
    private BigDecimal deadStockValue;
    private List<InventoryTurnoverItem> turnoverAnalysis;
    private List<DeadStockItem> deadStockAnalysis;
    private List<TechObsolescenceItem> obsolescenceRisk;
    private List<StockLevelItem> stockLevelAnalysis;
    private SeasonalInventoryTrend seasonalTrend;
    
    // Constructors
    public InventoryAnalysisDto() {}
    
    public InventoryAnalysisDto(LocalDate analysisDate, Integer totalItems, BigDecimal totalInventoryValue, 
                               BigDecimal averageInventoryTurnover) {
        this.analysisDate = analysisDate;
        this.totalItems = totalItems;
        this.totalInventoryValue = totalInventoryValue;
        this.averageInventoryTurnover = averageInventoryTurnover;
    }
    
    // Inner classes for detailed inventory analysis
    public static class InventoryTurnoverItem {
        private Long bookId;
        private String bookTitle;
        private String categoryCode;
        private Integer currentStock;
        private BigDecimal turnoverRate;
        private Integer daysSinceLastSale;
        private String turnoverCategory; // FAST, MEDIUM, SLOW, DEAD
        private BigDecimal annualRevenue;
        
        public InventoryTurnoverItem() {}
        
        public InventoryTurnoverItem(Long bookId, String bookTitle, String categoryCode, 
                                   Integer currentStock, BigDecimal turnoverRate) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.categoryCode = categoryCode;
            this.currentStock = currentStock;
            this.turnoverRate = turnoverRate;
        }
        
        // Getters and Setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public BigDecimal getTurnoverRate() { return turnoverRate; }
        public void setTurnoverRate(BigDecimal turnoverRate) { this.turnoverRate = turnoverRate; }
        public Integer getDaysSinceLastSale() { return daysSinceLastSale; }
        public void setDaysSinceLastSale(Integer daysSinceLastSale) { this.daysSinceLastSale = daysSinceLastSale; }
        public String getTurnoverCategory() { return turnoverCategory; }
        public void setTurnoverCategory(String turnoverCategory) { this.turnoverCategory = turnoverCategory; }
        public BigDecimal getAnnualRevenue() { return annualRevenue; }
        public void setAnnualRevenue(BigDecimal annualRevenue) { this.annualRevenue = annualRevenue; }
    }
    
    public static class DeadStockItem {
        private Long bookId;
        private String bookTitle;
        private String categoryCode;
        private Integer currentStock;
        private BigDecimal stockValue;
        private Integer daysSinceLastSale;
        private LocalDate lastSaleDate;
        private String riskLevel; // HIGH, MEDIUM, LOW
        private String recommendedAction; // DISCOUNT, RETURN, LIQUIDATE
        
        public DeadStockItem() {}
        
        public DeadStockItem(Long bookId, String bookTitle, Integer currentStock, 
                           BigDecimal stockValue, Integer daysSinceLastSale) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.currentStock = currentStock;
            this.stockValue = stockValue;
            this.daysSinceLastSale = daysSinceLastSale;
        }
        
        // Getters and Setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public BigDecimal getStockValue() { return stockValue; }
        public void setStockValue(BigDecimal stockValue) { this.stockValue = stockValue; }
        public Integer getDaysSinceLastSale() { return daysSinceLastSale; }
        public void setDaysSinceLastSale(Integer daysSinceLastSale) { this.daysSinceLastSale = daysSinceLastSale; }
        public LocalDate getLastSaleDate() { return lastSaleDate; }
        public void setLastSaleDate(LocalDate lastSaleDate) { this.lastSaleDate = lastSaleDate; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public String getRecommendedAction() { return recommendedAction; }
        public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
    }
    
    public static class TechObsolescenceItem {
        private String categoryCode;
        private String categoryName;
        private Integer itemCount;
        private BigDecimal totalValue;
        private String obsolescenceRisk; // HIGH, MEDIUM, LOW
        private String techLifecycleStage; // EMERGING, GROWTH, MATURE, DECLINING
        private Integer monthsToObsolescence;
        private String mitigationStrategy;
        
        public TechObsolescenceItem() {}
        
        public TechObsolescenceItem(String categoryCode, String categoryName, Integer itemCount, 
                                  BigDecimal totalValue, String obsolescenceRisk) {
            this.categoryCode = categoryCode;
            this.categoryName = categoryName;
            this.itemCount = itemCount;
            this.totalValue = totalValue;
            this.obsolescenceRisk = obsolescenceRisk;
        }
        
        // Getters and Setters
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public Integer getItemCount() { return itemCount; }
        public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
        public String getObsolescenceRisk() { return obsolescenceRisk; }
        public void setObsolescenceRisk(String obsolescenceRisk) { this.obsolescenceRisk = obsolescenceRisk; }
        public String getTechLifecycleStage() { return techLifecycleStage; }
        public void setTechLifecycleStage(String techLifecycleStage) { this.techLifecycleStage = techLifecycleStage; }
        public Integer getMonthsToObsolescence() { return monthsToObsolescence; }
        public void setMonthsToObsolescence(Integer monthsToObsolescence) { this.monthsToObsolescence = monthsToObsolescence; }
        public String getMitigationStrategy() { return mitigationStrategy; }
        public void setMitigationStrategy(String mitigationStrategy) { this.mitigationStrategy = mitigationStrategy; }
    }
    
    public static class StockLevelItem {
        private Long bookId;
        private String bookTitle;
        private String categoryCode;
        private Integer currentStock;
        private Integer reorderPoint;
        private Integer reorderQuantity;
        private Integer safetyStock;
        private String stockStatus; // NORMAL, LOW, CRITICAL, OVERSTOCK
        private Integer daysOfSupply;
        
        public StockLevelItem() {}
        
        public StockLevelItem(Long bookId, String bookTitle, Integer currentStock, 
                            Integer reorderPoint, String stockStatus) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.currentStock = currentStock;
            this.reorderPoint = reorderPoint;
            this.stockStatus = stockStatus;
        }
        
        // Getters and Setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public Integer getReorderPoint() { return reorderPoint; }
        public void setReorderPoint(Integer reorderPoint) { this.reorderPoint = reorderPoint; }
        public Integer getReorderQuantity() { return reorderQuantity; }
        public void setReorderQuantity(Integer reorderQuantity) { this.reorderQuantity = reorderQuantity; }
        public Integer getSafetyStock() { return safetyStock; }
        public void setSafetyStock(Integer safetyStock) { this.safetyStock = safetyStock; }
        public String getStockStatus() { return stockStatus; }
        public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }
        public Integer getDaysOfSupply() { return daysOfSupply; }
        public void setDaysOfSupply(Integer daysOfSupply) { this.daysOfSupply = daysOfSupply; }
    }
    
    public static class SeasonalInventoryTrend {
        private String season; // SPRING, SUMMER, FALL, WINTER, NEW_YEAR, EXAM_PERIOD
        private BigDecimal expectedDemandIncrease;
        private List<String> topCategories;
        private String recommendation;
        
        public SeasonalInventoryTrend() {}
        
        public SeasonalInventoryTrend(String season, BigDecimal expectedDemandIncrease, 
                                    List<String> topCategories) {
            this.season = season;
            this.expectedDemandIncrease = expectedDemandIncrease;
            this.topCategories = topCategories;
        }
        
        // Getters and Setters
        public String getSeason() { return season; }
        public void setSeason(String season) { this.season = season; }
        public BigDecimal getExpectedDemandIncrease() { return expectedDemandIncrease; }
        public void setExpectedDemandIncrease(BigDecimal expectedDemandIncrease) { this.expectedDemandIncrease = expectedDemandIncrease; }
        public List<String> getTopCategories() { return topCategories; }
        public void setTopCategories(List<String> topCategories) { this.topCategories = topCategories; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    // Main class getters and setters
    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }
    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }
    public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
    public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
    public BigDecimal getAverageInventoryTurnover() { return averageInventoryTurnover; }
    public void setAverageInventoryTurnover(BigDecimal averageInventoryTurnover) { this.averageInventoryTurnover = averageInventoryTurnover; }
    public Integer getDeadStockItems() { return deadStockItems; }
    public void setDeadStockItems(Integer deadStockItems) { this.deadStockItems = deadStockItems; }
    public BigDecimal getDeadStockValue() { return deadStockValue; }
    public void setDeadStockValue(BigDecimal deadStockValue) { this.deadStockValue = deadStockValue; }
    public List<InventoryTurnoverItem> getTurnoverAnalysis() { return turnoverAnalysis; }
    public void setTurnoverAnalysis(List<InventoryTurnoverItem> turnoverAnalysis) { this.turnoverAnalysis = turnoverAnalysis; }
    public List<DeadStockItem> getDeadStockAnalysis() { return deadStockAnalysis; }
    public void setDeadStockAnalysis(List<DeadStockItem> deadStockAnalysis) { this.deadStockAnalysis = deadStockAnalysis; }
    public List<TechObsolescenceItem> getObsolescenceRisk() { return obsolescenceRisk; }
    public void setObsolescenceRisk(List<TechObsolescenceItem> obsolescenceRisk) { this.obsolescenceRisk = obsolescenceRisk; }
    public List<StockLevelItem> getStockLevelAnalysis() { return stockLevelAnalysis; }
    public void setStockLevelAnalysis(List<StockLevelItem> stockLevelAnalysis) { this.stockLevelAnalysis = stockLevelAnalysis; }
    public SeasonalInventoryTrend getSeasonalTrend() { return seasonalTrend; }
    public void setSeasonalTrend(SeasonalInventoryTrend seasonalTrend) { this.seasonalTrend = seasonalTrend; }
}