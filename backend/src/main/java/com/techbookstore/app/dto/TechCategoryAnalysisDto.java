package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for technology category analysis - 技術カテゴリ別分析
 */
public class TechCategoryAnalysisDto {
    
    private LocalDate analysisDate;
    private String categoryCode;
    private String categoryName;
    private String categoryLevel; // TOP_LEVEL, SUB_CATEGORY, DETAIL_CATEGORY
    private TechCategoryMetrics metrics;
    private TechCategoryTrend trend;
    private List<CompetitiveTech> competitiveTechnologies;
    private List<SubCategoryAnalysis> subCategories;
    private TechLifecycleAnalysis lifecycle;
    
    // Constructors
    public TechCategoryAnalysisDto() {}
    
    public TechCategoryAnalysisDto(LocalDate analysisDate, String categoryCode, String categoryName) {
        this.analysisDate = analysisDate;
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
    }
    
    // Inner classes for detailed tech category analysis
    public static class TechCategoryMetrics {
        private Integer totalBooks;
        private BigDecimal totalRevenue;
        private BigDecimal marketShare;
        private Integer activeCustomers;
        private BigDecimal averageBookPrice;
        private BigDecimal inventoryTurnover;
        private String profitabilityRank;
        
        public TechCategoryMetrics() {}
        
        public TechCategoryMetrics(Integer totalBooks, BigDecimal totalRevenue, BigDecimal marketShare) {
            this.totalBooks = totalBooks;
            this.totalRevenue = totalRevenue;
            this.marketShare = marketShare;
        }
        
        // Getters and Setters
        public Integer getTotalBooks() { return totalBooks; }
        public void setTotalBooks(Integer totalBooks) { this.totalBooks = totalBooks; }
        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
        public BigDecimal getMarketShare() { return marketShare; }
        public void setMarketShare(BigDecimal marketShare) { this.marketShare = marketShare; }
        public Integer getActiveCustomers() { return activeCustomers; }
        public void setActiveCustomers(Integer activeCustomers) { this.activeCustomers = activeCustomers; }
        public BigDecimal getAverageBookPrice() { return averageBookPrice; }
        public void setAverageBookPrice(BigDecimal averageBookPrice) { this.averageBookPrice = averageBookPrice; }
        public BigDecimal getInventoryTurnover() { return inventoryTurnover; }
        public void setInventoryTurnover(BigDecimal inventoryTurnover) { this.inventoryTurnover = inventoryTurnover; }
        public String getProfitabilityRank() { return profitabilityRank; }
        public void setProfitabilityRank(String profitabilityRank) { this.profitabilityRank = profitabilityRank; }
    }
    
    public static class TechCategoryTrend {
        private String trendDirection; // RISING, STABLE, DECLINING
        private BigDecimal growthRate;
        private String trendConfidence; // HIGH, MEDIUM, LOW
        private List<LocalDate> significantDates;
        private String trendAnalysis;
        private String futureOutlook;
        private List<String> trendDrivers;
        
        public TechCategoryTrend() {}
        
        public TechCategoryTrend(String trendDirection, BigDecimal growthRate, String trendConfidence) {
            this.trendDirection = trendDirection;
            this.growthRate = growthRate;
            this.trendConfidence = trendConfidence;
        }
        
