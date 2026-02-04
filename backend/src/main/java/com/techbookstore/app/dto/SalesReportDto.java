package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class SalesReportDto {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private BigDecimal averageOrderValue;
    private List<SalesTrendItem> trends;
    private List<SalesRankingItem> rankings;
    private SalesBreakdown breakdown;
    
    // Constructors
    public SalesReportDto() {}
    
    public SalesReportDto(LocalDate startDate, LocalDate endDate, BigDecimal totalRevenue, 
                         Integer totalOrders, BigDecimal averageOrderValue) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalRevenue = totalRevenue;
        this.totalOrders = totalOrders;
        this.averageOrderValue = averageOrderValue;
    }
    
    // Inner classes for report components
    public static class SalesTrendItem {
        private LocalDate date;
        private BigDecimal revenue;
        private Integer orderCount;
        
        public SalesTrendItem() {}
        
        public SalesTrendItem(LocalDate date, BigDecimal revenue, Integer orderCount) {
            this.date = date;
            this.revenue = revenue;
            this.orderCount = orderCount;
        }
        
        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Integer getOrderCount() { return orderCount; }
        public void setOrderCount(Integer orderCount) { this.orderCount = orderCount; }
    }
    
    public static class SalesRankingItem {
        private String category;
        private String itemName;
        private BigDecimal revenue;
        private Integer quantity;
        private Integer rank;
        
        public SalesRankingItem() {}
        
        public SalesRankingItem(String category, String itemName, BigDecimal revenue, Integer quantity, Integer rank) {
            this.category = category;
            this.itemName = itemName;
            this.revenue = revenue;
            this.quantity = quantity;
            this.rank = rank;
        }
        
        // Getters and Setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getItemName() { return itemName; }
        public void setItemName(String itemName) { this.itemName = itemName; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Integer getRank() { return rank; }
        public void setRank(Integer rank) { this.rank = rank; }
    }
    
    public static class SalesBreakdown {
        private BigDecimal onlineRevenue;
        private BigDecimal walkInRevenue;
        private BigDecimal phoneRevenue;
        
        public SalesBreakdown() {}
        
        public SalesBreakdown(BigDecimal onlineRevenue, BigDecimal walkInRevenue, BigDecimal phoneRevenue) {
            this.onlineRevenue = onlineRevenue;
            this.walkInRevenue = walkInRevenue;
            this.phoneRevenue = phoneRevenue;
        }
        
        // Getters and Setters
        public BigDecimal getOnlineRevenue() { return onlineRevenue; }
        public void setOnlineRevenue(BigDecimal onlineRevenue) { this.onlineRevenue = onlineRevenue; }
        public BigDecimal getWalkInRevenue() { return walkInRevenue; }
        public void setWalkInRevenue(BigDecimal walkInRevenue) { this.walkInRevenue = walkInRevenue; }
        public BigDecimal getPhoneRevenue() { return phoneRevenue; }
        public void setPhoneRevenue(BigDecimal phoneRevenue) { this.phoneRevenue = phoneRevenue; }
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
    public List<SalesTrendItem> getTrends() { return trends; }
    public void setTrends(List<SalesTrendItem> trends) { this.trends = trends; }
    public List<SalesRankingItem> getRankings() { return rankings; }
    public void setRankings(List<SalesRankingItem> rankings) { this.rankings = rankings; }
    public SalesBreakdown getBreakdown() { return breakdown; }
    public void setBreakdown(SalesBreakdown breakdown) { this.breakdown = breakdown; }
}