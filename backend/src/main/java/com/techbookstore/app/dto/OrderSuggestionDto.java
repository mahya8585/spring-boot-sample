package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for intelligent order suggestions - インテリジェント発注提案
 */
public class OrderSuggestionDto {
    
    private LocalDate suggestionDate;
    private String suggestionType; // REORDER, NEW_STOCK, SEASONAL_PREP, TREND_BASED
    private Integer totalSuggestions;
    private BigDecimal totalOrderValue;
    private String priority; // HIGH, MEDIUM, LOW
    private List<BookOrderSuggestion> bookSuggestions;
    private List<CategoryOrderSuggestion> categorySuggestions;
    private OrderOptimization optimization;
    private List<RiskFactor> riskFactors;
    
    // Constructors
    public OrderSuggestionDto() {}
    
    public OrderSuggestionDto(LocalDate suggestionDate, String suggestionType, Integer totalSuggestions, 
                             BigDecimal totalOrderValue) {
        this.suggestionDate = suggestionDate;
        this.suggestionType = suggestionType;
        this.totalSuggestions = totalSuggestions;
        this.totalOrderValue = totalOrderValue;
    }
    
    // Inner classes for detailed order suggestions
    public static class BookOrderSuggestion {
        private Long bookId;
        private String bookTitle;
        private String isbn13;
        private String categoryCode;
        private Integer currentStock;
        private Integer suggestedQuantity;
        private BigDecimal unitCost;
        private BigDecimal totalCost;
        private String reason; // LOW_STOCK, HIGH_DEMAND, SEASONAL, NEW_EDITION
        private String urgency; // IMMEDIATE, WITHIN_WEEK, WITHIN_MONTH
        private Integer daysUntilStockout;
        private BigDecimal expectedRoi;
        private LocalDate suggestedOrderDate;
        private LocalDate expectedDeliveryDate;
        
        public BookOrderSuggestion() {}
        
        public BookOrderSuggestion(Long bookId, String bookTitle, Integer currentStock, 
                                 Integer suggestedQuantity, BigDecimal unitCost, String reason) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.currentStock = currentStock;
            this.suggestedQuantity = suggestedQuantity;
            this.unitCost = unitCost;
            this.reason = reason;
        }
        
