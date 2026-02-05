package com.techbookstore.app.service;

import com.techbookstore.app.dto.TechCategoryAnalysisDto;
import com.techbookstore.app.entity.*;
import com.techbookstore.app.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for comprehensive tech trend analysis
 * 包括的技術トレンド分析サービス
 */
@Service
@Transactional(readOnly = true)
public class TechTrendAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(TechTrendAnalysisService.class);

    private final TechCategoryRepository techCategoryRepository;
    private final TechTrendAnalysisRepository trendAnalysisRepository;
    private final TechRelationshipRepository relationshipRepository;
    private final TechPredictionRepository predictionRepository;
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;

    public TechTrendAnalysisService(TechCategoryRepository techCategoryRepository,
                                  TechTrendAnalysisRepository trendAnalysisRepository,
                                  TechRelationshipRepository relationshipRepository,
                                  TechPredictionRepository predictionRepository,
                                  OrderRepository orderRepository,
                                  BookRepository bookRepository) {
        this.techCategoryRepository = techCategoryRepository;
        this.trendAnalysisRepository = trendAnalysisRepository;
        this.relationshipRepository = relationshipRepository;
        this.predictionRepository = predictionRepository;
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Generate comprehensive tech trend report
     * 包括的技術トレンドレポート生成
     */
    public Map<String, Object> generateTechTrendReport() {
        logger.info("Generating comprehensive tech trend report");
        
        Map<String, Object> report = new HashMap<>();
        
        // Overall market analysis
        report.put("marketOverview", generateMarketOverview());
        
        // Category analysis
        report.put("categoryAnalysis", generateAllCategoryAnalysis());
        
        // Emerging technologies
        report.put("emergingTechnologies", findEmergingTechnologies());
        
        // Technology correlations
        report.put("technologyCorrelations", generateTechnologyCorrelations());
        
        // Investment recommendations
        report.put("investmentRecommendations", generateInvestmentRecommendations());
        
        report.put("reportDate", LocalDate.now());
        
        return report;
    }

    /**
     * Analyze specific tech category trends
     * 特定技術カテゴリトレンド分析
     */
    public TechCategoryAnalysisDto analyzeTechCategoryTrends(String categoryCode) {
        logger.info("Analyzing tech trends for category: {}", categoryCode);
        
        Optional<TechCategory> categoryOpt = techCategoryRepository.findByCategoryCode(categoryCode);
        if (categoryOpt.isEmpty()) {
            throw new IllegalArgumentException("Tech category not found: " + categoryCode);
        }
        
        TechCategory category = categoryOpt.get();
        LocalDate analysisDate = LocalDate.now();
        
        TechCategoryAnalysisDto analysis = new TechCategoryAnalysisDto(analysisDate, categoryCode, category.getCategoryName());
        
        // Generate comprehensive metrics
        analysis.setMetrics(generateTechCategoryMetrics(category));
        analysis.setTrend(generateTechCategoryTrend(category));
        analysis.setCompetitiveTechnologies(generateCompetitiveTechAnalysis(category));
        analysis.setSubCategories(generateSubCategoryAnalysis(category));
        analysis.setLifecycle(generateTechLifecycleAnalysis(category));
        
        return analysis;
    }

    /**
     * Find emerging technologies based on various criteria
     * 複数の基準による新興技術検出
     */
    public List<Map<String, Object>> findEmergingTechnologies() {
        logger.info("Finding emerging technologies");
        
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        List<TechTrendAnalysis> emergingTrends = trendAnalysisRepository.findEmergingTechnologies(threeMonthsAgo);
        
        return emergingTrends.stream()
            .map(this::convertTrendAnalysisToMap)
            .collect(Collectors.toList());
    }

    /**
     * Generate technology correlation matrix
     * 技術相関マトリックス生成
     */
    public Map<String, Object> generateTechnologyCorrelations() {
        logger.info("Generating technology correlation matrix");
        
        List<TechRelationship> relationships = relationshipRepository.findStrongCorrelations(new BigDecimal("0.3"));
        
        Map<String, Object> correlationMatrix = new HashMap<>();
        correlationMatrix.put("relationships", relationships.stream()
            .map(this::convertRelationshipToMap)
            .collect(Collectors.toList()));
        correlationMatrix.put("totalRelationships", relationships.size());
        
        return correlationMatrix;
    }

    /**
     * Generate lifecycle distribution analysis
     * ライフサイクル分布分析
     */
    public Map<String, Object> generateLifecycleDistribution() {
        logger.info("Generating lifecycle distribution analysis");
        
        List<TechTrendAnalysis> latestAnalysis = trendAnalysisRepository.findLatestAnalysisForAllCategories();
        
        Map<TechTrendAnalysis.LifecycleStage, Long> distribution = latestAnalysis.stream()
            .filter(analysis -> analysis.getLifecycleStage() != null)
            .collect(Collectors.groupingBy(
                TechTrendAnalysis::getLifecycleStage,
                Collectors.counting()
            ));
        
        Map<String, Object> result = new HashMap<>();
        result.put("distribution", distribution);
        result.put("totalCategories", latestAnalysis.size());
        result.put("analysisDate", LocalDate.now());
        
        return result;
    }

    /**
     * Generate investment recommendations based on trend analysis
     * トレンド分析に基づく投資推奨
     */
    public List<Map<String, Object>> generateInvestmentRecommendations() {
        logger.info("Generating investment recommendations");
        
        List<TechTrendAnalysis> latestAnalysis = trendAnalysisRepository.findLatestAnalysisForAllCategories();
        
        return latestAnalysis.stream()
            .filter(analysis -> analysis.getInvestmentRecommendation() != null)
            .sorted((a, b) -> {
                // Sort by emerging score and growth rate
                BigDecimal scoreA = calculateInvestmentScore(a);
                BigDecimal scoreB = calculateInvestmentScore(b);
                return scoreB.compareTo(scoreA);
            })
            .limit(10)
            .map(this::convertAnalysisToInvestmentRecommendation)
            .collect(Collectors.toList());
    }

    // Private helper methods

    private Map<String, Object> generateMarketOverview() {
        Map<String, Object> overview = new HashMap<>();
        
        // Calculate overall market metrics
        List<TechTrendAnalysis> latestAnalysis = trendAnalysisRepository.findLatestAnalysisForAllCategories();
        
        BigDecimal totalRevenue = latestAnalysis.stream()
            .filter(analysis -> analysis.getTotalRevenue() != null)
            .map(TechTrendAnalysis::getTotalRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal avgGrowthRate = latestAnalysis.stream()
            .filter(analysis -> analysis.getGrowthRate() != null)
            .map(TechTrendAnalysis::getGrowthRate)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(new BigDecimal(latestAnalysis.size()), 2, RoundingMode.HALF_UP);
        
        overview.put("totalMarketRevenue", totalRevenue);
        overview.put("averageGrowthRate", avgGrowthRate);
        overview.put("totalCategories", latestAnalysis.size());
        overview.put("analysisDate", LocalDate.now());
        
        return overview;
    }

    private List<TechCategoryAnalysisDto> generateAllCategoryAnalysis() {
        List<TechCategory> topLevelCategories = techCategoryRepository.findTopLevelCategories();
        
        return topLevelCategories.stream()
            .map(category -> analyzeTechCategoryTrends(category.getCategoryCode()))
            .collect(Collectors.toList());
    }

    private TechCategoryAnalysisDto.TechCategoryMetrics generateTechCategoryMetrics(TechCategory category) {
        // Get latest trend analysis for the category
        List<TechTrendAnalysis> trends = trendAnalysisRepository.findByTechCategoryCode(category.getCategoryCode());
        
        if (trends.isEmpty()) {
            // Return default metrics if no analysis exists
            return new TechCategoryAnalysisDto.TechCategoryMetrics(0, BigDecimal.ZERO, BigDecimal.ZERO);
        }
        
        TechTrendAnalysis latest = trends.getFirst();
        
        TechCategoryAnalysisDto.TechCategoryMetrics metrics = new TechCategoryAnalysisDto.TechCategoryMetrics();
        metrics.setTotalRevenue(latest.getTotalRevenue() != null ? latest.getTotalRevenue() : BigDecimal.ZERO);
        metrics.setMarketShare(latest.getMarketShare() != null ? latest.getMarketShare() : BigDecimal.ZERO);
        metrics.setTotalBooks(latest.getTotalUnitsSold() != null ? latest.getTotalUnitsSold() : 0);
        
        return metrics;
    }

    private TechCategoryAnalysisDto.TechCategoryTrend generateTechCategoryTrend(TechCategory category) {
        List<TechTrendAnalysis> trends = trendAnalysisRepository.findByTechCategoryCode(category.getCategoryCode());
        
        if (trends.isEmpty()) {
            return new TechCategoryAnalysisDto.TechCategoryTrend("STABLE", BigDecimal.ZERO, "LOW");
        }
        
        TechTrendAnalysis latest = trends.getFirst();
        
        String trendDirection = latest.getTrendDirection() != null ? latest.getTrendDirection().name() : "STABLE";
        BigDecimal growthRate = latest.getGrowthRate() != null ? latest.getGrowthRate() : BigDecimal.ZERO;
        
        return new TechCategoryAnalysisDto.TechCategoryTrend(trendDirection, growthRate, "MEDIUM");
    }

    private List<TechCategoryAnalysisDto.CompetitiveTech> generateCompetitiveTechAnalysis(TechCategory category) {
        List<TechRelationship> relationships = relationshipRepository.findRelationshipsForCategory(category.getCategoryCode());
        
        return relationships.stream()
            .filter(rel -> rel.getRelationshipType() == TechRelationship.RelationshipType.COMPETITIVE)
            .map(rel -> {
                TechCategory relatedTech = rel.getRelatedTech().getId().equals(category.getId()) 
                    ? rel.getPrimaryTech() : rel.getRelatedTech();
                
                TechCategoryAnalysisDto.CompetitiveTech comp = new TechCategoryAnalysisDto.CompetitiveTech();
                comp.setTechName(relatedTech.getCategoryName());
                comp.setCompetitorCategory(relatedTech.getCategoryCode());
                comp.setMarketShareImpact(rel.getCorrelationStrength().abs());
                comp.setRelationshipType("COMPETITIVE");
                
                return comp;
            })
            .collect(Collectors.toList());
    }

    private List<TechCategoryAnalysisDto.SubCategoryAnalysis> generateSubCategoryAnalysis(TechCategory category) {
        List<TechCategory> subCategories = techCategoryRepository.findByParent(category);
        
        return subCategories.stream()
            .map(subCat -> {
                TechCategoryAnalysisDto.SubCategoryAnalysis analysis = new TechCategoryAnalysisDto.SubCategoryAnalysis();
                analysis.setSubCategoryCode(subCat.getCategoryCode());
                analysis.setSubCategoryName(subCat.getCategoryName());
                
                // Get revenue and market share for subcategory
                List<TechTrendAnalysis> subTrends = trendAnalysisRepository.findByTechCategoryCode(subCat.getCategoryCode());
                if (!subTrends.isEmpty()) {
                    TechTrendAnalysis latest = subTrends.getFirst();
                    analysis.setRevenue(latest.getTotalRevenue());
                    analysis.setMarketShare(latest.getMarketShare());
                }
                
                return analysis;
            })
            .collect(Collectors.toList());
    }

    private TechCategoryAnalysisDto.TechLifecycleAnalysis generateTechLifecycleAnalysis(TechCategory category) {
        List<TechTrendAnalysis> trends = trendAnalysisRepository.findByTechCategoryCode(category.getCategoryCode());
        
        if (trends.isEmpty()) {
            return new TechCategoryAnalysisDto.TechLifecycleAnalysis("MATURITY", 18, "STABLE");
        }
        
        TechTrendAnalysis latest = trends.getFirst();
        String currentStage = latest.getLifecycleStage() != null ? latest.getLifecycleStage().name() : "MATURITY";
        
        TechCategoryAnalysisDto.TechLifecycleAnalysis lifecycle = new TechCategoryAnalysisDto.TechLifecycleAnalysis();
        lifecycle.setCurrentStage(currentStage);
        lifecycle.setEstimatedMonthsInStage(18); // Default estimate
        lifecycle.setNextStageProjection("STABLE");
        lifecycle.setInvestmentRecommendation(latest.getInvestmentRecommendation());
        
        return lifecycle;
    }

    private Map<String, Object> convertTrendAnalysisToMap(TechTrendAnalysis analysis) {
        Map<String, Object> map = new HashMap<>();
        map.put("categoryCode", analysis.getTechCategory().getCategoryCode());
        map.put("categoryName", analysis.getTechCategory().getCategoryName());
        map.put("emergingScore", analysis.getEmergingScore());
        map.put("growthRate", analysis.getGrowthRate());
        map.put("lifecycleStage", analysis.getLifecycleStage());
        map.put("analysisDate", analysis.getAnalysisDate());
        return map;
    }

    private Map<String, Object> convertRelationshipToMap(TechRelationship relationship) {
        Map<String, Object> map = new HashMap<>();
        map.put("primaryTech", relationship.getPrimaryTech().getCategoryName());
        map.put("relatedTech", relationship.getRelatedTech().getCategoryName());
        map.put("relationshipType", relationship.getRelationshipType());
        map.put("correlationStrength", relationship.getCorrelationStrength());
        map.put("confidenceLevel", relationship.getConfidenceLevel());
        return map;
    }

    private BigDecimal calculateInvestmentScore(TechTrendAnalysis analysis) {
        BigDecimal score = BigDecimal.ZERO;
        
        if (analysis.getEmergingScore() != null) {
            score = score.add(analysis.getEmergingScore().multiply(new BigDecimal("0.4")));
        }
        
        if (analysis.getGrowthRate() != null) {
            score = score.add(analysis.getGrowthRate().multiply(new BigDecimal("0.3")));
        }
        
        if (analysis.getMarketShare() != null) {
            score = score.add(analysis.getMarketShare().multiply(new BigDecimal("0.3")));
        }
        
        return score;
    }

    private Map<String, Object> convertAnalysisToInvestmentRecommendation(TechTrendAnalysis analysis) {
        Map<String, Object> recommendation = new HashMap<>();
        recommendation.put("categoryCode", analysis.getTechCategory().getCategoryCode());
        recommendation.put("categoryName", analysis.getTechCategory().getCategoryName());
        recommendation.put("lifecycleStage", analysis.getLifecycleStage());
        recommendation.put("growthRate", analysis.getGrowthRate());
        recommendation.put("investmentScore", calculateInvestmentScore(analysis));
        recommendation.put("recommendation", analysis.getInvestmentRecommendation());
        
        return recommendation;
    }
}