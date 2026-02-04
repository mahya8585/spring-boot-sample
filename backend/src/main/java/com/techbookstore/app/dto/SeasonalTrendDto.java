package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for seasonal trend analysis - 季節性分析
 */
public class SeasonalTrendDto {
    
    private LocalDate analysisDate;
    private String seasonType; // QUARTERLY, MONTHLY, ACADEMIC_YEAR, EXAM_PERIODS
    private String currentSeason;
    private List<SeasonalPeriod> seasonalPeriods;
    private List<CategorySeasonality> categorySeasonality;
    private SeasonalForecast forecast;
    private List<SeasonalRecommendation> recommendations;
    
    // Constructors
    public SeasonalTrendDto() {}
    
    public SeasonalTrendDto(LocalDate analysisDate, String seasonType, String currentSeason) {
        this.analysisDate = analysisDate;
        this.seasonType = seasonType;
        this.currentSeason = currentSeason;
    }
    
    // Inner classes for seasonal analysis
    public static class SeasonalPeriod {
        private String periodName; // Q1, Q2, Q3, Q4, NEW_YEAR, EXAM_SEASON, SUMMER_BREAK
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal revenueMultiplier;
        private BigDecimal demandMultiplier;
        private String characteristics;
        private List<String> keyEvents;
        
        public SeasonalPeriod() {}
        
        public SeasonalPeriod(String periodName, LocalDate startDate, LocalDate endDate, 
                            BigDecimal revenueMultiplier) {
            this.periodName = periodName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.revenueMultiplier = revenueMultiplier;
        }
        
        // Getters and Setters
        public String getPeriodName() { return periodName; }
        public void setPeriodName(String periodName) { this.periodName = periodName; }
        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
        public BigDecimal getRevenueMultiplier() { return revenueMultiplier; }
        public void setRevenueMultiplier(BigDecimal revenueMultiplier) { this.revenueMultiplier = revenueMultiplier; }
        public BigDecimal getDemandMultiplier() { return demandMultiplier; }
        public void setDemandMultiplier(BigDecimal demandMultiplier) { this.demandMultiplier = demandMultiplier; }
        public String getCharacteristics() { return characteristics; }
        public void setCharacteristics(String characteristics) { this.characteristics = characteristics; }
        public List<String> getKeyEvents() { return keyEvents; }
        public void setKeyEvents(List<String> keyEvents) { this.keyEvents = keyEvents; }
    }
    
    public static class CategorySeasonality {
        private String categoryCode;
        private String categoryName;
        private String seasonalityType; // HIGH, MEDIUM, LOW, COUNTER_SEASONAL
        private String peakSeason;
        private String lowSeason;
        private BigDecimal seasonalVariation;
        private List<MonthlyTrend> monthlyTrends;
        
        public CategorySeasonality() {}
        
        public CategorySeasonality(String categoryCode, String categoryName, String seasonalityType, 
                                 String peakSeason) {
            this.categoryCode = categoryCode;
            this.categoryName = categoryName;
            this.seasonalityType = seasonalityType;
            this.peakSeason = peakSeason;
        }
        
        // Getters and Setters
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public String getSeasonalityType() { return seasonalityType; }
        public void setSeasonalityType(String seasonalityType) { this.seasonalityType = seasonalityType; }
        public String getPeakSeason() { return peakSeason; }
        public void setPeakSeason(String peakSeason) { this.peakSeason = peakSeason; }
        public String getLowSeason() { return lowSeason; }
        public void setLowSeason(String lowSeason) { this.lowSeason = lowSeason; }
        public BigDecimal getSeasonalVariation() { return seasonalVariation; }
        public void setSeasonalVariation(BigDecimal seasonalVariation) { this.seasonalVariation = seasonalVariation; }
        public List<MonthlyTrend> getMonthlyTrends() { return monthlyTrends; }
        public void setMonthlyTrends(List<MonthlyTrend> monthlyTrends) { this.monthlyTrends = monthlyTrends; }
    }
    
    public static class MonthlyTrend {
        private Integer month;
        private String monthName;
        private BigDecimal normalizedRevenue;
        private BigDecimal yearOverYearGrowth;
        private String trendIndicator; // UP, DOWN, STABLE
        
        public MonthlyTrend() {}
        
        public MonthlyTrend(Integer month, String monthName, BigDecimal normalizedRevenue) {
            this.month = month;
            this.monthName = monthName;
            this.normalizedRevenue = normalizedRevenue;
        }
        
