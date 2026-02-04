package com.techbookstore.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Comprehensive DTO for Phase 4 integrated inventory analysis
 * Combines data from all previous phases into a unified response
 */
public class IntegratedAnalysisResult {
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime analysisTimestamp;
    
    private String analysisId;
    private String status; // COMPLETED, IN_PROGRESS, FAILED
    private Long executionTimeMs;
    
    // Phase 1: Base Reports
    private InventoryReportDto baseReport;
    
    // Phase 2: Advanced Analysis
    private AdvancedAnalysisData advancedAnalysis;
    
    // Phase 3: Forecasting
    private ForecastingData forecasting;
    
    // Phase 4: Integration & Optimization
    private OptimizationData optimization;
    
    // Performance Metrics
    private PerformanceMetrics performanceMetrics;
    
    // Constructors
    public IntegratedAnalysisResult() {
        this.analysisTimestamp = LocalDateTime.now();
        this.status = "IN_PROGRESS";
    }
    
    public IntegratedAnalysisResult(String analysisId) {
        this();
        this.analysisId = analysisId;
    }
    
    // Inner classes for structured data
    public static class AdvancedAnalysisData {
        private List<ABCXYZAnalysisResult> abcxyzResults;
        private InventoryAnalysisDto inventoryAnalysis;
        private Map<String, Object> additionalMetrics;
        
        // Getters and setters
        public List<ABCXYZAnalysisResult> getAbcxyzResults() { return abcxyzResults; }
        public void setAbcxyzResults(List<ABCXYZAnalysisResult> abcxyzResults) { this.abcxyzResults = abcxyzResults; }
        
        public InventoryAnalysisDto getInventoryAnalysis() { return inventoryAnalysis; }
        public void setInventoryAnalysis(InventoryAnalysisDto inventoryAnalysis) { this.inventoryAnalysis = inventoryAnalysis; }
        
        public Map<String, Object> getAdditionalMetrics() { return additionalMetrics; }
        public void setAdditionalMetrics(Map<String, Object> additionalMetrics) { this.additionalMetrics = additionalMetrics; }
    }
    
    public static class ForecastingData {
        private List<DemandForecastResult> demandForecasts;
        private List<OptimalStockLevel> optimalLevels;
        private Map<String, BigDecimal> forecastAccuracy;
        
        // Getters and setters
        public List<DemandForecastResult> getDemandForecasts() { return demandForecasts; }
        public void setDemandForecasts(List<DemandForecastResult> demandForecasts) { this.demandForecasts = demandForecasts; }
        
        public List<OptimalStockLevel> getOptimalLevels() { return optimalLevels; }
        public void setOptimalLevels(List<OptimalStockLevel> optimalLevels) { this.optimalLevels = optimalLevels; }
        
        public Map<String, BigDecimal> getForecastAccuracy() { return forecastAccuracy; }
        public void setForecastAccuracy(Map<String, BigDecimal> forecastAccuracy) { this.forecastAccuracy = forecastAccuracy; }
    }
    
    public static class OptimizationData {
        private List<StockOptimizationRecommendation> recommendations;
        private BigDecimal potentialCostSavings;
        private BigDecimal currentEfficiency;
        private BigDecimal optimizedEfficiency;
        private Map<String, Object> optimizationMetrics;
        
        // Getters and setters
        public List<StockOptimizationRecommendation> getRecommendations() { return recommendations; }
        public void setRecommendations(List<StockOptimizationRecommendation> recommendations) { this.recommendations = recommendations; }
        
        public BigDecimal getPotentialCostSavings() { return potentialCostSavings; }
        public void setPotentialCostSavings(BigDecimal potentialCostSavings) { this.potentialCostSavings = potentialCostSavings; }
        
        public BigDecimal getCurrentEfficiency() { return currentEfficiency; }
        public void setCurrentEfficiency(BigDecimal currentEfficiency) { this.currentEfficiency = currentEfficiency; }
        
        public BigDecimal getOptimizedEfficiency() { return optimizedEfficiency; }
        public void setOptimizedEfficiency(BigDecimal optimizedEfficiency) { this.optimizedEfficiency = optimizedEfficiency; }
        
