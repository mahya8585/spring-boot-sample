package com.techbookstore.app.service;

import com.techbookstore.app.dto.OrderSuggestionDto;
import com.techbookstore.app.dto.OptimalStockDto;
import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.OptimalStockSettings;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.OptimalStockSettingsRepository;
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
 * Service for intelligent ordering strategies and optimization
 * インテリジェント発注戦略・最適化サービス
 */
@Service
@Transactional
public class IntelligentOrderingService {

    private static final Logger logger = LoggerFactory.getLogger(IntelligentOrderingService.class);

    private final OptimalStockCalculatorService optimalStockCalculatorService;
    private final DemandForecastService demandForecastService;
    private final TechTrendAnalysisService techTrendAnalysisService;
    private final SeasonalAnalysisService seasonalAnalysisService;
    private final BookRepository bookRepository;
    private final OptimalStockSettingsRepository optimalStockSettingsRepository;

    public IntelligentOrderingService(OptimalStockCalculatorService optimalStockCalculatorService,
                                     DemandForecastService demandForecastService,
                                     TechTrendAnalysisService techTrendAnalysisService,
                                     SeasonalAnalysisService seasonalAnalysisService,
                                     BookRepository bookRepository,
                                     OptimalStockSettingsRepository optimalStockSettingsRepository) {
        this.optimalStockCalculatorService = optimalStockCalculatorService;
        this.demandForecastService = demandForecastService;
        this.techTrendAnalysisService = techTrendAnalysisService;
        this.seasonalAnalysisService = seasonalAnalysisService;
        this.bookRepository = bookRepository;
        this.optimalStockSettingsRepository = optimalStockSettingsRepository;
    }

    /**
     * Generate comprehensive order suggestions for multiple books
     * 複数書籍の包括的発注提案生成
     */
    public OrderSuggestionDto generateOrderSuggestions(List<Long> bookIds, String orderType) {
        logger.info("Generating order suggestions for {} books with type {}", bookIds.size(), orderType);
        
        // Get optimal stock information for all books
        List<OptimalStockDto> stockAnalysis = bookIds.stream()
            .map(optimalStockCalculatorService::calculateOptimalStock)
            .collect(Collectors.toList());
        
        // Generate suggestions based on order type
        OrderSuggestionDto suggestion;
        switch (orderType) {
            case "EMERGENCY":
                suggestion = generateEmergencyOrderSuggestion(stockAnalysis);
                break;
            case "STRATEGIC":
                suggestion = generateStrategicOrderSuggestion(stockAnalysis);
                break;
            case "SEASONAL":
                suggestion = generateSeasonalOrderSuggestion(stockAnalysis);
                break;
            case "OPTIMIZED":
                suggestion = generateOptimizedOrderSuggestion(stockAnalysis);
                break;
            default:
                suggestion = generateOptimizedOrderSuggestion(stockAnalysis);
                break;
        }
        
        // Add risk analysis
        suggestion.setRiskFactors(analyzeRiskFactors(stockAnalysis));
        
        // Add optimization recommendations
        suggestion.setOptimization(calculateOptimization(stockAnalysis));
        
        logger.info("Generated order suggestion with {} category suggestions", 
                   suggestion.getCategorySuggestions().size());
        
        return suggestion;
    }