        // Getters and Setters
        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        public String getMonthName() { return monthName; }
        public void setMonthName(String monthName) { this.monthName = monthName; }
        public BigDecimal getNormalizedRevenue() { return normalizedRevenue; }
        public void setNormalizedRevenue(BigDecimal normalizedRevenue) { this.normalizedRevenue = normalizedRevenue; }
        public BigDecimal getYearOverYearGrowth() { return yearOverYearGrowth; }
        public void setYearOverYearGrowth(BigDecimal yearOverYearGrowth) { this.yearOverYearGrowth = yearOverYearGrowth; }
        public String getTrendIndicator() { return trendIndicator; }
        public void setTrendIndicator(String trendIndicator) { this.trendIndicator = trendIndicator; }
    }
    
    public static class SeasonalForecast {
        private LocalDate forecastStartDate;
        private LocalDate forecastEndDate;
        private String forecastSeason;
        private BigDecimal expectedRevenue;
        private BigDecimal expectedGrowth;
        private String confidenceLevel; // HIGH, MEDIUM, LOW
        private List<String> keyAssumptions;
        
        public SeasonalForecast() {}
        
        public SeasonalForecast(LocalDate forecastStartDate, LocalDate forecastEndDate, 
                              String forecastSeason, BigDecimal expectedRevenue) {
            this.forecastStartDate = forecastStartDate;
            this.forecastEndDate = forecastEndDate;
            this.forecastSeason = forecastSeason;
            this.expectedRevenue = expectedRevenue;
        }
        
        // Getters and Setters
        public LocalDate getForecastStartDate() { return forecastStartDate; }
        public void setForecastStartDate(LocalDate forecastStartDate) { this.forecastStartDate = forecastStartDate; }
        public LocalDate getForecastEndDate() { return forecastEndDate; }
        public void setForecastEndDate(LocalDate forecastEndDate) { this.forecastEndDate = forecastEndDate; }
        public String getForecastSeason() { return forecastSeason; }
        public void setForecastSeason(String forecastSeason) { this.forecastSeason = forecastSeason; }
        public BigDecimal getExpectedRevenue() { return expectedRevenue; }
        public void setExpectedRevenue(BigDecimal expectedRevenue) { this.expectedRevenue = expectedRevenue; }
        public BigDecimal getExpectedGrowth() { return expectedGrowth; }
        public void setExpectedGrowth(BigDecimal expectedGrowth) { this.expectedGrowth = expectedGrowth; }
        public String getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }
        public List<String> getKeyAssumptions() { return keyAssumptions; }
        public void setKeyAssumptions(List<String> keyAssumptions) { this.keyAssumptions = keyAssumptions; }
    }
    
    public static class SeasonalRecommendation {
        private String recommendationType; // INVENTORY, MARKETING, PRICING, STAFFING
        private String priority; // HIGH, MEDIUM, LOW
        private String action;
        private String targetPeriod;
        private BigDecimal expectedImpact;
        private String implementation;
        
        public SeasonalRecommendation() {}
        
        public SeasonalRecommendation(String recommendationType, String priority, String action, 
                                    String targetPeriod) {
            this.recommendationType = recommendationType;
            this.priority = priority;
            this.action = action;
            this.targetPeriod = targetPeriod;
        }
        
        // Getters and Setters
        public String getRecommendationType() { return recommendationType; }
        public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
        public String getTargetPeriod() { return targetPeriod; }
        public void setTargetPeriod(String targetPeriod) { this.targetPeriod = targetPeriod; }
        public BigDecimal getExpectedImpact() { return expectedImpact; }
        public void setExpectedImpact(BigDecimal expectedImpact) { this.expectedImpact = expectedImpact; }
        public String getImplementation() { return implementation; }
        public void setImplementation(String implementation) { this.implementation = implementation; }
    }
    
    // Main class getters and setters
    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }
    public String getSeasonType() { return seasonType; }
    public void setSeasonType(String seasonType) { this.seasonType = seasonType; }
    public String getCurrentSeason() { return currentSeason; }
    public void setCurrentSeason(String currentSeason) { this.currentSeason = currentSeason; }
    public List<SeasonalPeriod> getSeasonalPeriods() { return seasonalPeriods; }
    public void setSeasonalPeriods(List<SeasonalPeriod> seasonalPeriods) { this.seasonalPeriods = seasonalPeriods; }
    public List<CategorySeasonality> getCategorySeasonality() { return categorySeasonality; }
    public void setCategorySeasonality(List<CategorySeasonality> categorySeasonality) { this.categorySeasonality = categorySeasonality; }
    public SeasonalForecast getForecast() { return forecast; }
    public void setForecast(SeasonalForecast forecast) { this.forecast = forecast; }
    public List<SeasonalRecommendation> getRecommendations() { return recommendations; }
    public void setRecommendations(List<SeasonalRecommendation> recommendations) { this.recommendations = recommendations; }
}