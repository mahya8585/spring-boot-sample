package com.techbookstore.app.service;

import com.techbookstore.app.dto.InventoryAnalysisDto;
import com.techbookstore.app.entity.Book;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for Seasonal Analysis of Technical Books
 * 技術専門書の季節性分析サービス
 */
@Service
@Transactional(readOnly = true)
public class SeasonalAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(SeasonalAnalysisService.class);

    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;

    // Tech book specific seasonal patterns - cached for performance
    private static final Map<String, SeasonalPattern> TECH_SEASONAL_PATTERNS;
    static {
        TECH_SEASONAL_PATTERNS = new HashMap<>();
        TECH_SEASONAL_PATTERNS.put("SPRING", new SeasonalPattern("新学期需要", 1.3, Arrays.asList(Month.MARCH, Month.APRIL)));
        TECH_SEASONAL_PATTERNS.put("SUMMER", new SeasonalPattern("夏休み学習", 1.2, Arrays.asList(Month.JULY, Month.AUGUST)));
        TECH_SEASONAL_PATTERNS.put("FALL", new SeasonalPattern("資格試験・就活", 1.4, Arrays.asList(Month.SEPTEMBER, Month.OCTOBER, Month.NOVEMBER)));
        TECH_SEASONAL_PATTERNS.put("WINTER", new SeasonalPattern("年末学習・新年準備", 1.1, Arrays.asList(Month.DECEMBER, Month.JANUARY)));
    }

    // Category-specific seasonal multipliers - cached for performance
    private static final Map<String, Map<String, BigDecimal>> CATEGORY_SEASONAL_MULTIPLIERS;
    static {
        CATEGORY_SEASONAL_MULTIPLIERS = new HashMap<>();
        
        Map<String, BigDecimal> javaMultipliers = new HashMap<>();
        javaMultipliers.put("SPRING", BigDecimal.valueOf(1.4)); // Spring framework popularity in new semester
        javaMultipliers.put("SUMMER", BigDecimal.valueOf(1.1));
        javaMultipliers.put("FALL", BigDecimal.valueOf(1.3)); // Job hunting season
        javaMultipliers.put("WINTER", BigDecimal.valueOf(1.0));
        CATEGORY_SEASONAL_MULTIPLIERS.put("JAVA", javaMultipliers);
        
        Map<String, BigDecimal> pythonMultipliers = new HashMap<>();
        pythonMultipliers.put("SPRING", BigDecimal.valueOf(1.2));
        pythonMultipliers.put("SUMMER", BigDecimal.valueOf(1.4)); // AI/ML summer courses
        pythonMultipliers.put("FALL", BigDecimal.valueOf(1.5)); // Data science job market
        pythonMultipliers.put("WINTER", BigDecimal.valueOf(1.1));
        CATEGORY_SEASONAL_MULTIPLIERS.put("PYTHON", pythonMultipliers);
        
        Map<String, BigDecimal> javascriptMultipliers = new HashMap<>();
        javascriptMultipliers.put("SPRING", BigDecimal.valueOf(1.3)); // New web development courses
        javascriptMultipliers.put("SUMMER", BigDecimal.valueOf(1.2));
        javascriptMultipliers.put("FALL", BigDecimal.valueOf(1.4)); // Job hunting season
        javascriptMultipliers.put("WINTER", BigDecimal.valueOf(1.0));
        CATEGORY_SEASONAL_MULTIPLIERS.put("JAVASCRIPT", javascriptMultipliers);
        
        Map<String, BigDecimal> databaseMultipliers = new HashMap<>();
        databaseMultipliers.put("SPRING", BigDecimal.valueOf(1.2)); // Database courses
        databaseMultipliers.put("SUMMER", BigDecimal.valueOf(1.1));
        databaseMultipliers.put("FALL", BigDecimal.valueOf(1.3)); // Enterprise planning season
        databaseMultipliers.put("WINTER", BigDecimal.valueOf(1.2)); // Year-end data analysis
        CATEGORY_SEASONAL_MULTIPLIERS.put("DATABASE", databaseMultipliers);
        
        Map<String, BigDecimal> aiMlMultipliers = new HashMap<>();
        aiMlMultipliers.put("SPRING", BigDecimal.valueOf(1.3)); // Academic semester
        aiMlMultipliers.put("SUMMER", BigDecimal.valueOf(1.5)); // Research and summer programs
        aiMlMultipliers.put("FALL", BigDecimal.valueOf(1.4)); // Job market demand
        aiMlMultipliers.put("WINTER", BigDecimal.valueOf(1.2));
        CATEGORY_SEASONAL_MULTIPLIERS.put("AI_ML", aiMlMultipliers);
        
        Map<String, BigDecimal> cloudMultipliers = new HashMap<>();
        cloudMultipliers.put("SPRING", BigDecimal.valueOf(1.2)); // Enterprise adoption cycles
        cloudMultipliers.put("SUMMER", BigDecimal.valueOf(1.1));
        cloudMultipliers.put("FALL", BigDecimal.valueOf(1.4)); // Budget planning season
        cloudMultipliers.put("WINTER", BigDecimal.valueOf(1.3)); // Year-end migrations
        CATEGORY_SEASONAL_MULTIPLIERS.put("CLOUD", cloudMultipliers);
    }
    
    // Core technology categories for analysis - cached for performance
    private static final List<String> CORE_TECH_CATEGORIES = Arrays.asList(
        "JAVA", "PYTHON", "JAVASCRIPT", "DATABASE", "AI_ML", "CLOUD"
    );

    public SeasonalAnalysisService(BookRepository bookRepository, OrderRepository orderRepository) {
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
    }

    /**
     * Generate seasonal inventory trend analysis
     * 季節性在庫トレンド分析を生成
     */
    public InventoryAnalysisDto.SeasonalInventoryTrend generateSeasonalInventoryTrend() {
        logger.info("Generating seasonal inventory trend analysis");

        String currentSeason = getCurrentSeason();
        SeasonalPattern pattern = TECH_SEASONAL_PATTERNS.get(currentSeason);
        
        // Calculate expected demand increase for current season
        BigDecimal expectedDemandIncrease = BigDecimal.valueOf(pattern.getDemandMultiplier())
            .setScale(2, RoundingMode.HALF_UP);
        
        // Get top categories for current season
        List<String> topCategories = getTopCategoriesForSeason(currentSeason);
        
        // Generate recommendation
        String recommendation = generateSeasonalRecommendation(currentSeason, topCategories);
        
        InventoryAnalysisDto.SeasonalInventoryTrend trend = new InventoryAnalysisDto.SeasonalInventoryTrend(
            currentSeason, expectedDemandIncrease, topCategories
        );
        trend.setRecommendation(recommendation);
        
        return trend;
    }

    /**
     * Analyze seasonal patterns for all categories
     * 全カテゴリの季節性パターンを分析
     */
    public Map<String, SeasonalCategoryAnalysis> analyzeSeasonalPatterns() {
        logger.info("Analyzing seasonal patterns for all categories");
        
        Map<String, SeasonalCategoryAnalysis> categoryAnalysis = new HashMap<>();
        
        // Use cached core tech categories for better performance
        for (String category : CORE_TECH_CATEGORIES) {
            try {
                SeasonalCategoryAnalysis analysis = analyzeCategorySeasonality(category);
                categoryAnalysis.put(category, analysis);
            } catch (Exception e) {
                logger.error("Failed to analyze seasonality for category: {}", category, e);
                // Continue with other categories
            }
        }
        
        return categoryAnalysis;
    }

    /**
     * Analyze seasonality for a specific category
     * 特定カテゴリの季節性を分析
     */
    public SeasonalCategoryAnalysis analyzeCategorySeasonality(String categoryCode) {
        logger.info("Analyzing seasonality for category: {}", categoryCode);
        
        // Calculate historical seasonal indices (mock implementation)
        Map<String, BigDecimal> seasonalIndices = calculateSeasonalIndices(categoryCode);
        
        // Determine peak and low seasons
        String peakSeason = seasonalIndices.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("FALL");
            
        String lowSeason = seasonalIndices.entrySet().stream()
            .min(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse("WINTER");
        
        // Calculate seasonal volatility
        double volatility = calculateSeasonalVolatility(seasonalIndices);
        
        return new SeasonalCategoryAnalysis(
            categoryCode,
            seasonalIndices,
            peakSeason,
            lowSeason,
            BigDecimal.valueOf(volatility)
        );
    }

    /**
     * Generate seasonal inventory recommendations
     * 季節性在庫推奨を生成
     */
    public List<SeasonalInventoryRecommendation> generateSeasonalRecommendations() {
        logger.info("Generating seasonal inventory recommendations");
        
        List<SeasonalInventoryRecommendation> recommendations = new ArrayList<>();
        String currentSeason = getCurrentSeason();
        String nextSeason = getNextSeason(currentSeason);
        
        // Use cached core tech categories for better performance
        for (String category : CORE_TECH_CATEGORIES) {
            try {
                SeasonalInventoryRecommendation recommendation = generateCategoryRecommendation(
                    category, currentSeason, nextSeason
                );
                recommendations.add(recommendation);
            } catch (Exception e) {
                logger.error("Failed to generate seasonal recommendation for category: {}", category, e);
                // Continue with other categories
            }
        }
        
        return recommendations;
    }

    /**
     * Calculate seasonal indices for a category
     * カテゴリの季節指数を計算
     */
    private Map<String, BigDecimal> calculateSeasonalIndices(String categoryCode) {
        Map<String, BigDecimal> indices = new HashMap<>();
        
        // Get category-specific multipliers or use defaults
        Map<String, BigDecimal> categoryMultipliers = CATEGORY_SEASONAL_MULTIPLIERS.get(categoryCode);
        
        if (categoryMultipliers != null) {
            indices.putAll(categoryMultipliers);
        } else {
            // Default seasonal pattern for unknown categories
            indices.put("SPRING", BigDecimal.valueOf(1.2));
            indices.put("SUMMER", BigDecimal.valueOf(1.1));
            indices.put("FALL", BigDecimal.valueOf(1.3));
            indices.put("WINTER", BigDecimal.valueOf(1.0));
        }
        
        return indices;
    }

    /**
     * Calculate seasonal volatility (coefficient of variation)
     * 季節変動性を計算（変動係数）
     */
    private double calculateSeasonalVolatility(Map<String, BigDecimal> seasonalIndices) {
        List<Double> values = seasonalIndices.values().stream()
            .map(BigDecimal::doubleValue)
            .collect(Collectors.toList());
        
        double mean = values.stream().mapToDouble(v -> v).average().orElse(1.0);
        double variance = values.stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average()
            .orElse(0.0);
        
        double stdDev = Math.sqrt(variance);
        return mean > 0 ? stdDev / mean : 0.0;
    }

    /**
     * Get current season based on current date
     * 現在の日付に基づく季節を取得
     */
    private String getCurrentSeason() {
        Month currentMonth = LocalDate.now().getMonth();
        
        if (currentMonth == Month.MARCH || currentMonth == Month.APRIL) {
            return "SPRING";
        } else if (currentMonth == Month.JULY || currentMonth == Month.AUGUST) {
            return "SUMMER";
        } else if (currentMonth == Month.SEPTEMBER || currentMonth == Month.OCTOBER || currentMonth == Month.NOVEMBER) {
            return "FALL";
        } else {
            return "WINTER";
        }
    }

    /**
     * Get next season
     * 次の季節を取得
     */
    private String getNextSeason(String currentSeason) {
        switch (currentSeason) {
            case "SPRING": return "SUMMER";
            case "SUMMER": return "FALL";
            case "FALL": return "WINTER";
            case "WINTER": return "SPRING";
            default: return "SPRING";
        }
    }

    /**
     * Get top categories for a specific season
     * 特定季節のトップカテゴリを取得
     */
    private List<String> getTopCategoriesForSeason(String season) {
        Map<String, BigDecimal> categoryRankings = new HashMap<>();
        
        for (Map.Entry<String, Map<String, BigDecimal>> entry : CATEGORY_SEASONAL_MULTIPLIERS.entrySet()) {
            String category = entry.getKey();
            BigDecimal seasonalMultiplier = entry.getValue().getOrDefault(season, BigDecimal.ONE);
            categoryRankings.put(category, seasonalMultiplier);
        }
        
        return categoryRankings.entrySet().stream()
            .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
            .limit(5)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    }

    /**
     * Generate seasonal recommendation text
     * 季節性推奨テキストを生成
     */
    private String generateSeasonalRecommendation(String season, List<String> topCategories) {
        SeasonalPattern pattern = TECH_SEASONAL_PATTERNS.get(season);
        String topCategory = topCategories.isEmpty() ? "技術書全般" : topCategories.get(0);
        
        return String.format("%s期間中は%sを重点に、%s分野で%.0f%%の需要増加が予想されます。" +
                           "在庫レベルを事前に調整し、マーケティング活動を強化することを推奨します。",
                           pattern.getDescription(),
                           topCategory,
                           String.join("、", topCategories),
                           (pattern.getDemandMultiplier() - 1) * 100);
    }

    /**
     * Generate category-specific seasonal recommendation
     * カテゴリ別季節性推奨を生成
     */
    private SeasonalInventoryRecommendation generateCategoryRecommendation(
            String category, String currentSeason, String nextSeason) {
        
        Map<String, BigDecimal> categoryMultipliers = CATEGORY_SEASONAL_MULTIPLIERS.get(category);
        if (categoryMultipliers == null) {
            categoryMultipliers = new HashMap<>();
            categoryMultipliers.put("SPRING", BigDecimal.valueOf(1.2));
            categoryMultipliers.put("SUMMER", BigDecimal.valueOf(1.1));
            categoryMultipliers.put("FALL", BigDecimal.valueOf(1.3));
            categoryMultipliers.put("WINTER", BigDecimal.valueOf(1.0));
        }
        
        BigDecimal currentMultiplier = categoryMultipliers.get(currentSeason);
        BigDecimal nextMultiplier = categoryMultipliers.get(nextSeason);
        
        String action;
        String reason;
        
        if (nextMultiplier.compareTo(currentMultiplier) > 0) {
            action = "在庫増加";
            reason = String.format("次期（%s）の需要増加（%.0f%% → %.0f%%）に備える",
                                 nextSeason,
                                 (currentMultiplier.doubleValue() - 1) * 100,
                                 (nextMultiplier.doubleValue() - 1) * 100);
        } else if (nextMultiplier.compareTo(currentMultiplier) < 0) {
            action = "在庫調整";
            reason = String.format("次期（%s）の需要減少（%.0f%% → %.0f%%）に対応",
                                 nextSeason,
                                 (currentMultiplier.doubleValue() - 1) * 100,
                                 (nextMultiplier.doubleValue() - 1) * 100);
        } else {
            action = "現状維持";
            reason = "季節変動が小さく、安定した需要が予想される";
        }
        
        return new SeasonalInventoryRecommendation(category, currentSeason, nextSeason, action, reason);
    }

    // Inner classes for data structures
    
    public static class SeasonalPattern {
        private final String description;
        private final double demandMultiplier;
        private final List<Month> months;
        
        public SeasonalPattern(String description, double demandMultiplier, List<Month> months) {
            this.description = description;
            this.demandMultiplier = demandMultiplier;
            this.months = months;
        }
        
        public String getDescription() { return description; }
        public double getDemandMultiplier() { return demandMultiplier; }
        public List<Month> getMonths() { return months; }
    }
    
    public static class SeasonalCategoryAnalysis {
        private final String categoryCode;
        private final Map<String, BigDecimal> seasonalIndices;
        private final String peakSeason;
        private final String lowSeason;
        private final BigDecimal volatility;
        
        public SeasonalCategoryAnalysis(String categoryCode, Map<String, BigDecimal> seasonalIndices,
                                      String peakSeason, String lowSeason, BigDecimal volatility) {
            this.categoryCode = categoryCode;
            this.seasonalIndices = seasonalIndices;
            this.peakSeason = peakSeason;
            this.lowSeason = lowSeason;
            this.volatility = volatility;
        }
        
        // Getters
        public String getCategoryCode() { return categoryCode; }
        public Map<String, BigDecimal> getSeasonalIndices() { return seasonalIndices; }
        public String getPeakSeason() { return peakSeason; }
        public String getLowSeason() { return lowSeason; }
        public BigDecimal getVolatility() { return volatility; }
    }
    
    public static class SeasonalInventoryRecommendation {
        private final String categoryCode;
        private final String currentSeason;
        private final String nextSeason;
        private final String recommendedAction;
        private final String reason;
        
        public SeasonalInventoryRecommendation(String categoryCode, String currentSeason, String nextSeason,
                                             String recommendedAction, String reason) {
            this.categoryCode = categoryCode;
            this.currentSeason = currentSeason;
            this.nextSeason = nextSeason;
            this.recommendedAction = recommendedAction;
            this.reason = reason;
        }
        
        // Getters
        public String getCategoryCode() { return categoryCode; }
        public String getCurrentSeason() { return currentSeason; }
        public String getNextSeason() { return nextSeason; }
        public String getRecommendedAction() { return recommendedAction; }
        public String getReason() { return reason; }
    }
}