package com.techbookstore.app.service;

import com.techbookstore.app.dto.*;
import com.techbookstore.app.entity.*;
import com.techbookstore.app.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for advanced analytics and predictions.
 * 高度分析・予測サービス
 */
@Service
@Transactional(readOnly = true)
public class AnalyticsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
    private final BookRepository bookRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final AggregationCacheRepository cacheRepository;
    private final ABCXYZAnalysisService abcxyzAnalysisService;
    private final TechObsolescenceAnalysisService obsolescenceAnalysisService;
    private final SeasonalAnalysisService seasonalAnalysisService;
    
    public AnalyticsService(OrderRepository orderRepository, CustomerRepository customerRepository,
                           InventoryRepository inventoryRepository, BookRepository bookRepository,
                           InventoryTransactionRepository inventoryTransactionRepository,
                           AggregationCacheRepository cacheRepository,
                           ABCXYZAnalysisService abcxyzAnalysisService,
                           TechObsolescenceAnalysisService obsolescenceAnalysisService,
                           SeasonalAnalysisService seasonalAnalysisService) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.inventoryRepository = inventoryRepository;
        this.bookRepository = bookRepository;
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.cacheRepository = cacheRepository;
        this.abcxyzAnalysisService = abcxyzAnalysisService;
        this.obsolescenceAnalysisService = obsolescenceAnalysisService;
        this.seasonalAnalysisService = seasonalAnalysisService;
    }
    
    /**
     * Generate comprehensive sales analysis - 詳細売上分析
     */
    public SalesAnalysisDto generateSalesAnalysis(LocalDate startDate, LocalDate endDate, 
                                                 String categoryCode, String customerSegment) {
        logger.info("Generating sales analysis from {} to {}, category: {}, segment: {}", 
                   startDate, endDate, categoryCode, customerSegment);
        
        SalesAnalysisDto analysis = new SalesAnalysisDto(startDate, endDate, 
                                                        calculateTotalRevenue(startDate, endDate),
                                                        calculateTotalOrders(startDate, endDate),
                                                        calculateAverageOrderValue(startDate, endDate));
        
        // Set growth rate
        analysis.setGrowthRate(calculateGrowthRate(startDate, endDate));
        
        // Tech category sales analysis
        analysis.setTechCategorySales(generateTechCategorySales(startDate, endDate, categoryCode));
        
        // Customer segment analysis
        analysis.setCustomerSegmentSales(generateCustomerSegmentSales(startDate, endDate, customerSegment));
        
        // Period sales analysis
        analysis.setPeriodSales(generatePeriodSales(startDate, endDate));
        
        // Profitability analysis
        analysis.setProfitabilityItems(generateProfitabilityItems(startDate, endDate));
        
        // Comparison analysis
        analysis.setComparisonAnalysis(generateComparisonAnalysis(startDate, endDate));
        
        return analysis;
    }
    
    /**
     * Generate comprehensive inventory analysis - 詳細在庫分析
     */
    public InventoryAnalysisDto generateInventoryAnalysis(String categoryCode, String analysisType) {
        logger.info("Generating inventory analysis for category: {}, type: {}", categoryCode, analysisType);
        
        LocalDate analysisDate = LocalDate.now();
        InventoryAnalysisDto analysis = new InventoryAnalysisDto(analysisDate,
                                                               calculateTotalInventoryItems(),
                                                               calculateTotalInventoryValue(),
                                                               calculateAverageInventoryTurnover());
        
        // Dead stock analysis
        analysis.setDeadStockItems(calculateDeadStockItems());
        analysis.setDeadStockValue(calculateDeadStockValue());
        
        // Detailed turnover analysis
        analysis.setTurnoverAnalysis(generateInventoryTurnoverAnalysis(categoryCode));
        
        // Dead stock detailed analysis
        analysis.setDeadStockAnalysis(generateDeadStockAnalysis(categoryCode));
        
        // Tech obsolescence risk analysis
        analysis.setObsolescenceRisk(generateObsolescenceRiskAnalysis());
        
        // Stock level analysis
        analysis.setStockLevelAnalysis(generateStockLevelAnalysis(categoryCode));
        
        // Seasonal trend analysis
        analysis.setSeasonalTrend(generateSeasonalInventoryTrend());
        
        return analysis;
    }
    
    /**
     * Predict demand using moving average and seasonal factors - 需要予測
     */
    public PredictionDto predictDemand(String timeHorizon, String categoryCode, String algorithm) {
        logger.info("Predicting demand with horizon: {}, category: {}, algorithm: {}", 
                   timeHorizon, categoryCode, algorithm);
        
        LocalDate predictionDate = LocalDate.now();
        PredictionDto prediction = new PredictionDto(predictionDate, "DEMAND", timeHorizon, 
                                                   calculatePredictionAccuracy(algorithm));
        
        prediction.setAlgorithm(algorithm);
        
        // Generate demand predictions
        prediction.setDemandPredictions(generateDemandPredictions(timeHorizon, categoryCode, algorithm));
        
        // Generate sales predictions
        prediction.setSalesPredictions(generateSalesPredictions(timeHorizon, categoryCode));
        
        // Generate seasonal factors
        prediction.setSeasonalFactors(generateSeasonalFactors(categoryCode));
        
        // Set prediction confidence
        prediction.setConfidence(generatePredictionConfidence(algorithm, timeHorizon));
        
        return prediction;
    }
    
    /**
     * Generate intelligent order suggestions - インテリジェント発注提案
     */
    public OrderSuggestionDto generateOrderSuggestions(String suggestionType, String priority, 
                                                      BigDecimal budget) {
        logger.info("Generating order suggestions type: {}, priority: {}, budget: {}", 
                   suggestionType, priority, budget);
        
        LocalDate suggestionDate = LocalDate.now();
        OrderSuggestionDto suggestions = new OrderSuggestionDto(suggestionDate, suggestionType, 
                                                              0, BigDecimal.ZERO);
        
        suggestions.setPriority(priority);
        
        // Generate book-level suggestions
        List<OrderSuggestionDto.BookOrderSuggestion> bookSuggestions = 
            generateBookOrderSuggestions(suggestionType, priority);
        suggestions.setBookSuggestions(bookSuggestions);
        
        // Generate category-level suggestions
        suggestions.setCategorySuggestions(generateCategoryOrderSuggestions(suggestionType));
        
        // Calculate totals
        int totalSuggestions = bookSuggestions.size();
        BigDecimal totalOrderValue = bookSuggestions.stream()
            .map(OrderSuggestionDto.BookOrderSuggestion::getTotalCost)
            .filter(Objects::nonNull)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        suggestions.setTotalSuggestions(totalSuggestions);
        suggestions.setTotalOrderValue(totalOrderValue);
        
        // Generate optimization recommendations
        suggestions.setOptimization(generateOrderOptimization(budget, totalOrderValue));
        
        // Generate risk factors
        suggestions.setRiskFactors(generateOrderRiskFactors(suggestionType));
        
        return suggestions;
    }
    
    /**
     * Analyze technology trends - 技術トレンド分析
     */
    public TechCategoryAnalysisDto analyzeTechTrends(String categoryCode) {
        logger.info("Analyzing tech trends for category: {}", categoryCode);
        
        LocalDate analysisDate = LocalDate.now();
        TechCategoryAnalysisDto analysis = new TechCategoryAnalysisDto(analysisDate, categoryCode, 
                                                                      getCategoryName(categoryCode));
        
        // Generate metrics
        analysis.setMetrics(generateTechCategoryMetrics(categoryCode));
        
        // Generate trend analysis
        analysis.setTrend(generateTechCategoryTrend(categoryCode));
        
        // Generate competitive technology analysis
        analysis.setCompetitiveTechnologies(generateCompetitiveTechAnalysis(categoryCode));
        
        // Generate subcategory analysis
        analysis.setSubCategories(generateSubCategoryAnalysis(categoryCode));
        
        // Generate lifecycle analysis
        analysis.setLifecycle(generateTechLifecycleAnalysis(categoryCode));
        
        return analysis;
    }
    
    /**
     * Calculate profitability with tech book specific metrics - 収益性分析
     */
    public List<SalesAnalysisDto.ProfitabilityItem> calculateProfitability(LocalDate startDate, 
                                                                           LocalDate endDate, 
                                                                           String analysisLevel) {
        logger.info("Calculating profitability from {} to {}, level: {}", startDate, endDate, analysisLevel);
        
        return generateProfitabilityItems(startDate, endDate);
    }
    
    // Private helper methods for calculations
    
    private BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        // Mock implementation - replace with actual repository query
        return new BigDecimal("45000.00");
    }
    
    private Integer calculateTotalOrders(LocalDate startDate, LocalDate endDate) {
        // Mock implementation - replace with actual repository query  
        return 125;
    }
    
    private BigDecimal calculateAverageOrderValue(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalRevenue = calculateTotalRevenue(startDate, endDate);
        Integer totalOrders = calculateTotalOrders(startDate, endDate);
        
        if (totalOrders == 0) return BigDecimal.ZERO;
        return totalRevenue.divide(new BigDecimal(totalOrders), 2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateGrowthRate(LocalDate startDate, LocalDate endDate) {
        // Mock calculation - replace with actual logic
        return new BigDecimal("12.5");
    }
    
    private List<SalesAnalysisDto.TechCategorySales> generateTechCategorySales(LocalDate startDate, 
                                                                              LocalDate endDate, 
                                                                              String categoryCode) {
        List<SalesAnalysisDto.TechCategorySales> categorySales = new ArrayList<>();
        
        // Mock data - replace with actual repository queries
        categorySales.add(new SalesAnalysisDto.TechCategorySales("JAVA", "Java Programming", 
                                                               new BigDecimal("15000.00"), 45, new BigDecimal("33.3")));
        categorySales.add(new SalesAnalysisDto.TechCategorySales("PYTHON", "Python Programming", 
                                                               new BigDecimal("12000.00"), 38, new BigDecimal("26.7")));
        categorySales.add(new SalesAnalysisDto.TechCategorySales("JAVASCRIPT", "JavaScript Programming", 
                                                               new BigDecimal("10000.00"), 32, new BigDecimal("22.2")));
        
        // Set additional properties
        for (SalesAnalysisDto.TechCategorySales category : categorySales) {
            category.setGrowthRate(new BigDecimal("8.5"));
            category.setTrendDirection("UP");
        }
        
        return categorySales;
    }
    
    private List<SalesAnalysisDto.CustomerSegmentSales> generateCustomerSegmentSales(LocalDate startDate, 
                                                                                    LocalDate endDate, 
                                                                                    String customerSegment) {
        List<SalesAnalysisDto.CustomerSegmentSales> segmentSales = new ArrayList<>();
        
        // Mock data - replace with actual repository queries
        segmentSales.add(new SalesAnalysisDto.CustomerSegmentSales("INTERMEDIATE", 35, 
                                                                  new BigDecimal("18000.00"), new BigDecimal("514.29")));
        segmentSales.add(new SalesAnalysisDto.CustomerSegmentSales("ADVANCED", 25, 
                                                                  new BigDecimal("15000.00"), new BigDecimal("600.00")));
        segmentSales.add(new SalesAnalysisDto.CustomerSegmentSales("BEGINNER", 40, 
                                                                  new BigDecimal("12000.00"), new BigDecimal("300.00")));
        
        // Set customer lifetime values
        for (SalesAnalysisDto.CustomerSegmentSales segment : segmentSales) {
            segment.setCustomerLifetimeValue(segment.getAverageOrderValue().multiply(new BigDecimal("5")));
        }
        
        return segmentSales;
    }
    
    private List<SalesAnalysisDto.PeriodSales> generatePeriodSales(LocalDate startDate, LocalDate endDate) {
        List<SalesAnalysisDto.PeriodSales> periodSales = new ArrayList<>();
        
        // Generate monthly sales data
        LocalDate current = startDate.withDayOfMonth(1);
        while (!current.isAfter(endDate)) {
            periodSales.add(new SalesAnalysisDto.PeriodSales(current, "MONTHLY", 
                                                           new BigDecimal("15000.00"), 42));
            current = current.plusMonths(1);
        }
        
        return periodSales;
    }
    
    private List<SalesAnalysisDto.ProfitabilityItem> generateProfitabilityItems(LocalDate startDate, 
                                                                               LocalDate endDate) {
        List<SalesAnalysisDto.ProfitabilityItem> profitabilityItems = new ArrayList<>();
        
        // Mock profitability data
        profitabilityItems.add(new SalesAnalysisDto.ProfitabilityItem("CATEGORY", "Java Programming",
                                                                     new BigDecimal("15000.00"), new BigDecimal("9000.00"),
                                                                     new BigDecimal("6000.00"), new BigDecimal("40.0")));
        profitabilityItems.add(new SalesAnalysisDto.ProfitabilityItem("CATEGORY", "Python Programming",
                                                                     new BigDecimal("12000.00"), new BigDecimal("7200.00"),
                                                                     new BigDecimal("4800.00"), new BigDecimal("40.0")));
        
        return profitabilityItems;
    }
    
    private SalesAnalysisDto.ComparisonAnalysis generateComparisonAnalysis(LocalDate startDate, LocalDate endDate) {
        return new SalesAnalysisDto.ComparisonAnalysis("PREVIOUS_PERIOD", 
                                                      new BigDecimal("45000.00"), 
                                                      new BigDecimal("40000.00"));
    }
    
    // Inventory analysis helper methods
    
    private Integer calculateTotalInventoryItems() {
        return Math.toIntExact(inventoryRepository.count());
    }
    
    private BigDecimal calculateTotalInventoryValue() {
        // Mock calculation - replace with actual repository query
        return new BigDecimal("125000.00");
    }
    
    private BigDecimal calculateAverageInventoryTurnover() {
        // Mock calculation - replace with actual repository query
        return new BigDecimal("4.2");
    }
    
    private Integer calculateDeadStockItems() {
        // Mock calculation - replace with actual repository query
        return 15;
    }
    
    private BigDecimal calculateDeadStockValue() {
        // Mock calculation - replace with actual repository query
        return new BigDecimal("8500.00");
    }
    
    private List<InventoryAnalysisDto.InventoryTurnoverItem> generateInventoryTurnoverAnalysis(String categoryCode) {
        logger.info("Generating inventory turnover analysis for category: {}", categoryCode);
        
        List<InventoryAnalysisDto.InventoryTurnoverItem> turnoverItems = new ArrayList<>();
        
        try {
            // Perform ABC/XYZ analysis for current date
            LocalDate analysisDate = LocalDate.now();
            List<ABCXYZAnalysis> abcxyzResults = abcxyzAnalysisService.getLatestAnalysis();
            
            // If no recent analysis exists, perform new analysis
            if (abcxyzResults.isEmpty()) {
                logger.info("No recent ABC/XYZ analysis found, performing new analysis");
                abcxyzResults = abcxyzAnalysisService.performAnalysis(analysisDate);
            }
            
            // Convert ABC/XYZ analysis to turnover items
            for (ABCXYZAnalysis analysis : abcxyzResults) {
                Book book = analysis.getBook();
                
                // Filter by category if specified
                if (categoryCode != null && !categoryCode.isEmpty()) {
                    // For now, skip category filtering since we don't have book categories properly linked
                    // In a real implementation, you would check book.getCategories() or similar
                }
                
                // Get current inventory
                Optional<Inventory> inventoryOpt = inventoryRepository.findByBookId(book.getId());
                if (inventoryOpt.isPresent()) {
                    Inventory inventory = inventoryOpt.get();
                    
                    InventoryAnalysisDto.InventoryTurnoverItem item = new InventoryAnalysisDto.InventoryTurnoverItem(
                        book.getId(),
                        book.getTitle(),
                        determineCategoryCode(book), // Helper method to determine category code
                        inventory.getStoreStock() + inventory.getWarehouseStock(), // Total stock
                        analysis.getSalesContribution()
                    );
                    
                    // Set additional fields
                    item.setDaysSinceLastSale(inventory.getDaysSinceLastSale() != null ? 
                                            inventory.getDaysSinceLastSale() : 0);
                    
                    // Calculate turnover category based on ABC/XYZ classification
                    String turnoverCategory = determineTurnoverCategory(
                        analysis.getAbcCategory(), 
                        analysis.getXyzCategory()
                    );
                    item.setTurnoverCategory(turnoverCategory);
                    
                    // Calculate annual revenue (mock calculation)
                    BigDecimal annualRevenue = analysis.getSalesContribution()
                        .multiply(BigDecimal.valueOf(1000)) // Scaling factor
                        .setScale(2, RoundingMode.HALF_UP);
                    item.setAnnualRevenue(annualRevenue);
                    
                    turnoverItems.add(item);
                }
            }
            
            // Sort by sales contribution (descending)
            turnoverItems.sort((a, b) -> b.getTurnoverRate().compareTo(a.getTurnoverRate()));
            
            // Limit to top 50 for performance
            if (turnoverItems.size() > 50) {
                turnoverItems = turnoverItems.subList(0, 50);
            }
            
        } catch (Exception e) {
            logger.error("Error generating inventory turnover analysis", e);
            
            // Fallback to mock data
            turnoverItems.add(new InventoryAnalysisDto.InventoryTurnoverItem(1L, "Java: The Complete Reference", 
                                                                            "JAVA", 25, new BigDecimal("5.2")));
            turnoverItems.add(new InventoryAnalysisDto.InventoryTurnoverItem(2L, "Python Crash Course", 
                                                                            "PYTHON", 18, new BigDecimal("6.1")));
        }
        
        return turnoverItems;
    }
    
    private List<InventoryAnalysisDto.DeadStockItem> generateDeadStockAnalysis(String categoryCode) {
        logger.info("Generating enhanced dead stock analysis with disposal strategies for category: {}", categoryCode);
        
        List<InventoryAnalysisDto.DeadStockItem> deadStockItems = new ArrayList<>();
        
        try {
            // Find items with no sales in the last 90 days
            List<Inventory> allInventory = inventoryRepository.findAll();
            LocalDate cutoffDate = LocalDate.now().minusDays(90);
            
            for (Inventory inventory : allInventory) {
                if (isDeadStock(inventory, cutoffDate)) {
                    Book book = inventory.getBook();
                    
                    // Filter by category if specified
                    if (categoryCode != null && !categoryCode.isEmpty()) {
                        String bookCategory = determineCategoryCode(book);
                        if (!bookCategory.equals(categoryCode)) {
                            continue;
                        }
                    }
                    
                    // Create dead stock item with disposal strategy
                    InventoryAnalysisDto.DeadStockItem item = new InventoryAnalysisDto.DeadStockItem(
                        book.getId(),
                        book.getTitle(),
                        inventory.getStoreStock() + inventory.getWarehouseStock(),
                        calculateStockValue(book, inventory),
                        calculateDaysSinceLastSale(inventory)
                    );
                    
                    // Set additional fields
                    item.setCategoryCode(determineCategoryCode(book));
                    item.setLastSaleDate(inventory.getLastSoldDate());
                    
                    // Determine risk level and recommended action based on disposal strategy
                    DisposalStrategy strategy = determineDisposalStrategy(book, inventory);
                    item.setRiskLevel(strategy.getRiskLevel());
                    item.setRecommendedAction(strategy.getAction());
                    
                    deadStockItems.add(item);
                }
            }
            
            // Sort by days since last sale (descending - oldest first)
            deadStockItems.sort((a, b) -> 
                Integer.compare(b.getDaysSinceLastSale(), a.getDaysSinceLastSale()));
            
            // Limit to top 100 for performance
            if (deadStockItems.size() > 100) {
                deadStockItems = deadStockItems.subList(0, 100);
            }
            
        } catch (Exception e) {
            logger.error("Error generating dead stock analysis", e);
            
            // Fallback to mock data
            deadStockItems.add(new InventoryAnalysisDto.DeadStockItem(3L, "Legacy Framework Guide", 
                                                                     12, new BigDecimal("720.00"), 180));
        }
        
        return deadStockItems;
    }
    
    private List<InventoryAnalysisDto.TechObsolescenceItem> generateObsolescenceRiskAnalysis() {
        logger.info("Generating tech obsolescence risk analysis");
        
        List<InventoryAnalysisDto.TechObsolescenceItem> obsolescenceItems = new ArrayList<>();
        
        try {
            // Get obsolescence analysis by risk level
            LocalDate assessmentDate = LocalDate.now();
            Map<String, List<ObsolescenceAssessment>> riskAnalysis = 
                obsolescenceAnalysisService.getAnalysisByRiskLevel(assessmentDate);
                
            // Get tech lifecycle distribution
            Map<String, Long> lifecycleDistribution = 
                obsolescenceAnalysisService.getTechLifecycleDistribution(assessmentDate);
            
            // Group by category and aggregate data
            Map<String, CategoryRiskData> categoryData = new HashMap<>();
            
            for (Map.Entry<String, List<ObsolescenceAssessment>> entry : riskAnalysis.entrySet()) {
                String riskLevel = entry.getKey();
                
                for (ObsolescenceAssessment assessment : entry.getValue()) {
                    Book book = assessment.getBook();
                    String categoryCode = determineCategoryCode(book);
                    String categoryName = getCategoryName(categoryCode);
                    
                    CategoryRiskData data = categoryData.computeIfAbsent(categoryCode, 
                        k -> new CategoryRiskData(categoryCode, categoryName));
                    
                    BigDecimal bookPrice = book.getSellingPrice() != null ? book.getSellingPrice() : 
                                         (book.getListPrice() != null ? book.getListPrice() : BigDecimal.valueOf(3000));
                    data.addItem(riskLevel, bookPrice);
                    data.setTechLifecycle(obsolescenceAnalysisService.determineTechLifecycleStage(book, assessmentDate));
                    data.setMonthsToObsolescence(assessment.getMonthsToObsolescence());
                }
            }
            
            // Convert to DTO
            for (CategoryRiskData data : categoryData.values()) {
                InventoryAnalysisDto.TechObsolescenceItem item = new InventoryAnalysisDto.TechObsolescenceItem(
                    data.getCategoryCode(),
                    data.getCategoryName(),
                    data.getTotalItems(),
                    data.getTotalValue(),
                    data.getDominantRiskLevel()
                );
                
                item.setTechLifecycleStage(data.getTechLifecycle());
                item.setMonthsToObsolescence(data.getMonthsToObsolescence());
                item.setMitigationStrategy(generateMitigationStrategy(data));
                
                obsolescenceItems.add(item);
            }
            
            // Sort by risk level (HIGH first) and total value (descending)
            obsolescenceItems.sort((a, b) -> {
                int riskCompare = getRiskWeight(b.getObsolescenceRisk()) - getRiskWeight(a.getObsolescenceRisk());
                if (riskCompare != 0) return riskCompare;
                return b.getTotalValue().compareTo(a.getTotalValue());
            });
            
        } catch (Exception e) {
            logger.error("Error generating obsolescence risk analysis", e);
            
            // Fallback to mock data
            obsolescenceItems.add(new InventoryAnalysisDto.TechObsolescenceItem("LEGACY_FRAMEWORKS", 
                                                                              "Legacy Frameworks", 5, 
                                                                              new BigDecimal("3000.00"), "HIGH"));
        }
        
        return obsolescenceItems;
    }
    
    private List<InventoryAnalysisDto.StockLevelItem> generateStockLevelAnalysis(String categoryCode) {
        // Mock implementation
        List<InventoryAnalysisDto.StockLevelItem> stockLevelItems = new ArrayList<>();
        
        stockLevelItems.add(new InventoryAnalysisDto.StockLevelItem(1L, "Java: The Complete Reference", 
                                                                   25, 10, "NORMAL"));
        
        return stockLevelItems;
    }
    
    private InventoryAnalysisDto.SeasonalInventoryTrend generateSeasonalInventoryTrend() {
        logger.info("Generating seasonal inventory trend using SeasonalAnalysisService");
        
        try {
            return seasonalAnalysisService.generateSeasonalInventoryTrend();
        } catch (Exception e) {
            logger.error("Error generating seasonal trend analysis", e);
            
            // Fallback to mock data
            return new InventoryAnalysisDto.SeasonalInventoryTrend("FALL", new BigDecimal("1.25"), 
                                                                  Arrays.asList("JAVA", "PYTHON", "JAVASCRIPT"));
        }
    }
    
    // Prediction helper methods
    
    private BigDecimal calculatePredictionAccuracy(String algorithm) {
        // Mock implementation - replace with actual accuracy calculation
        switch (algorithm) {
            case "MOVING_AVERAGE": return new BigDecimal("75.5");
            case "SEASONAL": return new BigDecimal("82.3");
            case "TREND_ANALYSIS": return new BigDecimal("78.9");
            default: return new BigDecimal("70.0");
        }
    }
    
    private List<PredictionDto.DemandPrediction> generateDemandPredictions(String timeHorizon, 
                                                                          String categoryCode, 
                                                                          String algorithm) {
        List<PredictionDto.DemandPrediction> predictions = new ArrayList<>();
        
        LocalDate forecastDate = LocalDate.now().plusMonths(1);
        predictions.add(new PredictionDto.DemandPrediction(1L, "Java: The Complete Reference", 
                                                          forecastDate, 35, 25));
        
        return predictions;
    }
    
    private List<PredictionDto.SalesPrediction> generateSalesPredictions(String timeHorizon, String categoryCode) {
        List<PredictionDto.SalesPrediction> predictions = new ArrayList<>();
        
        LocalDate startDate = LocalDate.now().plusMonths(1);
        LocalDate endDate = startDate.plusMonths(1);
        
        predictions.add(new PredictionDto.SalesPrediction("JAVA", "Java Programming", 
                                                         startDate, endDate, 
                                                         new BigDecimal("16000.00"), 48));
        
        return predictions;
    }
    
    private List<PredictionDto.SeasonalFactor> generateSeasonalFactors(String categoryCode) {
        List<PredictionDto.SeasonalFactor> factors = new ArrayList<>();
        
        factors.add(new PredictionDto.SeasonalFactor("NEW_YEAR", "JAVA", new BigDecimal("1.25")));
        factors.add(new PredictionDto.SeasonalFactor("EXAM_PERIOD", "JAVA", new BigDecimal("1.15")));
        
        return factors;
    }
    
    private PredictionDto.PredictionConfidence generatePredictionConfidence(String algorithm, String timeHorizon) {
        return new PredictionDto.PredictionConfidence(new BigDecimal("78.5"), "MEDIUM", "GOOD");
    }
    
    // Order suggestion helper methods
    
    private List<OrderSuggestionDto.BookOrderSuggestion> generateBookOrderSuggestions(String suggestionType, 
                                                                                     String priority) {
        List<OrderSuggestionDto.BookOrderSuggestion> suggestions = new ArrayList<>();
        
        suggestions.add(new OrderSuggestionDto.BookOrderSuggestion(1L, "Java: The Complete Reference", 
                                                                   10, 25, new BigDecimal("45.00"), "LOW_STOCK"));
        suggestions.get(0).setTotalCost(suggestions.get(0).getUnitCost().multiply(new BigDecimal(25)));
        suggestions.get(0).setUrgency("WITHIN_WEEK");
        suggestions.get(0).setDaysUntilStockout(7);
        
        return suggestions;
    }
    
    private List<OrderSuggestionDto.CategoryOrderSuggestion> generateCategoryOrderSuggestions(String suggestionType) {
        List<OrderSuggestionDto.CategoryOrderSuggestion> suggestions = new ArrayList<>();
        
        suggestions.add(new OrderSuggestionDto.CategoryOrderSuggestion("JAVA", "Java Programming", 
                                                                      3, new BigDecimal("1350.00"), "RISING"));
        
        return suggestions;
    }
    
    private OrderSuggestionDto.OrderOptimization generateOrderOptimization(BigDecimal budget, 
                                                                          BigDecimal totalOrderValue) {
        return new OrderSuggestionDto.OrderOptimization(budget, totalOrderValue, 
                                                       totalOrderValue.multiply(new BigDecimal("2.2")), 
                                                       totalOrderValue.multiply(new BigDecimal("0.4")));
    }
    
    private List<OrderSuggestionDto.RiskFactor> generateOrderRiskFactors(String suggestionType) {
        List<OrderSuggestionDto.RiskFactor> riskFactors = new ArrayList<>();
        
        riskFactors.add(new OrderSuggestionDto.RiskFactor("TECH_OBSOLESCENCE", "MEDIUM", 
                                                         "Risk of technology becoming outdated", 
                                                         "Monitor technology trends and adjust inventory accordingly"));
        
        return riskFactors;
    }
    
    // Tech category analysis helper methods
    
    private String getCategoryName(String categoryCode) {
        // Mock implementation - replace with actual repository query
        switch (categoryCode) {
            case "JAVA": return "Java Programming";
            case "PYTHON": return "Python Programming"; 
            case "JAVASCRIPT": return "JavaScript Programming";
            default: return "Unknown Category";
        }
    }
    
    private TechCategoryAnalysisDto.TechCategoryMetrics generateTechCategoryMetrics(String categoryCode) {
        return new TechCategoryAnalysisDto.TechCategoryMetrics(45, new BigDecimal("15000.00"), 
                                                              new BigDecimal("33.3"));
    }
    
    private TechCategoryAnalysisDto.TechCategoryTrend generateTechCategoryTrend(String categoryCode) {
        return new TechCategoryAnalysisDto.TechCategoryTrend("RISING", new BigDecimal("12.5"), "HIGH");
    }
    
    private List<TechCategoryAnalysisDto.CompetitiveTech> generateCompetitiveTechAnalysis(String categoryCode) {
        List<TechCategoryAnalysisDto.CompetitiveTech> competitiveTechs = new ArrayList<>();
        
        competitiveTechs.add(new TechCategoryAnalysisDto.CompetitiveTech("Kotlin", "KOTLIN", 
                                                                       new BigDecimal("5.2")));
        
        return competitiveTechs;
    }
    
    private List<TechCategoryAnalysisDto.SubCategoryAnalysis> generateSubCategoryAnalysis(String categoryCode) {
        List<TechCategoryAnalysisDto.SubCategoryAnalysis> subCategories = new ArrayList<>();
        
        subCategories.add(new TechCategoryAnalysisDto.SubCategoryAnalysis("JAVA_SPRING", "Java Spring Framework", 
                                                                         new BigDecimal("8000.00"), new BigDecimal("53.3")));
        
        return subCategories;
    }
    
    private TechCategoryAnalysisDto.TechLifecycleAnalysis generateTechLifecycleAnalysis(String categoryCode) {
        return new TechCategoryAnalysisDto.TechLifecycleAnalysis("MATURITY", 18, "STABLE");
    }
    
    /**
     * Helper method to determine category code from book
     */
    private String determineCategoryCode(Book book) {
        // Mock implementation - in real system, extract from book categories
        if (book.getTitle().toLowerCase().contains("java")) {
            return "JAVA";
        } else if (book.getTitle().toLowerCase().contains("python")) {
            return "PYTHON";
        } else if (book.getTitle().toLowerCase().contains("javascript")) {
            return "JAVASCRIPT";
        } else {
            return "OTHER";
        }
    }
    
    /**
     * Helper method to determine turnover category based on ABC/XYZ classification
     */
    private String determineTurnoverCategory(String abcCategory, String xyzCategory) {
        String combined = abcCategory + xyzCategory;
        
        switch (combined) {
            case "AX":
            case "AY":
                return "FAST";
            case "BX":
            case "BY":
                return "MEDIUM";
            case "CX":
            case "CY":
                return "SLOW";
            case "AZ":
            case "BZ":
            case "CZ":
                return "DEAD";
            default:
                return "MEDIUM";
        }
    }
    
    /**
     * Check if inventory item is dead stock (no sales in 90+ days)
     */
    private boolean isDeadStock(Inventory inventory, LocalDate cutoffDate) {
        if (inventory.getLastSoldDate() == null) {
            return true; // Never sold = dead stock
        }
        return inventory.getLastSoldDate().isBefore(cutoffDate);
    }
    
    /**
     * Calculate stock value for inventory item
     */
    private BigDecimal calculateStockValue(Book book, Inventory inventory) {
        int totalStock = inventory.getStoreStock() + inventory.getWarehouseStock();
        BigDecimal price = book.getSellingPrice() != null ? book.getSellingPrice() : 
                          (book.getListPrice() != null ? book.getListPrice() : BigDecimal.valueOf(3000)); // Default price
        return price.multiply(BigDecimal.valueOf(totalStock));
    }
    
    /**
     * Calculate days since last sale
     */
    private Integer calculateDaysSinceLastSale(Inventory inventory) {
        if (inventory.getLastSoldDate() == null) {
            return 365; // Default to 1 year if never sold
        }
        return (int) ChronoUnit.DAYS.between(inventory.getLastSoldDate(), LocalDate.now());
    }
    
    /**
     * Determine disposal strategy based on tech lifecycle, inventory period, and stock quantity
     */
    private DisposalStrategy determineDisposalStrategy(Book book, Inventory inventory) {
        int daysSinceLastSale = calculateDaysSinceLastSale(inventory);
        int totalStock = inventory.getStoreStock() + inventory.getWarehouseStock();
        String techLifecycle = obsolescenceAnalysisService.determineTechLifecycleStage(book, LocalDate.now());
        
        // Strategy decision matrix
        if (daysSinceLastSale >= 180 && "DECLINING".equals(techLifecycle)) {
            // Very old stock of declining tech - liquidate immediately
            return new DisposalStrategy("HIGH", "LIQUIDATE", 
                "廃棄処分 - 回収率10-30%", BigDecimal.valueOf(0.20));
        } else if (daysSinceLastSale >= 150 || totalStock > 50) {
            // Old stock or high quantity - bulk sale
            return new DisposalStrategy("HIGH", "BULK_SALE", 
                "バルク販売 - 40-50%割引、回収率50-60%", BigDecimal.valueOf(0.55));
        } else if (daysSinceLastSale >= 120 || "MATURE".equals(techLifecycle)) {
            // Medium-old stock or mature tech - return to supplier
            return new DisposalStrategy("MEDIUM", "RETURN", 
                "サプライヤー返品 - 返送料負担、回収率90-95%", BigDecimal.valueOf(0.92));
        } else {
            // Recent dead stock - discount sale
            return new DisposalStrategy("MEDIUM", "DISCOUNT_SALE", 
                "割引販売 - 20-30%割引、回収率70-80%", BigDecimal.valueOf(0.75));
        }
    }
    
    /**
     * Internal class for disposal strategy
     */
    private static class DisposalStrategy {
        private final String riskLevel;
        private final String action;
        private final String description;
        private final BigDecimal recoveryRate;
        
        public DisposalStrategy(String riskLevel, String action, String description, BigDecimal recoveryRate) {
            this.riskLevel = riskLevel;
            this.action = action;
            this.description = description;
            this.recoveryRate = recoveryRate;
        }
        
        public String getRiskLevel() { return riskLevel; }
        public String getAction() { return action; }
        public String getDescription() { return description; }
        public BigDecimal getRecoveryRate() { return recoveryRate; }
    }
    
    /**
     * Internal class for category risk data aggregation
     */
    private static class CategoryRiskData {
        private final String categoryCode;
        private final String categoryName;
        private int totalItems = 0;
        private BigDecimal totalValue = BigDecimal.ZERO;
        private final Map<String, Integer> riskCounts = new HashMap<>();
        private String techLifecycle = "MATURE";
        private Integer monthsToObsolescence;
        
        public CategoryRiskData(String categoryCode, String categoryName) {
            this.categoryCode = categoryCode;
            this.categoryName = categoryName;
        }
        
        public void addItem(String riskLevel, BigDecimal itemValue) {
            totalItems++;
            totalValue = totalValue.add(itemValue);
            riskCounts.merge(riskLevel, 1, Integer::sum);
        }
        
        public String getDominantRiskLevel() {
            return riskCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("MEDIUM");
        }
        
        // Getters and setters
        public String getCategoryCode() { return categoryCode; }
        public String getCategoryName() { return categoryName; }
        public Integer getTotalItems() { return totalItems; }
        public BigDecimal getTotalValue() { return totalValue; }
        public String getTechLifecycle() { return techLifecycle; }
        public void setTechLifecycle(String techLifecycle) { this.techLifecycle = techLifecycle; }
        public Integer getMonthsToObsolescence() { return monthsToObsolescence; }
        public void setMonthsToObsolescence(Integer monthsToObsolescence) { this.monthsToObsolescence = monthsToObsolescence; }
    }
    
    /**
     * Generate mitigation strategy based on category risk data
     */
    private String generateMitigationStrategy(CategoryRiskData data) {
        String riskLevel = data.getDominantRiskLevel();
        String lifecycle = data.getTechLifecycle();
        
        if ("HIGH".equals(riskLevel)) {
            if ("DECLINING".equals(lifecycle)) {
                return "即座の在庫処分・新規入荷停止・代替技術への切り替え";
            } else {
                return "在庫削減・販売促進・市場動向の詳細分析";
            }
        } else if ("MEDIUM".equals(riskLevel)) {
            if ("MATURE".equals(lifecycle)) {
                return "定期的な需要監視・段階的在庫調整・関連技術動向追跡";
            } else {
                return "マーケティング強化・需要予測精度向上・競合分析";
            }
        } else {
            return "継続監視・四半期レビュー・市場機会の探索";
        }
    }
    
    /**
     * Get risk weight for sorting (higher = more important)
     */
    private int getRiskWeight(String riskLevel) {
        switch (riskLevel) {
            case "HIGH": return 3;
            case "MEDIUM": return 2;
            case "LOW": return 1;
            default: return 0;
        }
    }
}