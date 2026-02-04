package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for demand and sales predictions - 需要予測・売上予測
 */
public class PredictionDto {
    
    private LocalDate predictionDate;
    private String predictionType; // DEMAND, SALES, INVENTORY_TURNOVER
    private String timeHorizon; // SHORT_TERM, MEDIUM_TERM, LONG_TERM (1 month, 3 months, 6 months)
    private BigDecimal accuracy; // Prediction accuracy percentage
    private String algorithm; // MOVING_AVERAGE, SEASONAL, TREND_ANALYSIS
    private List<DemandPrediction> demandPredictions;
    private List<SalesPrediction> salesPredictions;
    private List<SeasonalFactor> seasonalFactors;
    private PredictionConfidence confidence;
    
    // Constructors
    public PredictionDto() {}
    
    public PredictionDto(LocalDate predictionDate, String predictionType, String timeHorizon, 
                        BigDecimal accuracy) {
        this.predictionDate = predictionDate;
        this.predictionType = predictionType;
        this.timeHorizon = timeHorizon;
        this.accuracy = accuracy;
    }
    
    // Inner classes for detailed predictions
    public static class DemandPrediction {
        private Long bookId;
        private String bookTitle;
        private String categoryCode;
        private LocalDate forecastDate;
        private Integer predictedDemand;
        private Integer currentStock;
        private String demandTrend; // INCREASING, DECREASING, STABLE
        private BigDecimal confidenceLevel;
        private List<String> influencingFactors;
        
        public DemandPrediction() {}
        
        public DemandPrediction(Long bookId, String bookTitle, LocalDate forecastDate, 
                              Integer predictedDemand, Integer currentStock) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.forecastDate = forecastDate;
            this.predictedDemand = predictedDemand;
            this.currentStock = currentStock;
        }
        
