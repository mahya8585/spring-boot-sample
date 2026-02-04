package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class DashboardKpiDto {
    
    private LocalDate reportDate;
    private RevenueKpis revenue;
    private OrderKpis orders;
    private CustomerKpis customers;
    private InventoryKpis inventory;
    private TechTrendKpis techTrends;
    private List<TrendSummary> trends;
    
    // Constructors
    public DashboardKpiDto() {}
    
    public DashboardKpiDto(LocalDate reportDate) {
        this.reportDate = reportDate;
    }
    
    // Inner classes for KPI groupings
    public static class RevenueKpis {
        private BigDecimal todayRevenue;
        private BigDecimal weekRevenue;
        private BigDecimal monthRevenue;
        private BigDecimal yearRevenue;
        private Double revenueGrowth;
        
        public RevenueKpis() {}
        
        public RevenueKpis(BigDecimal todayRevenue, BigDecimal weekRevenue, BigDecimal monthRevenue, 
                          BigDecimal yearRevenue, Double revenueGrowth) {
            this.todayRevenue = todayRevenue;
            this.weekRevenue = weekRevenue;
            this.monthRevenue = monthRevenue;
            this.yearRevenue = yearRevenue;
            this.revenueGrowth = revenueGrowth;
        }
        
        // Getters and Setters
        public BigDecimal getTodayRevenue() { return todayRevenue; }
        public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
        public BigDecimal getWeekRevenue() { return weekRevenue; }
        public void setWeekRevenue(BigDecimal weekRevenue) { this.weekRevenue = weekRevenue; }
        public BigDecimal getMonthRevenue() { return monthRevenue; }
        public void setMonthRevenue(BigDecimal monthRevenue) { this.monthRevenue = monthRevenue; }
        public BigDecimal getYearRevenue() { return yearRevenue; }
        public void setYearRevenue(BigDecimal yearRevenue) { this.yearRevenue = yearRevenue; }
        public Double getRevenueGrowth() { return revenueGrowth; }
        public void setRevenueGrowth(Double revenueGrowth) { this.revenueGrowth = revenueGrowth; }
    }
    
    public static class OrderKpis {
        private Integer todayOrders;
        private Integer weekOrders;
        private Integer monthOrders;
        private BigDecimal averageOrderValue;
        private Double orderGrowth;
        
        public OrderKpis() {}
        
        public OrderKpis(Integer todayOrders, Integer weekOrders, Integer monthOrders, 
                        BigDecimal averageOrderValue, Double orderGrowth) {
            this.todayOrders = todayOrders;
            this.weekOrders = weekOrders;
            this.monthOrders = monthOrders;
            this.averageOrderValue = averageOrderValue;
            this.orderGrowth = orderGrowth;
        }
        
        // Getters and Setters
        public Integer getTodayOrders() { return todayOrders; }
        public void setTodayOrders(Integer todayOrders) { this.todayOrders = todayOrders; }
        public Integer getWeekOrders() { return weekOrders; }
        public void setWeekOrders(Integer weekOrders) { this.weekOrders = weekOrders; }
        public Integer getMonthOrders() { return monthOrders; }
        public void setMonthOrders(Integer monthOrders) { this.monthOrders = monthOrders; }
        public BigDecimal getAverageOrderValue() { return averageOrderValue; }
        public void setAverageOrderValue(BigDecimal averageOrderValue) { this.averageOrderValue = averageOrderValue; }
        public Double getOrderGrowth() { return orderGrowth; }
        public void setOrderGrowth(Double orderGrowth) { this.orderGrowth = orderGrowth; }
    }
    
    public static class CustomerKpis {
        private Integer totalCustomers;
        private Integer newCustomersThisMonth;
        private Integer activeCustomers;
        private Double customerRetentionRate;
        private Double customerGrowth;
        
        public CustomerKpis() {}
        
        public CustomerKpis(Integer totalCustomers, Integer newCustomersThisMonth, Integer activeCustomers, 
                           Double customerRetentionRate, Double customerGrowth) {
            this.totalCustomers = totalCustomers;
            this.newCustomersThisMonth = newCustomersThisMonth;
            this.activeCustomers = activeCustomers;
            this.customerRetentionRate = customerRetentionRate;
            this.customerGrowth = customerGrowth;
        }
        
        // Getters and Setters
        public Integer getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(Integer totalCustomers) { this.totalCustomers = totalCustomers; }
        public Integer getNewCustomersThisMonth() { return newCustomersThisMonth; }
        public void setNewCustomersThisMonth(Integer newCustomersThisMonth) { this.newCustomersThisMonth = newCustomersThisMonth; }
        public Integer getActiveCustomers() { return activeCustomers; }
        public void setActiveCustomers(Integer activeCustomers) { this.activeCustomers = activeCustomers; }
        public Double getCustomerRetentionRate() { return customerRetentionRate; }
        public void setCustomerRetentionRate(Double customerRetentionRate) { this.customerRetentionRate = customerRetentionRate; }
        public Double getCustomerGrowth() { return customerGrowth; }
        public void setCustomerGrowth(Double customerGrowth) { this.customerGrowth = customerGrowth; }
    }
    
    public static class InventoryKpis {
        private Integer totalProducts;
        private Integer lowStockItems;
        private Integer outOfStockItems;
        private BigDecimal totalInventoryValue;
        private Double inventoryTurnover;
        
        public InventoryKpis() {}
        
        public InventoryKpis(Integer totalProducts, Integer lowStockItems, Integer outOfStockItems, 
                           BigDecimal totalInventoryValue, Double inventoryTurnover) {
            this.totalProducts = totalProducts;
            this.lowStockItems = lowStockItems;
            this.outOfStockItems = outOfStockItems;
            this.totalInventoryValue = totalInventoryValue;
            this.inventoryTurnover = inventoryTurnover;
        }
        
        // Getters and Setters
        public Integer getTotalProducts() { return totalProducts; }
        public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }
        public Integer getLowStockItems() { return lowStockItems; }
        public void setLowStockItems(Integer lowStockItems) { this.lowStockItems = lowStockItems; }
        public Integer getOutOfStockItems() { return outOfStockItems; }
        public void setOutOfStockItems(Integer outOfStockItems) { this.outOfStockItems = outOfStockItems; }
        public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
        public void setTotalInventoryValue(BigDecimal totalInventoryValue) { this.totalInventoryValue = totalInventoryValue; }
        public Double getInventoryTurnover() { return inventoryTurnover; }
        public void setInventoryTurnover(Double inventoryTurnover) { this.inventoryTurnover = inventoryTurnover; }
    }
    
    public static class TechTrendKpis {
        private String topRisingTech;
        private Double topRisingGrowth;
        private String topFallingTech;
        private Double topFallingDecline;
        private Integer emergingTechCount;
        private Integer obsoleteTechCount;
        private BigDecimal techCategoryDiversity;
        private Double innovationIndex;
        
        public TechTrendKpis() {}
        
        public TechTrendKpis(String topRisingTech, Double topRisingGrowth, String topFallingTech, 
                           Double topFallingDecline, Integer emergingTechCount, Integer obsoleteTechCount,
                           BigDecimal techCategoryDiversity, Double innovationIndex) {
            this.topRisingTech = topRisingTech;
            this.topRisingGrowth = topRisingGrowth;
            this.topFallingTech = topFallingTech;
            this.topFallingDecline = topFallingDecline;
            this.emergingTechCount = emergingTechCount;
            this.obsoleteTechCount = obsoleteTechCount;
            this.techCategoryDiversity = techCategoryDiversity;
            this.innovationIndex = innovationIndex;
        }
        
        // Getters and Setters
        public String getTopRisingTech() { return topRisingTech; }
        public void setTopRisingTech(String topRisingTech) { this.topRisingTech = topRisingTech; }
        public Double getTopRisingGrowth() { return topRisingGrowth; }
        public void setTopRisingGrowth(Double topRisingGrowth) { this.topRisingGrowth = topRisingGrowth; }
        public String getTopFallingTech() { return topFallingTech; }
        public void setTopFallingTech(String topFallingTech) { this.topFallingTech = topFallingTech; }
        public Double getTopFallingDecline() { return topFallingDecline; }
        public void setTopFallingDecline(Double topFallingDecline) { this.topFallingDecline = topFallingDecline; }
        public Integer getEmergingTechCount() { return emergingTechCount; }
        public void setEmergingTechCount(Integer emergingTechCount) { this.emergingTechCount = emergingTechCount; }
        public Integer getObsoleteTechCount() { return obsoleteTechCount; }
        public void setObsoleteTechCount(Integer obsoleteTechCount) { this.obsoleteTechCount = obsoleteTechCount; }
        public BigDecimal getTechCategoryDiversity() { return techCategoryDiversity; }
        public void setTechCategoryDiversity(BigDecimal techCategoryDiversity) { this.techCategoryDiversity = techCategoryDiversity; }
        public Double getInnovationIndex() { return innovationIndex; }
        public void setInnovationIndex(Double innovationIndex) { this.innovationIndex = innovationIndex; }
    }
    
    public static class TrendSummary {
        private String metric;
        private String period;
        private Double changePercent;
        private String trend; // UP, DOWN, STABLE
        
        public TrendSummary() {}
        
        public TrendSummary(String metric, String period, Double changePercent, String trend) {
            this.metric = metric;
            this.period = period;
            this.changePercent = changePercent;
            this.trend = trend;
        }
        
        // Getters and Setters
        public String getMetric() { return metric; }
        public void setMetric(String metric) { this.metric = metric; }
        public String getPeriod() { return period; }
        public void setPeriod(String period) { this.period = period; }
        public Double getChangePercent() { return changePercent; }
        public void setChangePercent(Double changePercent) { this.changePercent = changePercent; }
        public String getTrend() { return trend; }
        public void setTrend(String trend) { this.trend = trend; }
    }
    
    // Main class getters and setters
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public RevenueKpis getRevenue() { return revenue; }
    public void setRevenue(RevenueKpis revenue) { this.revenue = revenue; }
    public OrderKpis getOrders() { return orders; }
    public void setOrders(OrderKpis orders) { this.orders = orders; }
    public CustomerKpis getCustomers() { return customers; }
    public void setCustomers(CustomerKpis customers) { this.customers = customers; }
    public InventoryKpis getInventory() { return inventory; }
    public void setInventory(InventoryKpis inventory) { this.inventory = inventory; }
    public TechTrendKpis getTechTrends() { return techTrends; }
    public void setTechTrends(TechTrendKpis techTrends) { this.techTrends = techTrends; }
    public List<TrendSummary> getTrends() { return trends; }
    public void setTrends(List<TrendSummary> trends) { this.trends = trends; }
}