        // Getters and Setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getIsbn13() { return isbn13; }
        public void setIsbn13(String isbn13) { this.isbn13 = isbn13; }
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public Integer getSuggestedQuantity() { return suggestedQuantity; }
        public void setSuggestedQuantity(Integer suggestedQuantity) { this.suggestedQuantity = suggestedQuantity; }
        public BigDecimal getUnitCost() { return unitCost; }
        public void setUnitCost(BigDecimal unitCost) { this.unitCost = unitCost; }
        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public String getUrgency() { return urgency; }
        public void setUrgency(String urgency) { this.urgency = urgency; }
        public Integer getDaysUntilStockout() { return daysUntilStockout; }
        public void setDaysUntilStockout(Integer daysUntilStockout) { this.daysUntilStockout = daysUntilStockout; }
        public BigDecimal getExpectedRoi() { return expectedRoi; }
        public void setExpectedRoi(BigDecimal expectedRoi) { this.expectedRoi = expectedRoi; }
        public LocalDate getSuggestedOrderDate() { return suggestedOrderDate; }
        public void setSuggestedOrderDate(LocalDate suggestedOrderDate) { this.suggestedOrderDate = suggestedOrderDate; }
        public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }
        public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    }
    
    public static class CategoryOrderSuggestion {
        private String categoryCode;
        private String categoryName;
        private Integer itemCount;
        private BigDecimal totalOrderValue;
        private String trend; // RISING, STABLE, DECLINING
        private BigDecimal marketGrowthRate;
        private String seasonalFactor;
        private String strategicRecommendation;
        private List<String> topBooks;
        
        public CategoryOrderSuggestion() {}
        
        public CategoryOrderSuggestion(String categoryCode, String categoryName, Integer itemCount, 
                                     BigDecimal totalOrderValue, String trend) {
            this.categoryCode = categoryCode;
            this.categoryName = categoryName;
            this.itemCount = itemCount;
            this.totalOrderValue = totalOrderValue;
            this.trend = trend;
        }
        
        // Getters and Setters
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public Integer getItemCount() { return itemCount; }
        public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }
        public BigDecimal getTotalOrderValue() { return totalOrderValue; }
        public void setTotalOrderValue(BigDecimal totalOrderValue) { this.totalOrderValue = totalOrderValue; }
        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
        public BigDecimal getMarketGrowthRate() { return marketGrowthRate; }
        public void setMarketGrowthRate(BigDecimal marketGrowthRate) { this.marketGrowthRate = marketGrowthRate; }
        public String getSeasonalFactor() { return seasonalFactor; }
        public void setSeasonalFactor(String seasonalFactor) { this.seasonalFactor = seasonalFactor; }
        public String getStrategicRecommendation() { return strategicRecommendation; }
        public void setStrategicRecommendation(String strategicRecommendation) { this.strategicRecommendation = strategicRecommendation; }
        public List<String> getTopBooks() { return topBooks; }
        public void setTopBooks(List<String> topBooks) { this.topBooks = topBooks; }
    }
    
    public static class OrderOptimization {
        private BigDecimal totalBudget;
        private BigDecimal suggestedSpending;
        private BigDecimal expectedRevenue;
        private BigDecimal expectedProfit;
        private String cashFlowImpact;
        private String riskLevel;
        private List<String> optimizationRecommendations;
        
        public OrderOptimization() {}
        
        public OrderOptimization(BigDecimal totalBudget, BigDecimal suggestedSpending, 
                               BigDecimal expectedRevenue, BigDecimal expectedProfit) {
            this.totalBudget = totalBudget;
            this.suggestedSpending = suggestedSpending;
            this.expectedRevenue = expectedRevenue;
            this.expectedProfit = expectedProfit;
        }
        
        // Getters and Setters
        public BigDecimal getTotalBudget() { return totalBudget; }
        public void setTotalBudget(BigDecimal totalBudget) { this.totalBudget = totalBudget; }
        public BigDecimal getSuggestedSpending() { return suggestedSpending; }
        public void setSuggestedSpending(BigDecimal suggestedSpending) { this.suggestedSpending = suggestedSpending; }
        public BigDecimal getExpectedRevenue() { return expectedRevenue; }
        public void setExpectedRevenue(BigDecimal expectedRevenue) { this.expectedRevenue = expectedRevenue; }
        public BigDecimal getExpectedProfit() { return expectedProfit; }
        public void setExpectedProfit(BigDecimal expectedProfit) { this.expectedProfit = expectedProfit; }
        public String getCashFlowImpact() { return cashFlowImpact; }
        public void setCashFlowImpact(String cashFlowImpact) { this.cashFlowImpact = cashFlowImpact; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public List<String> getOptimizationRecommendations() { return optimizationRecommendations; }
        public void setOptimizationRecommendations(List<String> optimizationRecommendations) { this.optimizationRecommendations = optimizationRecommendations; }
    }
    
    public static class RiskFactor {
        private String riskType; // MARKET_RISK, TECH_OBSOLESCENCE, SUPPLIER_RISK, CASH_FLOW
        private String severity; // HIGH, MEDIUM, LOW
        private String description;
        private String mitigation;
        private BigDecimal probabilityPercentage;
        
        public RiskFactor() {}
        
        public RiskFactor(String riskType, String severity, String description, String mitigation) {
            this.riskType = riskType;
            this.severity = severity;
            this.description = description;
            this.mitigation = mitigation;
        }
        
        // Getters and Setters
        public String getRiskType() { return riskType; }
        public void setRiskType(String riskType) { this.riskType = riskType; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getMitigation() { return mitigation; }
        public void setMitigation(String mitigation) { this.mitigation = mitigation; }
        public BigDecimal getProbabilityPercentage() { return probabilityPercentage; }
        public void setProbabilityPercentage(BigDecimal probabilityPercentage) { this.probabilityPercentage = probabilityPercentage; }
    }
    
    // Main class getters and setters
    public LocalDate getSuggestionDate() { return suggestionDate; }
    public void setSuggestionDate(LocalDate suggestionDate) { this.suggestionDate = suggestionDate; }
    public String getSuggestionType() { return suggestionType; }
    public void setSuggestionType(String suggestionType) { this.suggestionType = suggestionType; }
    public Integer getTotalSuggestions() { return totalSuggestions; }
    public void setTotalSuggestions(Integer totalSuggestions) { this.totalSuggestions = totalSuggestions; }
    public BigDecimal getTotalOrderValue() { return totalOrderValue; }
    public void setTotalOrderValue(BigDecimal totalOrderValue) { this.totalOrderValue = totalOrderValue; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public List<BookOrderSuggestion> getBookSuggestions() { return bookSuggestions; }
    public void setBookSuggestions(List<BookOrderSuggestion> bookSuggestions) { this.bookSuggestions = bookSuggestions; }
    public List<CategoryOrderSuggestion> getCategorySuggestions() { return categorySuggestions; }
    public void setCategorySuggestions(List<CategoryOrderSuggestion> categorySuggestions) { this.categorySuggestions = categorySuggestions; }
    public OrderOptimization getOptimization() { return optimization; }
    public void setOptimization(OrderOptimization optimization) { this.optimization = optimization; }
    public List<RiskFactor> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<RiskFactor> riskFactors) { this.riskFactors = riskFactors; }
}