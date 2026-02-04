package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CustomerAnalyticsDto {
    
    private LocalDate reportDate;
    private Integer totalCustomers;
    private Integer activeCustomers;
    private BigDecimal averageCustomerValue;
    private List<CustomerSegment> segments;
    private RFMAnalysis rfmAnalysis;
    private CustomerTrends trends;
    
    // Constructors
    public CustomerAnalyticsDto() {}
    
    public CustomerAnalyticsDto(LocalDate reportDate, Integer totalCustomers, Integer activeCustomers, BigDecimal averageCustomerValue) {
        this.reportDate = reportDate;
        this.totalCustomers = totalCustomers;
        this.activeCustomers = activeCustomers;
        this.averageCustomerValue = averageCustomerValue;
    }
    
    // Inner classes
    public static class CustomerSegment {
        private String segmentName;
        private Integer customerCount;
        private BigDecimal totalValue;
        private Double percentage;
        private String characteristics;
        
        public CustomerSegment() {}
        
        public CustomerSegment(String segmentName, Integer customerCount, BigDecimal totalValue, 
                             Double percentage, String characteristics) {
            this.segmentName = segmentName;
            this.customerCount = customerCount;
            this.totalValue = totalValue;
            this.percentage = percentage;
            this.characteristics = characteristics;
        }
        
        // Getters and Setters
        public String getSegmentName() { return segmentName; }
        public void setSegmentName(String segmentName) { this.segmentName = segmentName; }
        public Integer getCustomerCount() { return customerCount; }
        public void setCustomerCount(Integer customerCount) { this.customerCount = customerCount; }
        public BigDecimal getTotalValue() { return totalValue; }
        public void setTotalValue(BigDecimal totalValue) { this.totalValue = totalValue; }
        public Double getPercentage() { return percentage; }
        public void setPercentage(Double percentage) { this.percentage = percentage; }
        public String getCharacteristics() { return characteristics; }
        public void setCharacteristics(String characteristics) { this.characteristics = characteristics; }
    }
    
    public static class RFMAnalysis {
        private List<RFMSegment> segments;
        private Integer championCount;
        private Integer loyalCount;
        private Integer potentialLoyalistCount;
        private Integer atRiskCount;
        
        public RFMAnalysis() {}
        
        public RFMAnalysis(List<RFMSegment> segments, Integer championCount, Integer loyalCount, 
                          Integer potentialLoyalistCount, Integer atRiskCount) {
            this.segments = segments;
            this.championCount = championCount;
            this.loyalCount = loyalCount;
            this.potentialLoyalistCount = potentialLoyalistCount;
            this.atRiskCount = atRiskCount;
        }
        
        // Getters and Setters
        public List<RFMSegment> getSegments() { return segments; }
        public void setSegments(List<RFMSegment> segments) { this.segments = segments; }
        public Integer getChampionCount() { return championCount; }
        public void setChampionCount(Integer championCount) { this.championCount = championCount; }
        public Integer getLoyalCount() { return loyalCount; }
        public void setLoyalCount(Integer loyalCount) { this.loyalCount = loyalCount; }
        public Integer getPotentialLoyalistCount() { return potentialLoyalistCount; }
        public void setPotentialLoyalistCount(Integer potentialLoyalistCount) { this.potentialLoyalistCount = potentialLoyalistCount; }
        public Integer getAtRiskCount() { return atRiskCount; }
        public void setAtRiskCount(Integer atRiskCount) { this.atRiskCount = atRiskCount; }
    }
    
    public static class RFMSegment {
        private String segmentName;
        private Integer recencyScore;
        private Integer frequencyScore;
        private Integer monetaryScore;
        private Integer customerCount;
        
        public RFMSegment() {}
        
        public RFMSegment(String segmentName, Integer recencyScore, Integer frequencyScore, 
                         Integer monetaryScore, Integer customerCount) {
            this.segmentName = segmentName;
            this.recencyScore = recencyScore;
            this.frequencyScore = frequencyScore;
            this.monetaryScore = monetaryScore;
            this.customerCount = customerCount;
        }
        
        // Getters and Setters
        public String getSegmentName() { return segmentName; }
        public void setSegmentName(String segmentName) { this.segmentName = segmentName; }
        public Integer getRecencyScore() { return recencyScore; }
        public void setRecencyScore(Integer recencyScore) { this.recencyScore = recencyScore; }
        public Integer getFrequencyScore() { return frequencyScore; }
        public void setFrequencyScore(Integer frequencyScore) { this.frequencyScore = frequencyScore; }
        public Integer getMonetaryScore() { return monetaryScore; }
        public void setMonetaryScore(Integer monetaryScore) { this.monetaryScore = monetaryScore; }
        public Integer getCustomerCount() { return customerCount; }
        public void setCustomerCount(Integer customerCount) { this.customerCount = customerCount; }
    }
    
    public static class CustomerTrends {
        private List<CustomerTrendItem> newCustomers;
        private List<CustomerTrendItem> returningCustomers;
        private Double retentionRate;
        private Double churnRate;
        
        public CustomerTrends() {}
        
        public CustomerTrends(List<CustomerTrendItem> newCustomers, List<CustomerTrendItem> returningCustomers, 
                             Double retentionRate, Double churnRate) {
            this.newCustomers = newCustomers;
            this.returningCustomers = returningCustomers;
            this.retentionRate = retentionRate;
            this.churnRate = churnRate;
        }
        
        // Getters and Setters
        public List<CustomerTrendItem> getNewCustomers() { return newCustomers; }
        public void setNewCustomers(List<CustomerTrendItem> newCustomers) { this.newCustomers = newCustomers; }
        public List<CustomerTrendItem> getReturningCustomers() { return returningCustomers; }
        public void setReturningCustomers(List<CustomerTrendItem> returningCustomers) { this.returningCustomers = returningCustomers; }
        public Double getRetentionRate() { return retentionRate; }
        public void setRetentionRate(Double retentionRate) { this.retentionRate = retentionRate; }
        public Double getChurnRate() { return churnRate; }
        public void setChurnRate(Double churnRate) { this.churnRate = churnRate; }
    }
    
    public static class CustomerTrendItem {
        private LocalDate date;
        private Integer count;
        private BigDecimal value;
        
        public CustomerTrendItem() {}
        
        public CustomerTrendItem(LocalDate date, Integer count, BigDecimal value) {
            this.date = date;
            this.count = count;
            this.value = value;
        }
        
        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
        public BigDecimal getValue() { return value; }
        public void setValue(BigDecimal value) { this.value = value; }
    }
    
    // Main class getters and setters
    public LocalDate getReportDate() { return reportDate; }
    public void setReportDate(LocalDate reportDate) { this.reportDate = reportDate; }
    public Integer getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(Integer totalCustomers) { this.totalCustomers = totalCustomers; }
    public Integer getActiveCustomers() { return activeCustomers; }
    public void setActiveCustomers(Integer activeCustomers) { this.activeCustomers = activeCustomers; }
    public BigDecimal getAverageCustomerValue() { return averageCustomerValue; }
    public void setAverageCustomerValue(BigDecimal averageCustomerValue) { this.averageCustomerValue = averageCustomerValue; }
    public List<CustomerSegment> getSegments() { return segments; }
    public void setSegments(List<CustomerSegment> segments) { this.segments = segments; }
    public RFMAnalysis getRfmAnalysis() { return rfmAnalysis; }
    public void setRfmAnalysis(RFMAnalysis rfmAnalysis) { this.rfmAnalysis = rfmAnalysis; }
    public CustomerTrends getTrends() { return trends; }
    public void setTrends(CustomerTrends trends) { this.trends = trends; }
}