        // Getters and Setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public LocalDate getForecastDate() { return forecastDate; }
        public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }
        public Integer getPredictedDemand() { return predictedDemand; }
        public void setPredictedDemand(Integer predictedDemand) { this.predictedDemand = predictedDemand; }
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        public String getDemandTrend() { return demandTrend; }
        public void setDemandTrend(String demandTrend) { this.demandTrend = demandTrend; }
        public BigDecimal getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(BigDecimal confidenceLevel) { this.confidenceLevel = confidenceLevel; }
        public List<String> getInfluencingFactors() { return influencingFactors; }
        public void setInfluencingFactors(List<String> influencingFactors) { this.influencingFactors = influencingFactors; }
    }
    
    public static class SalesPrediction {
        private String categoryCode;
        private String categoryName;
        private LocalDate forecastPeriodStart;
        private LocalDate forecastPeriodEnd;
        private BigDecimal predictedRevenue;
        private Integer predictedOrderCount;
        private BigDecimal growthRate;
        private String marketTrend;
        private List<String> keyDrivers;
        
        public SalesPrediction() {}
        
        public SalesPrediction(String categoryCode, String categoryName, 
                             LocalDate forecastPeriodStart, LocalDate forecastPeriodEnd,
                             BigDecimal predictedRevenue, Integer predictedOrderCount) {
            this.categoryCode = categoryCode;
            this.categoryName = categoryName;
            this.forecastPeriodStart = forecastPeriodStart;
            this.forecastPeriodEnd = forecastPeriodEnd;
            this.predictedRevenue = predictedRevenue;
            this.predictedOrderCount = predictedOrderCount;
        }
        
        // Getters and Setters
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
        public LocalDate getForecastPeriodStart() { return forecastPeriodStart; }
        public void setForecastPeriodStart(LocalDate forecastPeriodStart) { this.forecastPeriodStart = forecastPeriodStart; }
        public LocalDate getForecastPeriodEnd() { return forecastPeriodEnd; }
        public void setForecastPeriodEnd(LocalDate forecastPeriodEnd) { this.forecastPeriodEnd = forecastPeriodEnd; }
        public BigDecimal getPredictedRevenue() { return predictedRevenue; }
        public void setPredictedRevenue(BigDecimal predictedRevenue) { this.predictedRevenue = predictedRevenue; }
        public Integer getPredictedOrderCount() { return predictedOrderCount; }
        public void setPredictedOrderCount(Integer predictedOrderCount) { this.predictedOrderCount = predictedOrderCount; }
        public BigDecimal getGrowthRate() { return growthRate; }
        public void setGrowthRate(BigDecimal growthRate) { this.growthRate = growthRate; }
        public String getMarketTrend() { return marketTrend; }
        public void setMarketTrend(String marketTrend) { this.marketTrend = marketTrend; }
        public List<String> getKeyDrivers() { return keyDrivers; }
        public void setKeyDrivers(List<String> keyDrivers) { this.keyDrivers = keyDrivers; }
    }
    
    public static class SeasonalFactor {
        private String season; // Q1, Q2, Q3, Q4, NEW_YEAR, EXAM_PERIOD, SUMMER, etc.
        private String categoryCode;
        private BigDecimal seasonalMultiplier;
        private String description;
        private LocalDate historicalPeakDate;
        
        public SeasonalFactor() {}
        
        public SeasonalFactor(String season, String categoryCode, BigDecimal seasonalMultiplier) {
            this.season = season;
            this.categoryCode = categoryCode;
            this.seasonalMultiplier = seasonalMultiplier;
        }
        
        // Getters and Setters
        public String getSeason() { return season; }
        public void setSeason(String season) { this.season = season; }
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public BigDecimal getSeasonalMultiplier() { return seasonalMultiplier; }
        public void setSeasonalMultiplier(BigDecimal seasonalMultiplier) { this.seasonalMultiplier = seasonalMultiplier; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDate getHistoricalPeakDate() { return historicalPeakDate; }
        public void setHistoricalPeakDate(LocalDate historicalPeakDate) { this.historicalPeakDate = historicalPeakDate; }
    }
    
    public static class PredictionConfidence {
        private BigDecimal overallConfidence;
        private String confidenceLevel; // HIGH, MEDIUM, LOW
        private String dataQuality; // EXCELLENT, GOOD, FAIR, POOR
        private Integer historicalDataPoints;
        private List<String> uncertaintyFactors;
        private String recommendation;
        
        public PredictionConfidence() {}
        
        public PredictionConfidence(BigDecimal overallConfidence, String confidenceLevel, 
                                  String dataQuality) {
            this.overallConfidence = overallConfidence;
            this.confidenceLevel = confidenceLevel;
            this.dataQuality = dataQuality;
        }
        
        // Getters and Setters
        public BigDecimal getOverallConfidence() { return overallConfidence; }
        public void setOverallConfidence(BigDecimal overallConfidence) { this.overallConfidence = overallConfidence; }
        public String getConfidenceLevel() { return confidenceLevel; }
        public void setConfidenceLevel(String confidenceLevel) { this.confidenceLevel = confidenceLevel; }
        public String getDataQuality() { return dataQuality; }
        public void setDataQuality(String dataQuality) { this.dataQuality = dataQuality; }
        public Integer getHistoricalDataPoints() { return historicalDataPoints; }
        public void setHistoricalDataPoints(Integer historicalDataPoints) { this.historicalDataPoints = historicalDataPoints; }
        public List<String> getUncertaintyFactors() { return uncertaintyFactors; }
        public void setUncertaintyFactors(List<String> uncertaintyFactors) { this.uncertaintyFactors = uncertaintyFactors; }
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    }
    
    // Main class getters and setters
    public LocalDate getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDate predictionDate) { this.predictionDate = predictionDate; }
    public String getPredictionType() { return predictionType; }
    public void setPredictionType(String predictionType) { this.predictionType = predictionType; }
    public String getTimeHorizon() { return timeHorizon; }
    public void setTimeHorizon(String timeHorizon) { this.timeHorizon = timeHorizon; }
    public BigDecimal getAccuracy() { return accuracy; }
    public void setAccuracy(BigDecimal accuracy) { this.accuracy = accuracy; }
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    public List<DemandPrediction> getDemandPredictions() { return demandPredictions; }
    public void setDemandPredictions(List<DemandPrediction> demandPredictions) { this.demandPredictions = demandPredictions; }
    public List<SalesPrediction> getSalesPredictions() { return salesPredictions; }
    public void setSalesPredictions(List<SalesPrediction> salesPredictions) { this.salesPredictions = salesPredictions; }
    public List<SeasonalFactor> getSeasonalFactors() { return seasonalFactors; }
    public void setSeasonalFactors(List<SeasonalFactor> seasonalFactors) { this.seasonalFactors = seasonalFactors; }
    public PredictionConfidence getConfidence() { return confidence; }
    public void setConfidence(PredictionConfidence confidence) { this.confidence = confidence; }
}