    /**
     * Generate emergency order suggestion for immediate needs
     * 緊急発注提案生成
     */
    private OrderSuggestionDto generateEmergencyOrderSuggestion(List<OptimalStockDto> stockAnalysis) {
        OrderSuggestionDto suggestion = new OrderSuggestionDto();
        suggestion.setSuggestionType("EMERGENCY");
        suggestion.setSuggestionDate(LocalDate.now());
        
        // Filter books that need immediate reordering
        List<OptimalStockDto> emergencyBooks = stockAnalysis.stream()
            .filter(stock -> "REORDER_NEEDED".equals(stock.getStockStatus()))
            .sorted((a, b) -> Integer.compare(a.getCurrentStock(), b.getCurrentStock())) // Most critical first
            .collect(Collectors.toList());
        
        suggestion.setTotalSuggestions(emergencyBooks.size());
        
        // Create category suggestions
        Map<String, List<OptimalStockDto>> categorizedBooks = categorizeBooks(emergencyBooks);
        List<OrderSuggestionDto.CategoryOrderSuggestion> categorySuggestions = 
            categorizedBooks.entrySet().stream()
                .map(entry -> createCategoryOrderSuggestion(entry.getKey(), entry.getValue(), "EMERGENCY"))
                .collect(Collectors.toList());
        
        suggestion.setCategorySuggestions(categorySuggestions);
        
        // Calculate totals
        BigDecimal totalCost = emergencyBooks.stream()
            .filter(book -> book.getEstimatedCost() != null)
            .map(OptimalStockDto::getEstimatedCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        suggestion.setTotalOrderValue(totalCost);
        suggestion.setPriority("HIGH");
        
        return suggestion;
    }

    /**
     * Generate strategic order suggestion based on tech trends
     * 技術トレンド連動戦略的発注提案生成
     */
    private OrderSuggestionDto generateStrategicOrderSuggestion(List<OptimalStockDto> stockAnalysis) {
        OrderSuggestionDto suggestion = new OrderSuggestionDto();
        suggestion.setSuggestionType("STRATEGIC");
        suggestion.setSuggestionDate(LocalDate.now());
        
        // Include books that are understock or could benefit from strategic ordering
        List<OptimalStockDto> strategicBooks = stockAnalysis.stream()
            .filter(stock -> "REORDER_NEEDED".equals(stock.getStockStatus()) || 
                           "UNDERSTOCK".equals(stock.getStockStatus()) ||
                           hasHighTechTrendPotential(stock))
            .collect(Collectors.toList());
        
        suggestion.setTotalSuggestions(strategicBooks.size());
        
        // Create category suggestions with trend analysis
        Map<String, List<OptimalStockDto>> categorizedBooks = categorizeBooks(strategicBooks);
        List<OrderSuggestionDto.CategoryOrderSuggestion> categorySuggestions = 
            categorizedBooks.entrySet().stream()
                .map(entry -> createCategoryOrderSuggestion(entry.getKey(), entry.getValue(), "STRATEGIC"))
                .collect(Collectors.toList());
        
        suggestion.setCategorySuggestions(categorySuggestions);
        suggestion.setPriority("MEDIUM");
        
        return suggestion;
    }

    /**
     * Generate seasonal order suggestion
     * 季節性発注提案生成
     */
    private OrderSuggestionDto generateSeasonalOrderSuggestion(List<OptimalStockDto> stockAnalysis) {
        OrderSuggestionDto suggestion = new OrderSuggestionDto();
        suggestion.setSuggestionType("SEASONAL");
        suggestion.setSuggestionDate(LocalDate.now());
        
        // Apply seasonal adjustments
        LocalDate now = LocalDate.now();
        List<OptimalStockDto> seasonalBooks = stockAnalysis.stream()
            .map(stock -> applySeasonalAdjustment(stock, now))
            .filter(stock -> stock.getRecommendedOrderQuantity() != null && 
                           stock.getRecommendedOrderQuantity() > 0)
            .collect(Collectors.toList());
        
        suggestion.setTotalSuggestions(seasonalBooks.size());
        
        // Create category suggestions
        Map<String, List<OptimalStockDto>> categorizedBooks = categorizeBooks(seasonalBooks);
        List<OrderSuggestionDto.CategoryOrderSuggestion> categorySuggestions = 
            categorizedBooks.entrySet().stream()
                .map(entry -> createCategoryOrderSuggestion(entry.getKey(), entry.getValue(), "SEASONAL"))
                .collect(Collectors.toList());
        
        suggestion.setCategorySuggestions(categorySuggestions);
        suggestion.setPriority(getSeasonalPriority(now));
        
        return suggestion;
    }

    /**
     * Generate optimized order suggestion with constraint optimization
     * 制約最適化発注提案生成
     */
    private OrderSuggestionDto generateOptimizedOrderSuggestion(List<OptimalStockDto> stockAnalysis) {
        OrderSuggestionDto suggestion = new OrderSuggestionDto();
        suggestion.setSuggestionType("OPTIMIZED");
        suggestion.setSuggestionDate(LocalDate.now());
        
        // Apply multi-objective optimization
        List<OptimalStockDto> optimizedBooks = applyConstraintOptimization(stockAnalysis);
        
        suggestion.setTotalSuggestions(optimizedBooks.size());
        
        // Create category suggestions
        Map<String, List<OptimalStockDto>> categorizedBooks = categorizeBooks(optimizedBooks);
        List<OrderSuggestionDto.CategoryOrderSuggestion> categorySuggestions = 
            categorizedBooks.entrySet().stream()
                .map(entry -> createCategoryOrderSuggestion(entry.getKey(), entry.getValue(), "OPTIMIZED"))
                .collect(Collectors.toList());
        
        suggestion.setCategorySuggestions(categorySuggestions);
        suggestion.setPriority("MEDIUM");
        
        return suggestion;
    }

    /**
     * Check if a book has high tech trend potential
     */
    private boolean hasHighTechTrendPotential(OptimalStockDto stock) {
        // Simplified check - in practice would use TechTrendAnalysisService
        return stock.getTrendFactor() != null && 
               stock.getTrendFactor().compareTo(BigDecimal.valueOf(1.1)) >= 0;
    }

    /**
     * Apply seasonal adjustment to stock recommendation
     */
    private OptimalStockDto applySeasonalAdjustment(OptimalStockDto stock, LocalDate date) {
        Month month = date.getMonth();
        BigDecimal seasonalMultiplier = getSeasonalMultiplier(month);
        
        if (stock.getRecommendedOrderQuantity() == null) {
            // Calculate base recommendation if none exists
            int baseRecommendation = Math.max(0, stock.getOptimalStockLevel() - stock.getCurrentStock());
            stock.setRecommendedOrderQuantity(baseRecommendation);
        }
        
        // Apply seasonal adjustment
        int adjustedQuantity = BigDecimal.valueOf(stock.getRecommendedOrderQuantity())
            .multiply(seasonalMultiplier)
            .setScale(0, RoundingMode.HALF_UP)
            .intValue();
        
        stock.setRecommendedOrderQuantity(Math.max(0, adjustedQuantity));
        
        return stock;
    }

    /**
     * Get seasonal multiplier for a given month
     */
    private BigDecimal getSeasonalMultiplier(Month month) {
        switch (month) {
            case JANUARY:
            case FEBRUARY:
                return BigDecimal.valueOf(1.1); // New year learning
            case MARCH:
            case APRIL:
                return BigDecimal.valueOf(1.3); // Spring semester
            case MAY:
            case JUNE:
                return BigDecimal.valueOf(0.9); // Pre-summer slowdown
            case JULY:
            case AUGUST:
                return BigDecimal.valueOf(1.1); // Summer learning
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
                return BigDecimal.valueOf(1.4); // Back to school peak
            case DECEMBER:
            default:
                return BigDecimal.valueOf(1.0); // Year-end
        }
    }

    /**
     * Categorize books for ordering suggestions
     */
    private Map<String, List<OptimalStockDto>> categorizeBooks(List<OptimalStockDto> books) {
        // Simplified categorization - in practice would use book categories
        Map<String, List<OptimalStockDto>> categories = new HashMap<>();
        
        for (OptimalStockDto book : books) {
            String category = determineCategory(book);
            categories.computeIfAbsent(category, k -> new ArrayList<>()).add(book);
        }
        
        return categories;
    }

    /**
     * Determine category for a book (simplified)
     */
    private String determineCategory(OptimalStockDto book) {
        // Simplified logic - in practice would check actual book categories
        if (book.getBookTitle().toLowerCase().contains("java")) return "JAVA";
        if (book.getBookTitle().toLowerCase().contains("python")) return "PYTHON";
        if (book.getBookTitle().toLowerCase().contains("ai") || 
            book.getBookTitle().toLowerCase().contains("machine learning")) return "AI_ML";
        if (book.getBookTitle().toLowerCase().contains("database")) return "DATABASE";
        if (book.getBookTitle().toLowerCase().contains("cloud")) return "CLOUD";
        return "GENERAL";
    }

    /**
     * Create category order suggestion
     */
    private OrderSuggestionDto.CategoryOrderSuggestion createCategoryOrderSuggestion(
            String category, List<OptimalStockDto> books, String orderType) {
        
        OrderSuggestionDto.CategoryOrderSuggestion suggestion = new OrderSuggestionDto.CategoryOrderSuggestion();
        suggestion.setCategoryCode(category);
        suggestion.setCategoryName(category);
        suggestion.setItemCount(books.size());
        
        // Calculate totals
        BigDecimal totalCost = books.stream()
            .filter(book -> book.getEstimatedCost() != null)
            .map(OptimalStockDto::getEstimatedCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        suggestion.setTotalOrderValue(totalCost);
        suggestion.setTrend(determineCategoryTrend(category));
        suggestion.setSeasonalFactor(getSeasonalFactorForCategory(category));
        suggestion.setStrategicRecommendation(generateCategoryReason(category, books, orderType));
        
        // Set top books
        List<String> topBookTitles = books.stream()
            .limit(3)
            .map(OptimalStockDto::getBookTitle)
            .collect(Collectors.toList());
        suggestion.setTopBooks(topBookTitles);
        
        return suggestion;
    }

    /**
     * Calculate priority for a category
     */
    private String calculateCategoryPriority(String category, List<OptimalStockDto> books, String orderType) {
        long criticalBooks = books.stream()
            .filter(book -> "REORDER_NEEDED".equals(book.getStockStatus()))
            .count();
        
        if (criticalBooks > books.size() * 0.5) {
            return "HIGH";
        } else if ("EMERGENCY".equals(orderType)) {
            return "HIGH";
        } else if ("AI_ML".equals(category) || "CLOUD".equals(category)) {
            return "MEDIUM"; // Trending categories
        } else {
            return "LOW";
        }
    }

    /**
     * Generate reason for category suggestion
     */
    private String generateCategoryReason(String category, List<OptimalStockDto> books, String orderType) {
        long reorderNeeded = books.stream()
            .filter(book -> "REORDER_NEEDED".equals(book.getStockStatus()))
            .count();
        
        if (reorderNeeded > 0) {
            return "%d books need immediate reordering".formatted(reorderNeeded);
        }
        
        switch (orderType) {
            case "STRATEGIC":
                return "Strategic stocking based on tech trends";
            case "SEASONAL":
                return "Seasonal demand adjustment";
            case "OPTIMIZED":
                return "Optimized ordering for cost efficiency";
            default:
                return "Inventory replenishment";
        }
    }

    /**
     * Apply constraint optimization to book list
     */
    private List<OptimalStockDto> applyConstraintOptimization(List<OptimalStockDto> stockAnalysis) {
        // Simplified constraint optimization
        // In practice, this would use linear programming or other optimization techniques
        
        // Example constraints:
        final BigDecimal MAX_BUDGET = BigDecimal.valueOf(50000); // Example budget constraint
        final int MAX_ITEMS = 100; // Example volume constraint
        
        // Sort by priority (cost/benefit ratio)
        List<OptimalStockDto> sortedBooks = stockAnalysis.stream()
            .filter(stock -> "REORDER_NEEDED".equals(stock.getStockStatus()) || 
                           "UNDERSTOCK".equals(stock.getStockStatus()))
            .sorted((a, b) -> {
                // Prioritize by urgency and potential revenue
                double priorityA = calculatePriority(a);
                double priorityB = calculatePriority(b);
                return Double.compare(priorityB, priorityA); // Descending order
            })
            .collect(Collectors.toList());
        
        // Apply constraints
        List<OptimalStockDto> optimizedBooks = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;
        int totalItems = 0;
        
        for (OptimalStockDto book : sortedBooks) {
            if (book.getEstimatedCost() != null) {
                BigDecimal newTotalCost = totalCost.add(book.getEstimatedCost());
                if (newTotalCost.compareTo(MAX_BUDGET) <= 0 && totalItems < MAX_ITEMS) {
                    optimizedBooks.add(book);
                    totalCost = newTotalCost;
                    totalItems++;
                }
            }
        }
        
        return optimizedBooks;
    }

    /**
     * Calculate priority score for optimization
     */
    private double calculatePriority(OptimalStockDto stock) {
        double urgencyScore;
        switch (stock.getStockStatus()) {
            case "REORDER_NEEDED":
                urgencyScore = 1.0;
                break;
            case "UNDERSTOCK":
                urgencyScore = 0.7;
                break;
            case "OPTIMAL":
                urgencyScore = 0.3;
                break;
            default:
                urgencyScore = 0.1;
                break;
        }
        
        double revenueScore = stock.getEstimatedRevenue() != null ? 
            stock.getEstimatedRevenue().doubleValue() / 1000.0 : 0.0;
        
        return urgencyScore * 100 + revenueScore;
    }

    /**
     * Analyze risk factors for the order
     */
    private List<OrderSuggestionDto.RiskFactor> analyzeRiskFactors(List<OptimalStockDto> stockAnalysis) {
        List<OrderSuggestionDto.RiskFactor> riskFactors = new ArrayList<>();
        
        // Budget risk
        BigDecimal totalCost = stockAnalysis.stream()
            .filter(book -> book.getEstimatedCost() != null)
            .map(OptimalStockDto::getEstimatedCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalCost.compareTo(BigDecimal.valueOf(30000)) > 0) {
            OrderSuggestionDto.RiskFactor budgetRisk = new OrderSuggestionDto.RiskFactor();
            budgetRisk.setRiskType("CASH_FLOW");
            budgetRisk.setDescription("High total order cost");
            budgetRisk.setSeverity("HIGH");
            budgetRisk.setProbabilityPercentage(BigDecimal.valueOf(80));
            budgetRisk.setMitigation("Consider phased ordering or budget approval");
            riskFactors.add(budgetRisk);
        }
        
        // Obsolescence risk
        long oldBooks = stockAnalysis.stream()
            .filter(book -> book.getObsolescenceFactor() != null && 
                           book.getObsolescenceFactor().compareTo(BigDecimal.valueOf(0.8)) < 0)
            .count();
        
        if (oldBooks > 0) {
            OrderSuggestionDto.RiskFactor obsolescenceRisk = new OrderSuggestionDto.RiskFactor();
            obsolescenceRisk.setRiskType("TECH_OBSOLESCENCE");
            obsolescenceRisk.setDescription("%d books have high obsolescence risk".formatted(oldBooks));
            obsolescenceRisk.setSeverity("MEDIUM");
            obsolescenceRisk.setProbabilityPercentage(BigDecimal.valueOf(60));
            obsolescenceRisk.setMitigation("Reduce order quantities for older titles");
            riskFactors.add(obsolescenceRisk);
        }
        
        return riskFactors;
    }

    /**
     * Calculate optimization metrics
     */
    private OrderSuggestionDto.OrderOptimization calculateOptimization(List<OptimalStockDto> stockAnalysis) {
        OrderSuggestionDto.OrderOptimization optimization = new OrderSuggestionDto.OrderOptimization();
        
        BigDecimal totalCost = stockAnalysis.stream()
            .filter(book -> book.getEstimatedCost() != null)
            .map(OptimalStockDto::getEstimatedCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalRevenue = stockAnalysis.stream()
            .filter(book -> book.getEstimatedRevenue() != null)
            .map(OptimalStockDto::getEstimatedRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        optimization.setSuggestedSpending(totalCost);
        optimization.setExpectedRevenue(totalRevenue);
        optimization.setExpectedProfit(totalRevenue.subtract(totalCost));
        
        // Cash flow impact
        if (totalCost.compareTo(BigDecimal.valueOf(20000)) > 0) {
            optimization.setCashFlowImpact("HIGH");
        } else if (totalCost.compareTo(BigDecimal.valueOf(10000)) > 0) {
            optimization.setCashFlowImpact("MEDIUM");
        } else {
            optimization.setCashFlowImpact("LOW");
        }
        
        // Risk level
        double profitMargin = totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? 
            optimization.getExpectedProfit().divide(totalRevenue, 2, RoundingMode.HALF_UP).doubleValue() : 0.0;
        
        if (profitMargin > 0.3) {
            optimization.setRiskLevel("LOW");
        } else if (profitMargin > 0.15) {
            optimization.setRiskLevel("MEDIUM");
        } else {
            optimization.setRiskLevel("HIGH");
        }
        
        // Optimization recommendations
        List<String> recommendations = new ArrayList<>();
        recommendations.add("Consider volume discounts for large orders");
        if (optimization.getRiskLevel().equals("HIGH")) {
            recommendations.add("Review profit margins and consider alternative suppliers");
        }
        optimization.setOptimizationRecommendations(recommendations);
        
        return optimization;
    }

    /**
     * Determine category trend
     */
    private String determineCategoryTrend(String category) {
        // Simplified trend determination - in practice would use TechTrendAnalysisService
        switch (category) {
            case "AI_ML":
            case "CLOUD":
                return "RISING";
            case "JAVA":
            case "PYTHON":
                return "STABLE";
            default:
                return "STABLE";
        }
    }

    /**
     * Get seasonal factor for category
     */
    private String getSeasonalFactorForCategory(String category) {
        Month currentMonth = LocalDate.now().getMonth();
        switch (currentMonth) {
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
                return "HIGH";
            case JANUARY:
            case FEBRUARY:
                return "MEDIUM";
            default:
                return "NORMAL";
        }
    }

    /**
     * Get seasonal priority based on current date
     */
    private String getSeasonalPriority(LocalDate date) {
        Month month = date.getMonth();
        switch (month) {
            case SEPTEMBER:
            case OCTOBER:
            case NOVEMBER:
                return "HIGH"; // Back to school
            case JANUARY:
            case FEBRUARY:
                return "MEDIUM"; // New year
            default:
                return "LOW";
        }
    }

    /**
     * Calculate optimal delivery date for seasonal orders
     */
    private LocalDate calculateSeasonalDeliveryDate(LocalDate orderDate) {
        Month month = orderDate.getMonth();
        switch (month) {
            case AUGUST:
                return LocalDate.of(orderDate.getYear(), 9, 1); // Before school starts
            case DECEMBER:
                return LocalDate.of(orderDate.getYear() + 1, 1, 5); // After New Year
            default:
                return orderDate.plusWeeks(2);
        }
    }
}