        // Getters and Setters
        public String getTrendDirection() { return trendDirection; }
        public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }
        public BigDecimal getGrowthRate() { return growthRate; }
        public void setGrowthRate(BigDecimal growthRate) { this.growthRate = growthRate; }
        public String getTrendConfidence() { return trendConfidence; }
        public void setTrendConfidence(String trendConfidence) { this.trendConfidence = trendConfidence; }
        public List<LocalDate> getSignificantDates() { return significantDates; }
        public void setSignificantDates(List<LocalDate> significantDates) { this.significantDates = significantDates; }
        public String getTrendAnalysis() { return trendAnalysis; }
        public void setTrendAnalysis(String trendAnalysis) { this.trendAnalysis = trendAnalysis; }
        public String getFutureOutlook() { return futureOutlook; }
        public void setFutureOutlook(String futureOutlook) { this.futureOutlook = futureOutlook; }
        public List<String> getTrendDrivers() { return trendDrivers; }
        public void setTrendDrivers(List<String> trendDrivers) { this.trendDrivers = trendDrivers; }
    }
    
    public static class CompetitiveTech {
        private String techName;
        private String competitorCategory;
        private BigDecimal marketShareImpact;
        private String relationshipType; // COMPLEMENTARY, COMPETITIVE, SUBSTITUTE
        private String impactLevel; // HIGH, MEDIUM, LOW
        private String strategicRecommendation;
        
        public CompetitiveTech() {}
        
        public CompetitiveTech(String techName, String competitorCategory, BigDecimal marketShareImpact) {
            this.techName = techName;
            this.competitorCategory = competitorCategory;
            this.marketShareImpact = marketShareImpact;
        }
        
        // Getters and Setters
        public String getTechName() { return techName; }
        public void setTechName(String techName) { this.techName = techName; }
        public String getCompetitorCategory() { return competitorCategory; }
        public void setCompetitorCategory(String competitorCategory) { this.competitorCategory = competitorCategory; }
        public BigDecimal getMarketShareImpact() { return marketShareImpact; }
        public void setMarketShareImpact(BigDecimal marketShareImpact) { this.marketShareImpact = marketShareImpact; }
        public String getRelationshipType() { return relationshipType; }
        public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
        public String getImpactLevel() { return impactLevel; }
        public void setImpactLevel(String impactLevel) { this.impactLevel = impactLevel; }
        public String getStrategicRecommendation() { return strategicRecommendation; }
        public void setStrategicRecommendation(String strategicRecommendation) { this.strategicRecommendation = strategicRecommendation; }
    }
    
    public static class SubCategoryAnalysis {
        private String subCategoryCode;
        private String subCategoryName;
        private BigDecimal revenue;
        private BigDecimal marketShare;
        private String performance; // OUTPERFORMING, MEETING_EXPECTATIONS, UNDERPERFORMING
        private String recommendation;
        
        public SubCategoryAnalysis() {}
        
        public SubCategoryAnalysis(String subCategoryCode, String subCategoryName, BigDecimal revenue, 
                                 BigDecimal marketShare) {
            this.subCategoryCode = subCategoryCode;
            this.subCategoryName = subCategoryName;
            this.revenue = revenue;
            this.marketShare = marketShare;
        }
        
        // Getters and Setters
        public String getSubCategoryCode() { return subCategoryCode; }
        public void setSubCategoryCode(String subCategoryCode) { this.subCategoryCode = subCategoryCode; }
        public String getSubCategoryName() { return subCategoryName; }
        public void setSubCategoryName(String subCategoryName) { this.subCategoryName = subCategoryName; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public BigDecimal getMarketShare() { return marketShare; }
        public void setMarketShare(BigDecimal marketShare) { this.marketShare = marketShare; }
        public String getPerformance() { return performance; }
        public void setPerformance(String performance) { this.performance = performance; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    public static class TechLifecycleAnalysis {
        private String currentStage; // EMERGING, GROWTH, MATURITY, DECLINE
        private Integer estimatedMonthsInStage;
        private String nextStageProjection;
        private LocalDate estimatedTransitionDate;
        private String investmentRecommendation;
        private List<String> stageIndicators;
        
        public TechLifecycleAnalysis() {}
        
        public TechLifecycleAnalysis(String currentStage, Integer estimatedMonthsInStage, 
                                   String nextStageProjection) {
            this.currentStage = currentStage;
            this.estimatedMonthsInStage = estimatedMonthsInStage;
            this.nextStageProjection = nextStageProjection;
        }
        
        // Getters and Setters
        public String getCurrentStage() { return currentStage; }
        public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
        public Integer getEstimatedMonthsInStage() { return estimatedMonthsInStage; }
        public void setEstimatedMonthsInStage(Integer estimatedMonthsInStage) { this.estimatedMonthsInStage = estimatedMonthsInStage; }
        public String getNextStageProjection() { return nextStageProjection; }
        public void setNextStageProjection(String nextStageProjection) { this.nextStageProjection = nextStageProjection; }
        public LocalDate getEstimatedTransitionDate() { return estimatedTransitionDate; }
        public void setEstimatedTransitionDate(LocalDate estimatedTransitionDate) { this.estimatedTransitionDate = estimatedTransitionDate; }
        public String getInvestmentRecommendation() { return investmentRecommendation; }
        public void setInvestmentRecommendation(String investmentRecommendation) { this.investmentRecommendation = investmentRecommendation; }
        public List<String> getStageIndicators() { return stageIndicators; }
        public void setStageIndicators(List<String> stageIndicators) { this.stageIndicators = stageIndicators; }
    }
    
    // Main class getters and setters
    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }
    public String getCategoryCode() { return categoryCode; }
    public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getCategoryLevel() { return categoryLevel; }
    public void setCategoryLevel(String categoryLevel) { this.categoryLevel = categoryLevel; }
    public TechCategoryMetrics getMetrics() { return metrics; }
    public void setMetrics(TechCategoryMetrics metrics) { this.metrics = metrics; }
    public TechCategoryTrend getTrend() { return trend; }
    public void setTrend(TechCategoryTrend trend) { this.trend = trend; }
    public List<CompetitiveTech> getCompetitiveTechnologies() { return competitiveTechnologies; }
    public void setCompetitiveTechnologies(List<CompetitiveTech> competitiveTechnologies) { this.competitiveTechnologies = competitiveTechnologies; }
    public List<SubCategoryAnalysis> getSubCategories() { return subCategories; }
    public void setSubCategories(List<SubCategoryAnalysis> subCategories) { this.subCategories = subCategories; }
    public TechLifecycleAnalysis getLifecycle() { return lifecycle; }
    public void setLifecycle(TechLifecycleAnalysis lifecycle) { this.lifecycle = lifecycle; }
}