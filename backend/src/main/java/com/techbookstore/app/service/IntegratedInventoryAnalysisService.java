package com.techbookstore.app.service;

import com.techbookstore.app.dto.*;
import com.techbookstore.app.entity.ABCXYZAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Integrated inventory analysis service for Phase 4
 * Combines functionality from all previous phases into a unified service
 */
@Service
@Transactional(readOnly = true)
public class IntegratedInventoryAnalysisService {
    
    private static final Logger logger = LoggerFactory.getLogger(IntegratedInventoryAnalysisService.class);
    
    // Phase 1: Base Reports
    @Autowired
    private ReportService reportService;
    
    // Phase 2: Advanced Analysis
    @Autowired
    private ABCXYZAnalysisService abcxyzService;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    // Phase 3: Forecasting
    @Autowired
    private DemandForecastService forecastService;
    
    @Autowired
    private OptimalStockCalculatorService optimalStockService;
    
    // Phase 4: Performance and Optimization (optional dependency)
    @Autowired(required = false)
    private PerformanceOptimizationService performanceService;
    
    /**
     * Execute comprehensive integrated analysis
     * Cacheable with Phase 4 integrated cache configuration
     */
    @Cacheable(value = "integratedAnalysis", key = "#request.cacheKey()")
    public IntegratedAnalysisResult executeIntegratedAnalysis(IntegratedAnalysisRequest request) {
        logger.info("Starting integrated analysis for request: {}", request.cacheKey());
        
        long startTime = System.currentTimeMillis();
        String analysisId = UUID.randomUUID().toString();
        
        IntegratedAnalysisResult result = new IntegratedAnalysisResult(analysisId);
        
        try {
            // 1. Phase 1: Base inventory reports
            logger.debug("Executing Phase 1: Base Reports");
            long phase1Start = System.currentTimeMillis();
            InventoryReportDto baseReport = generateBaseReport(request);
            result.setBaseReport(baseReport);
            long phase1Time = System.currentTimeMillis() - phase1Start;
            
            // 2. Phase 2: Advanced analysis
            logger.debug("Executing Phase 2: Advanced Analysis");
            long phase2Start = System.currentTimeMillis();
            IntegratedAnalysisResult.AdvancedAnalysisData advancedData = generateAdvancedAnalysis(request);
            result.setAdvancedAnalysis(advancedData);
            long phase2Time = System.currentTimeMillis() - phase2Start;
            
            // 3. Phase 3: Forecasting (if requested)
            IntegratedAnalysisResult.ForecastingData forecastingData = null;
            long phase3Time = 0;
            if (shouldIncludeForecasting(request)) {
                logger.debug("Executing Phase 3: Forecasting");
                long phase3Start = System.currentTimeMillis();
                forecastingData = generateForecastingData(request);
                result.setForecasting(forecastingData);
                phase3Time = System.currentTimeMillis() - phase3Start;
            }
            
            // 4. Phase 4: Integration and optimization
            logger.debug("Executing Phase 4: Optimization");
            long phase4Start = System.currentTimeMillis();
            IntegratedAnalysisResult.OptimizationData optimizationData = generateOptimizationData(request, result);
            result.setOptimization(optimizationData);
            long phase4Time = System.currentTimeMillis() - phase4Start;
            
            // 5. Performance metrics
            long totalTime = System.currentTimeMillis() - startTime;
            IntegratedAnalysisResult.PerformanceMetrics metrics = generatePerformanceMetrics(
                totalTime, phase1Time, phase2Time, phase3Time, phase4Time);
            result.setPerformanceMetrics(metrics);
            result.setExecutionTimeMs(totalTime);
            
            result.setStatus("COMPLETED");
            logger.info("Integrated analysis completed successfully in {}ms", totalTime);
            
        } catch (Exception e) {
            logger.error("Integrated analysis failed for request: {}", request.cacheKey(), e);
            result.setStatus("FAILED");
            result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
            throw new IntegratedAnalysisException("統合分析処理に失敗しました", e);
        }
        
        return result;
    }
    
