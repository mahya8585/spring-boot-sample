package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for comprehensive sales analysis - 売上分析統合クラス
 */
public class SalesAnalysisDto {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private BigDecimal averageOrderValue;
    private BigDecimal growthRate;
    private List<TechCategorySales> techCategorySales;
    private List<CustomerSegmentSales> customerSegmentSales;
    private List<PeriodSales> periodSales;
    private List<ProfitabilityItem> profitabilityItems;
    private ComparisonAnalysis comparisonAnalysis;
    
    // Constructors
    public SalesAnalysisDto() {}
    
    public SalesAnalysisDto(LocalDate startDate, LocalDate endDate, BigDecimal totalRevenue, 
                           Integer totalOrders, BigDecimal averageOrderValue) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.averageOrderValue = averageOrderValue;
    }
    
    // Inner classes for detailed analysis
    public static class TechCategorySales {
        private String categoryCode;
        private String categoryName;
        private BigDecimal revenue;
        private Integer orderCount;
        private BigDecimal marketShare;
        private BigDecimal growthRate;
        private String trendDirection; // UP, DOWN, STABLE
        
        public TechCategorySales() {}
        
        public TechCategorySales(String categoryCode, String categoryName, BigDecimal revenue, 
                               Integer orderCount, BigDecimal marketShare) {
            this.categoryCode = categoryCode;
            this.categoryName = categoryName;
            this.revenue = revenue;
            this.orderCount = orderCount;
            this.marketShare = marketShare;
        }
        
        // Getters and Setters
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Integer getOrderCount() { return orderCount; }
        public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
        public BigDecimal getMarketShare() { return marketShare; }
        public void setMarketShare(BigDecimal marketShare) { this.marketShare = marketShare; }
        public BigDecimal getGrowthRate() { return growthRate; }
        public void setGrowthRate(BigDecimal growthRate) { this.growthRate = growthRate; }
        public String getTrendDirection() { return trendDirection; }
        public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }
    }
    
    public static class CustomerSegmentSales {
        private String segmentType; // BEGINNER, INTERMEDIATE, ADVANCED, ENTERPRISE
        private Integer customerCount;
        private BigDecimal revenue;
        private BigDecimal averageOrderValue;
        private BigDecimal customerLifetimeValue;
        
        public CustomerSegmentSales() {}
        
        public CustomerSegmentSales(String segmentType, Integer customerCount, BigDecimal revenue, 
                                  BigDecimal averageOrderValue) {
            this.segmentType = segmentType;
            this.customerCount = customerCount;
            this.revenue = revenue;
            this.averageOrderValue = averageOrderValue;
        }
        
        // Getters and Setters
        public String getSegmentType() { return segmentType; }
        public void setSegmentType(String segmentType) { this.segmentType = segmentType; }
        public Integer getCustomerCount() { return customerCount; }
        public void setCustomerCount(Integer customerCount) { this.customerCount = customerCount; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
        public BigDecimal getCustomerLifetimeValue() { return customerLifetimeValue; }
        public void setCustomerLifetimeValue(BigDecimal customerLifetimeValue) { this.customerLifetimeValue = customerLifetimeValue; }
    }
    
    public static class PeriodSales {
        private LocalDate periodDate;
        private String periodType; // DAILY, WEEKLY, MONTHLY, QUARTERLY
        private BigDecimal revenue;
        private Integer orderCount;
        private BigDecimal growthRate;
        
        public PeriodSales() {}
        
        public PeriodSales(LocalDate periodDate, String periodType, BigDecimal revenue, Integer orderCount) {
            this.periodDate = periodDate;
            this.periodType = periodType;
            this.revenue = revenue;
            this.orderCount = orderCount;
        }
        
        // Getters and Setters
        public LocalDate getPeriodDate() { return periodDate; }
        public void setPeriodDate(LocalDate periodDate) { this.periodDate = periodDate; }
        public String getPeriodType() { return periodType; }
        public void setPeriodType(String periodType) { this.periodType = periodType; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Integer getOrderCount() { return orderCount; }
        public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
        public BigDecimal getGrowthRate() { return growthRate; }
        public void setGrowthRate(BigDecimal growthRate) { this.growthRate = growthRate; }
    }
    
    public static class ProfitabilityItem {
        private String itemType; // BOOK, CATEGORY, SEGMENT
        private String itemName;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profit;
        private BigDecimal profitMargin;
        private BigDecimal roi;
        
        public ProfitabilityItem() {}
        
        public ProfitabilityItem(String itemType, String itemName, BigDecimal revenue, 
                               BigDecimal cost, BigDecimal profit, BigDecimal profitMargin) {
            this.itemType = itemType;
            this.itemName = itemName;
            this.revenue = revenue;
            this.cost = cost;
            this.profit = profit;
            this.profitMargin = profitMargin;
        }
        
        // Getters and Setters
        public String getItemType() { return itemType; }
        public void setItemType(String itemType) { this.itemType = itemType; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public BigDecimal getProfit() { return profit; }
        public void setProfit(BigDecimal profit) { this.profit = profit; }
        public BigDecimal getProfitMargin() { return profitMargin; }
        public void setProfitMargin(BigDecimal profitMargin) { this.profitMargin = profitMargin; }
        public BigDecimal getRoi() { return roi; }
        public void setRoi(BigDecimal roi) { this.roi = roi; }
    }
    
    public static class ComparisonAnalysis {
        private String comparisonType; // PREVIOUS_PERIOD, PREVIOUS_YEAR, SAME_PERIOD_LAST_YEAR
        private BigDecimal currentPeriodRevenue;
        private BigDecimal comparisonPeriodRevenue;
        private BigDecimal changeAmount;
        private BigDecimal changePercentage;
        private String trend; // IMPROVING, DECLINING, STABLE
        
        public ComparisonAnalysis() {}
        
        public ComparisonAnalysis(String comparisonType, BigDecimal currentPeriodRevenue, 
                                BigDecimal comparisonPeriodRevenue) {
            this.comparisonType = comparisonType;
            this.currentPeriodRevenue = currentPeriodRevenue;
            this.comparisonPeriodRevenue = comparisonPeriodRevenue;
        }
        
        // Getters and Setters
        public String getComparisonType() { return comparisonType; }
        public void setComparisonType(String comparisonType) { this.comparisonType = comparisonType; }
        public BigDecimal getCurrentPeriodRevenue() { return currentPeriodRevenue; }
        public void setCurrentPeriodRevenue(BigDecimal currentPeriodRevenue) { this.currentPeriodRevenue = currentPeriodRevenue; }
        public BigDecimal getComparisonPeriodRevenue() { return comparisonPeriodRevenue; }
        public void setComparisonPeriodRevenue(BigDecimal comparisonPeriodRevenue) { this.comparisonPeriodRevenue = comparisonPeriodRevenue; }
        public BigDecimal getChangeAmount() { return changeAmount; }
        public void setChangeAmount(BigDecimal changeAmount) { this.changeAmount = changeAmount; }
        public BigDecimal getChangePercentage() { return changePercentage; }
        public void setChangePercentage(BigDecimal changePercentage) { this.changePercentage = changePercentage; }
        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
    }
    
    // Main class getters and setters
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public BigDecimal getAverageOrderValue() { return averageOrderValue; }
    public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
    public BigDecimal getGrowthRate() { return growthRate; }
    public void setGrowthRate(BigDecimal growthRate) { this.growthRate = growthRate; }
    public List<TechCategorySales> getTechCategorySales() { return techCategorySales; }
    public void setTechCategorySales(List<TechCategorySales> techCategorySales) { this.techCategorySales = techCategorySales; }
    public List<CustomerSegmentSales> getCustomerSegmentSales() { return customerSegmentSales; }
    public void setCustomerSegmentSales(List<CustomerSegmentSales> customerSegmentSales) { this.customerSegmentSales = customerSegmentSales; }
    public List<PeriodSales> getPeriodSales() { return periodSales; }
    public void setPeriodSales(List<PeriodSales> periodSales) { this.periodSales = periodSales; }
    public List<ProfitabilityItem> getProfitabilityItems() { return profitabilityItems; }
    public void setProfitabilityItems(List<ProfitabilityItem> profitabilityItems) { this.profitabilityItems = profitabilityItems; }
    public ComparisonAnalysis getComparisonAnalysis() { return comparisonAnalysis; }
    public void setComparisonAnalysis(ComparisonAnalysis comparisonAnalysis) { this.comparisonAnalysis = comparisonAnalysis; }
}