        public Map<String, Object> getOptimizationMetrics() { return optimizationMetrics; }
        public void setOptimizationMetrics(Map<String, Object> optimizationMetrics) { this.optimizationMetrics = optimizationMetrics; }
    }
    
    public static class PerformanceMetrics {
        private Long totalAnalysisTime;
        private Long cacheHitRate;
        private Long dataConsistencyScore;
        private Map<String, Long> phaseExecutionTimes;
        
        // Getters and setters
        public Long getTotalAnalysisTime() { return totalAnalysisTime; }
        public void setTotalAnalysisTime(Long totalAnalysisTime) { this.totalAnalysisTime = totalAnalysisTime; }
        
        public Long getCacheHitRate() { return cacheHitRate; }
        public void setCacheHitRate(Long cacheHitRate) { this.cacheHitRate = cacheHitRate; }
        
        public Long getDataConsistencyScore() { return dataConsistencyScore; }
        public void setDataConsistencyScore(Long dataConsistencyScore) { this.dataConsistencyScore = dataConsistencyScore; }
        
        public Map<String, Long> getPhaseExecutionTimes() { return phaseExecutionTimes; }
        public void setPhaseExecutionTimes(Map<String, Long> phaseExecutionTimes) { this.phaseExecutionTimes = phaseExecutionTimes; }
    }
    
    public static class StockOptimizationRecommendation {
        private Long bookId;
        private String bookTitle;
        private String recommendationType; // INCREASE, DECREASE, MAINTAIN, DISCONTINUE
        private Integer currentStock;
        private Integer recommendedStock;
        private BigDecimal potentialSavings;
        private String reasoning;
        
        // Constructors
        public StockOptimizationRecommendation() {}
        
        public StockOptimizationRecommendation(Long bookId, String bookTitle, String recommendationType, 
                                             Integer currentStock, Integer recommendedStock, 
                                             BigDecimal potentialSavings, String reasoning) {
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.recommendationType = recommendationType;
            this.currentStock = currentStock;
            this.recommendedStock = recommendedStock;
            this.potentialSavings = potentialSavings;
            this.reasoning = reasoning;
        }
        
        // Getters and setters
        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        
        public String getBookTitle() { return bookTitle; }
        public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
        
        public String getRecommendationType() { return recommendationType; }
        public void setRecommendationType(String recommendationType) { this.recommendationType = recommendationType; }
        
        public Integer getCurrentStock() { return currentStock; }
        public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
        
        public Integer getRecommendedStock() { return recommendedStock; }
        public void setRecommendedStock(Integer recommendedStock) { this.recommendedStock = recommendedStock; }
        
        public BigDecimal getPotentialSavings() { return potentialSavings; }
        public void setPotentialSavings(BigDecimal potentialSavings) { this.potentialSavings = potentialSavings; }
        
        public String getReasoning() { return reasoning; }
        public void setReasoning(String reasoning) { this.reasoning = reasoning; }
    }
    
    // Main getters and setters
    public LocalDateTime getAnalysisTimestamp() { return analysisTimestamp; }
    public void setAnalysisTimestamp(LocalDateTime analysisTimestamp) { this.analysisTimestamp = analysisTimestamp; }
    
    public String getAnalysisId() { return analysisId; }
    public void setAnalysisId(String analysisId) { this.analysisId = analysisId; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(Long executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    
    public InventoryReportDto getBaseReport() { return baseReport; }
    public void setBaseReport(InventoryReportDto baseReport) { this.baseReport = baseReport; }
    
    public AdvancedAnalysisData getAdvancedAnalysis() { return advancedAnalysis; }
    public void setAdvancedAnalysis(AdvancedAnalysisData advancedAnalysis) { this.advancedAnalysis = advancedAnalysis; }
    
    public ForecastingData getForecasting() { return forecasting; }
    public void setForecasting(ForecastingData forecasting) { this.forecasting = forecasting; }
    
    public OptimizationData getOptimization() { return optimization; }
    public void setOptimization(OptimizationData optimization) { this.optimization = optimization; }
    
    public PerformanceMetrics getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(PerformanceMetrics performanceMetrics) { this.performanceMetrics = performanceMetrics; }
}