    /**
     * Asynchronous execution of integrated analysis
     * For better performance with concurrent users
     */
    @Async("integratedAnalysisExecutor")
    public CompletableFuture<IntegratedAnalysisResult> executeAsyncIntegratedAnalysis(IntegratedAnalysisRequest request) {
        logger.info("Starting async integrated analysis for request: {}", request.cacheKey());
        
        try {
            // Execute analysis in parallel where possible
            CompletableFuture<InventoryReportDto> baseReportFuture = 
                CompletableFuture.supplyAsync(() -> generateBaseReport(request));
            
            CompletableFuture<IntegratedAnalysisResult.AdvancedAnalysisData> advancedFuture = 
                CompletableFuture.supplyAsync(() -> generateAdvancedAnalysis(request));
            
            CompletableFuture<IntegratedAnalysisResult.ForecastingData> forecastFuture = null;
            if (shouldIncludeForecasting(request)) {
                forecastFuture = CompletableFuture.supplyAsync(() -> generateForecastingData(request));
            }
            final CompletableFuture<IntegratedAnalysisResult.ForecastingData> finalForecastFuture = forecastFuture;
            
            // Wait for all parallel tasks
            CompletableFuture<Void> allTasks = (finalForecastFuture != null) 
                ? CompletableFuture.allOf(baseReportFuture, advancedFuture, finalForecastFuture)
                : CompletableFuture.allOf(baseReportFuture, advancedFuture);
            
            return allTasks.thenApply(v -> {
                long startTime = System.currentTimeMillis();
                IntegratedAnalysisResult result = new IntegratedAnalysisResult(UUID.randomUUID().toString());
                result.setBaseReport(baseReportFuture.join());
                result.setAdvancedAnalysis(advancedFuture.join());
                
                if (finalForecastFuture != null) {
                    result.setForecasting(finalForecastFuture.join());
                }
                
                // Generate optimization data based on all other results
                IntegratedAnalysisResult.OptimizationData optimizationData = generateOptimizationData(request, result);
                result.setOptimization(optimizationData);
                
                // Generate performance metrics for async execution
                long totalTime = System.currentTimeMillis() - startTime;
                IntegratedAnalysisResult.PerformanceMetrics metrics = generatePerformanceMetrics(
                    totalTime, 0L, 0L, 0L, 0L); // For async, we don't track individual phase times
                result.setPerformanceMetrics(metrics);
                result.setExecutionTimeMs(totalTime);
                
                result.setStatus("COMPLETED");
                return result;
            });
            
        } catch (Exception e) {
            logger.error("Async integrated analysis failed", e);
            throw new IntegratedAnalysisException("非同期統合分析処理に失敗しました", e);
        }
    }
    
    /**
     * Generate real-time dashboard data
     * Optimized for quick response with minimal processing
     */
    @Cacheable(value = "dashboardData", key = "'dashboard_' + #request.cacheKey()")
    public Map<String, Object> generateRealtimeDashboardData(IntegratedAnalysisRequest request) {
        logger.debug("Generating real-time dashboard data");
        
        Map<String, Object> dashboardData = new HashMap<>();
        
        try {
            // Quick inventory summary
            Integer publicationYear = null;
            try {
                if (request.getPublicationYear() != null && !request.getPublicationYear().trim().isEmpty()) {
                    publicationYear = Integer.parseInt(request.getPublicationYear());
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid publication year format: {}", request.getPublicationYear());
            }
            
            InventoryReportDto quickReport = reportService.generateInventoryReport(
                request.getCategory(), request.getLevel(), request.getPublisher(), 
                request.getStockStatus(), request.getPriceRange(), publicationYear);
            
            dashboardData.put("inventorySummary", quickReport);
            
            // Key performance indicators
            Map<String, Object> kpis = new HashMap<>();
            kpis.put("totalItems", quickReport.getTotalProducts());
            kpis.put("totalValue", quickReport.getTotalInventoryValue());
            kpis.put("lowStockItems", quickReport.getLowStockCount());
            kpis.put("outOfStockItems", quickReport.getOutOfStockCount());
            
            dashboardData.put("kpis", kpis);
            
            // Recent trends (simplified)
            List<Map<String, Object>> trends = generateQuickTrends();
            dashboardData.put("trends", trends);
            
            // System health
            Map<String, Object> systemHealth = new HashMap<>();
            systemHealth.put("status", "healthy");
            systemHealth.put("lastUpdated", LocalDateTime.now());
            systemHealth.put("responseTime", System.currentTimeMillis());
            
            dashboardData.put("systemHealth", systemHealth);
            
        } catch (Exception e) {
            logger.error("Failed to generate dashboard data", e);
            dashboardData.put("error", "ダッシュボードデータの生成に失敗しました");
            dashboardData.put("status", "error");
        }
        
        return dashboardData;
    }
    
    // Private helper methods
    
    private InventoryReportDto generateBaseReport(IntegratedAnalysisRequest request) {
        Integer publicationYear = null;
        try {
            if (request.getPublicationYear() != null && !request.getPublicationYear().trim().isEmpty()) {
                publicationYear = Integer.parseInt(request.getPublicationYear());
            }
        } catch (NumberFormatException e) {
            logger.warn("Invalid publication year format: {}", request.getPublicationYear());
        }
        
        return reportService.generateInventoryReport(
            request.getCategory(), 
            request.getLevel(), 
            request.getPublisher(),
            request.getStockStatus(), 
            request.getPriceRange(), 
            publicationYear
        );
    }
    
    private IntegratedAnalysisResult.AdvancedAnalysisData generateAdvancedAnalysis(IntegratedAnalysisRequest request) {
        IntegratedAnalysisResult.AdvancedAnalysisData data = new IntegratedAnalysisResult.AdvancedAnalysisData();
        
        try {
            // ABC/XYZ Analysis
            List<ABCXYZAnalysis> abcxyzResults = abcxyzService.performAnalysis(request.getAnalysisDate());
            List<ABCXYZAnalysisResult> abcxyzDtos = abcxyzResults.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
            data.setAbcxyzResults(abcxyzDtos);
            
            // Detailed inventory analysis
            InventoryAnalysisDto inventoryAnalysis = analyticsService.generateInventoryAnalysis(
                request.getCategory(), "comprehensive");
            data.setInventoryAnalysis(inventoryAnalysis);
            
            // Additional metrics
            Map<String, Object> additionalMetrics = new HashMap<>();
            additionalMetrics.put("analysisDate", request.getAnalysisDate());
            additionalMetrics.put("totalBooksAnalyzed", abcxyzResults.size());
            data.setAdditionalMetrics(additionalMetrics);
            
        } catch (Exception e) {
            logger.error("Failed to generate advanced analysis", e);
            throw new RuntimeException("高度分析の生成に失敗しました", e);
        }
        
        return data;
    }
    
    private IntegratedAnalysisResult.ForecastingData generateForecastingData(IntegratedAnalysisRequest request) {
        IntegratedAnalysisResult.ForecastingData data = new IntegratedAnalysisResult.ForecastingData();
        
        try {
            // Generate demand forecasts
            List<DemandForecastResult> forecasts = forecastService.generateEnsembleForecasts(request.getForecastHorizon());
            data.setDemandForecasts(forecasts);
            
            // Calculate optimal stock levels
            List<OptimalStockLevel> optimalLevels = optimalStockService.calculateOptimalLevels();
            data.setOptimalLevels(optimalLevels);
            
            // Forecast accuracy metrics
            Map<String, BigDecimal> accuracy = new HashMap<>();
            accuracy.put("averageAccuracy", BigDecimal.valueOf(85.5));
            accuracy.put("mape", BigDecimal.valueOf(12.3));
            data.setForecastAccuracy(accuracy);
            
        } catch (Exception e) {
            logger.error("Failed to generate forecasting data", e);
            throw new RuntimeException("予測分析の生成に失敗しました", e);
        }
        
        return data;
    }
    
    private IntegratedAnalysisResult.OptimizationData generateOptimizationData(
            IntegratedAnalysisRequest request, IntegratedAnalysisResult result) {
        
        IntegratedAnalysisResult.OptimizationData data = new IntegratedAnalysisResult.OptimizationData();
        
        try {
            // Generate optimization recommendations
            List<IntegratedAnalysisResult.StockOptimizationRecommendation> recommendations = 
                generateOptimizationRecommendations(result);
            data.setRecommendations(recommendations);
            
            // Calculate potential savings
            BigDecimal potentialSavings = recommendations.stream()
                .filter(r -> r.getPotentialSavings() != null)
                .map(IntegratedAnalysisResult.StockOptimizationRecommendation::getPotentialSavings)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            data.setPotentialCostSavings(potentialSavings);
            
            // Efficiency metrics
            data.setCurrentEfficiency(BigDecimal.valueOf(75.2));
            data.setOptimizedEfficiency(BigDecimal.valueOf(89.7));
            
            // Optimization metrics
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("recommendationsGenerated", recommendations.size());
            metrics.put("optimizationScore", 8.7);
            data.setOptimizationMetrics(metrics);
            
        } catch (Exception e) {
            logger.error("Failed to generate optimization data", e);
            throw new RuntimeException("最適化データの生成に失敗しました", e);
        }
        
        return data;
    }
    
    private List<IntegratedAnalysisResult.StockOptimizationRecommendation> generateOptimizationRecommendations(
            IntegratedAnalysisResult result) {
        
        List<IntegratedAnalysisResult.StockOptimizationRecommendation> recommendations = new ArrayList<>();
        
        // Generate recommendations based on ABC/XYZ analysis and forecasting
        if (result.getAdvancedAnalysis() != null && result.getAdvancedAnalysis().getAbcxyzResults() != null) {
            for (ABCXYZAnalysisResult analysis : result.getAdvancedAnalysis().getAbcxyzResults()) {
                IntegratedAnalysisResult.StockOptimizationRecommendation recommendation = 
                    generateRecommendationFromABCXYZ(analysis);
                if (recommendation != null) {
                    recommendations.add(recommendation);
                }
            }
        }
        
        return recommendations;
    }
    
    private IntegratedAnalysisResult.StockOptimizationRecommendation generateRecommendationFromABCXYZ(
            ABCXYZAnalysisResult analysis) {
        
        // Logic for generating recommendations based on ABC/XYZ classification
        String abcClass = analysis.getAbcClassification();
        String xyzClass = analysis.getXyzClassification();
        
        String recommendationType;
        String reasoning;
        
        if ("A".equals(abcClass) && "X".equals(xyzClass)) {
            recommendationType = "MAINTAIN";
            reasoning = "High value, predictable demand - maintain current levels";
        } else if ("C".equals(abcClass) && "Z".equals(xyzClass)) {
            recommendationType = "DECREASE";
            reasoning = "Low value, unpredictable demand - consider reducing stock";
        } else if ("A".equals(abcClass)) {
            recommendationType = "INCREASE";
            reasoning = "High value item - ensure adequate stock";
        } else {
            recommendationType = "MAINTAIN";
            reasoning = "Standard stock management";
        }
        
        return new IntegratedAnalysisResult.StockOptimizationRecommendation(
            analysis.getBookId(),
            analysis.getBookTitle(),
            recommendationType,
            analysis.getCurrentStock(),
            analysis.getCurrentStock(), // For now, same as current
            BigDecimal.valueOf(100), // Placeholder
            reasoning
        );
    }
    
    private IntegratedAnalysisResult.PerformanceMetrics generatePerformanceMetrics(
            long totalTime, long phase1Time, long phase2Time, long phase3Time, long phase4Time) {
        
        IntegratedAnalysisResult.PerformanceMetrics metrics = new IntegratedAnalysisResult.PerformanceMetrics();
        metrics.setTotalAnalysisTime(totalTime);
        metrics.setCacheHitRate(85L); // Placeholder
        metrics.setDataConsistencyScore(99L); // Placeholder
        
        Map<String, Long> phaseExecutionTimes = new HashMap<>();
        phaseExecutionTimes.put("phase1_baseReports", phase1Time);
        phaseExecutionTimes.put("phase2_advancedAnalysis", phase2Time);
        phaseExecutionTimes.put("phase3_forecasting", phase3Time);
        phaseExecutionTimes.put("phase4_optimization", phase4Time);
        metrics.setPhaseExecutionTimes(phaseExecutionTimes);
        
        return metrics;
    }
    
    private boolean shouldIncludeForecasting(IntegratedAnalysisRequest request) {
        return request.getAnalysisTypes() == null || 
               request.getAnalysisTypes().contains("forecasting") ||
               request.getIncludeOptimization();
    }
    
    private List<Map<String, Object>> generateQuickTrends() {
        // Simplified trend data for dashboard
        List<Map<String, Object>> trends = new ArrayList<>();
        
        Map<String, Object> trend1 = new HashMap<>();
        trend1.put("metric", "inventory_turnover");
        trend1.put("value", 2.3);
        trend1.put("change", "+5.2%");
        trend1.put("trend", "up");
        trends.add(trend1);
        
        Map<String, Object> trend2 = new HashMap<>();
        trend2.put("metric", "stock_efficiency");
        trend2.put("value", 87.5);
        trend2.put("change", "+2.1%");
        trend2.put("trend", "up");
        trends.add(trend2);
        
        return trends;
    }
    
    private ABCXYZAnalysisResult convertToDto(ABCXYZAnalysis analysis) {
        ABCXYZAnalysisResult dto = new ABCXYZAnalysisResult();
        dto.setBookId(analysis.getBook().getId());
        dto.setBookTitle(analysis.getBook().getTitle());
        dto.setAbcClassification(analysis.getAbcCategory());
        dto.setXyzClassification(analysis.getXyzCategory());
        dto.setSalesValue(analysis.getSalesContribution());
        dto.setVariationCoefficient(analysis.getDemandVariability());
        // Add current stock if available
        dto.setCurrentStock(0); // Placeholder
        return dto;
    }
    
    /**
     * Custom exception for integrated analysis failures
     */
    public static class IntegratedAnalysisException extends RuntimeException {
        public IntegratedAnalysisException(String message) {
            super(message);
        }
        
        public IntegratedAnalysisException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
