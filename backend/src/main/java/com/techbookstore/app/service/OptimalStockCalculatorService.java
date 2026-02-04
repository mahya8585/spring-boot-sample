package com.techbookstore.app.service;

import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.OptimalStockSettings;
import com.techbookstore.app.entity.Inventory;
import com.techbookstore.app.dto.OptimalStockDto;
import com.techbookstore.app.repository.OptimalStockSettingsRepository;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.InventoryRepository;
import com.techbookstore.app.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for calculating optimal stock levels with tech-specific EOQ
 * 技術書特化EOQ計算サービス
 */
@Service
@Transactional
public class OptimalStockCalculatorService {

    private static final Logger logger = LoggerFactory.getLogger(OptimalStockCalculatorService.class);

    private final OptimalStockSettingsRepository optimalStockSettingsRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    private final OrderRepository orderRepository;
    private final DemandForecastService demandForecastService;
    private final TechObsolescenceAnalysisService obsolescenceAnalysisService;
    private final SeasonalAnalysisService seasonalAnalysisService;

    public OptimalStockCalculatorService(OptimalStockSettingsRepository optimalStockSettingsRepository,
                                        BookRepository bookRepository,
                                        InventoryRepository inventoryRepository,
                                        OrderRepository orderRepository,
                                        DemandForecastService demandForecastService,
                                        TechObsolescenceAnalysisService obsolescenceAnalysisService,
                                        SeasonalAnalysisService seasonalAnalysisService) {
        this.optimalStockSettingsRepository = optimalStockSettingsRepository;
        this.bookRepository = bookRepository;
        this.inventoryRepository = inventoryRepository;
        this.orderRepository = orderRepository;
        this.demandForecastService = demandForecastService;
        this.obsolescenceAnalysisService = obsolescenceAnalysisService;
        this.seasonalAnalysisService = seasonalAnalysisService;
    }

    /**
     * Calculate optimal stock levels for a specific book
     * 特定書籍の最適在庫レベルを計算
     */
    public OptimalStockDto calculateOptimalStock(Long bookId) {
        logger.info("Calculating optimal stock for book ID: {}", bookId);
        
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

        // Get current inventory
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBookId(bookId);
        int currentStock = inventoryOpt.map(Inventory::getTotalStock).orElse(0);

        // Calculate EOQ with tech-specific adjustments
        EOQCalculationResult eoqResult = calculateTechSpecificEOQ(book);
        
        // Calculate safety stock
        int safetyStock = calculateSafetyStock(book, eoqResult.getAverageDemand());
        
        // Calculate reorder point
        int reorderPoint = calculateReorderPoint(book, eoqResult.getAverageDemand(), safetyStock);
        
        // Determine optimal stock level
        int optimalStockLevel = eoqResult.getEoq() + safetyStock;
        
        // Calculate factors
        BigDecimal obsolescenceFactor = calculateObsolescenceFactor(book);
        BigDecimal trendFactor = calculateTrendFactor(book);
        BigDecimal seasonalityFactor = calculateSeasonalityFactor(book);
        
        // Apply factor adjustments
        optimalStockLevel = (int) Math.round(optimalStockLevel * 
            obsolescenceFactor.multiply(trendFactor).multiply(seasonalityFactor).doubleValue());
        
        // Create DTO
        OptimalStockDto dto = new OptimalStockDto(bookId, book.getTitle(), currentStock, 
                                                 optimalStockLevel, reorderPoint, safetyStock);
        dto.setEconomicOrderQuantity(eoqResult.getEoq());
        dto.setObsolescenceFactor(obsolescenceFactor);
        dto.setTrendFactor(trendFactor);
        dto.setSeasonalityFactor(seasonalityFactor);
        dto.setValidFrom(LocalDate.now());
        
        // Determine stock status
        dto.setStockStatus(determineStockStatus(currentStock, optimalStockLevel, reorderPoint));
        
        // Calculate recommended order quantity if needed
        if (currentStock <= reorderPoint) {
            int recommendedOrderQuantity = Math.max(eoqResult.getEoq(), optimalStockLevel - currentStock);
            dto.setRecommendedOrderQuantity(recommendedOrderQuantity);
            dto.setEstimatedCost(calculateEstimatedCost(book, recommendedOrderQuantity));
            dto.setEstimatedRevenue(calculateEstimatedRevenue(book, recommendedOrderQuantity));
        }
        
        logger.info("Calculated optimal stock for book {}: optimal={}, current={}, reorder={}", 
                   bookId, optimalStockLevel, currentStock, reorderPoint);
        
        return dto;
    }

    /**
     * Calculate tech-specific Economic Order Quantity (EOQ)
     * 技術書特化経済発注量計算
     */
    private EOQCalculationResult calculateTechSpecificEOQ(Book book) {
        // Calculate average demand from historical data (last 12 months)
        double averageDemand = calculateAverageDemand(book);
        
        // Estimated ordering cost (fixed cost per order)
        double orderingCost = 50.0; // Base ordering cost
        
        // Holding cost per unit per year (percentage of book price)
        double holdingCostRate = 0.25; // 25% of book price
        double holdingCost = book.getSellingPrice().multiply(BigDecimal.valueOf(holdingCostRate)).doubleValue();
        
        // Basic EOQ formula: sqrt(2 * D * S / H)
        // D = annual demand, S = ordering cost, H = holding cost per unit per year
        double annualDemand = averageDemand * 12;
        double basicEOQ = Math.sqrt((2 * annualDemand * orderingCost) / holdingCost);
        
        // Apply tech book specific adjustments
        double adjustedEOQ = applyTechBookAdjustments(basicEOQ, book);
        
        int finalEOQ = Math.max(1, (int) Math.round(adjustedEOQ));
        
        return new EOQCalculationResult(finalEOQ, averageDemand);
    }

    /**
     * Apply tech book specific adjustments to EOQ
     */
    private double applyTechBookAdjustments(double basicEOQ, Book book) {
        double adjustedEOQ = basicEOQ;
        
        // Volume discount adjustment (larger orders get better pricing)
        if (basicEOQ > 50) {
            adjustedEOQ *= 1.2; // Increase EOQ to take advantage of volume discounts
        }
        
        // Publication date adjustment (newer books have higher demand variability)
        if (book.getPublicationDate() != null && 
            book.getPublicationDate().isAfter(LocalDate.now().minusYears(1))) {
            adjustedEOQ *= 0.8; // Reduce EOQ for very new books due to uncertainty
        }
        
        // Price-based adjustment (expensive books should have lower EOQ)
        if (book.getSellingPrice().compareTo(BigDecimal.valueOf(100)) > 0) {
            adjustedEOQ *= 0.9; // Reduce EOQ for expensive books
        }
        
        return adjustedEOQ;
    }

    /**
     * Calculate safety stock based on demand variability
     */
    private int calculateSafetyStock(Book book, double averageDemand) {
        // Calculate demand variability (standard deviation)
        double demandVariability = calculateDemandVariability(book);
        
        // Service level factor (Z-score for 95% service level)
        double serviceLevelFactor = 1.65;
        
        // Lead time (assumed 2 weeks for tech books)
        double leadTimeWeeks = 2.0;
        
        // Safety stock formula: Z * σ * sqrt(LT)
        double safetyStock = serviceLevelFactor * demandVariability * Math.sqrt(leadTimeWeeks / 4.0);
        
        return Math.max(1, (int) Math.round(safetyStock));
    }

    /**
     * Calculate reorder point
     */
    private int calculateReorderPoint(Book book, double averageDemand, int safetyStock) {
        // Lead time demand (average demand during lead time)
        double leadTimeDemand = averageDemand * (2.0 / 4.0); // 2 weeks out of 4 weeks/month
        
        // Reorder point = Lead time demand + Safety stock
        int reorderPoint = (int) Math.round(leadTimeDemand) + safetyStock;
        
        return Math.max(1, reorderPoint);
    }

    /**
     * Calculate obsolescence factor based on tech lifecycle
     */
    private BigDecimal calculateObsolescenceFactor(Book book) {
        // Check if obsolescence analysis exists
        try {
            // This would use the TechObsolescenceAnalysisService
            // For now, use a simplified approach based on publication date
            if (book.getPublicationDate() == null) {
                return BigDecimal.valueOf(0.9);
            }
            
            long yearsOld = LocalDate.now().getYear() - book.getPublicationDate().getYear();
            
            if (yearsOld <= 1) {
                return BigDecimal.valueOf(1.0); // New books, no obsolescence factor
            } else if (yearsOld <= 3) {
                return BigDecimal.valueOf(0.95); // Slight reduction for moderately old books
            } else if (yearsOld <= 5) {
                return BigDecimal.valueOf(0.85); // Significant reduction for older books
            } else {
                return BigDecimal.valueOf(0.70); // High obsolescence risk for very old books
            }
        } catch (Exception e) {
            logger.warn("Could not calculate obsolescence factor for book {}: {}", book.getId(), e.getMessage());
            return BigDecimal.valueOf(0.9);
        }
    }

    /**
     * Calculate trend factor based on technology trends
     */
    private BigDecimal calculateTrendFactor(Book book) {
        // Simplified trend factor - would integrate with TechTrendAnalysisService
        // For now, assume stable trends
        return BigDecimal.valueOf(1.0);
    }

    /**
     * Calculate seasonality factor
     */
    private BigDecimal calculateSeasonalityFactor(Book book) {
        // Use current month to determine seasonal factor
        String currentMonth = LocalDate.now().getMonth().toString();
        
        // Simplified seasonal factors for tech books
        switch (currentMonth) {
            case "SEPTEMBER":
            case "OCTOBER":
                return BigDecimal.valueOf(1.3); // Back to school season
            case "JANUARY":
            case "FEBRUARY":
                return BigDecimal.valueOf(1.1); // New year learning
            case "JUNE":
            case "JULY":
                return BigDecimal.valueOf(0.9); // Summer slowdown
            default:
                return BigDecimal.valueOf(1.0);
        }
    }

    /**
     * Calculate average monthly demand for a book
     */
    private double calculateAverageDemand(Book book) {
        // Get historical orders from last 12 months
        LocalDate startDate = LocalDate.now().minusMonths(12);
        LocalDate endDate = LocalDate.now();
        
        List<com.techbookstore.app.entity.Order> orders = orderRepository.findByOrderDateBetween(
            startDate.atStartOfDay(), endDate.atStartOfDay());
        
        int totalDemand = orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .filter(item -> item.getBook().getId().equals(book.getId()))
            .mapToInt(item -> item.getQuantity())
            .sum();
        
        return totalDemand / 12.0; // Average per month
    }

    /**
     * Calculate demand variability (standard deviation)
     */
    private double calculateDemandVariability(Book book) {
        // Simplified calculation - in practice would use historical monthly demands
        double averageDemand = calculateAverageDemand(book);
        return Math.max(1.0, averageDemand * 0.3); // Assume 30% coefficient of variation
    }

    /**
     * Determine stock status based on current levels
     */
    private String determineStockStatus(int currentStock, int optimalStock, int reorderPoint) {
        if (currentStock <= reorderPoint) {
            return "REORDER_NEEDED";
        } else if (currentStock < optimalStock * 0.8) {
            return "UNDERSTOCK";
        } else if (currentStock > optimalStock * 1.2) {
            return "OVERSTOCK";
        } else {
            return "OPTIMAL";
        }
    }

    /**
     * Calculate estimated cost for recommended order
     */
    private BigDecimal calculateEstimatedCost(Book book, int quantity) {
        BigDecimal unitCost = book.getSellingPrice().multiply(BigDecimal.valueOf(0.7)); // Assume 70% cost ratio
        return unitCost.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate estimated revenue for recommended order
     */
    private BigDecimal calculateEstimatedRevenue(Book book, int quantity) {
        return book.getSellingPrice().multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Save optimal stock settings
     */
    public OptimalStockSettings saveOptimalStockSettings(OptimalStockDto dto) {
        Book book = bookRepository.findById(dto.getBookId())
            .orElseThrow(() -> new RuntimeException("Book not found: " + dto.getBookId()));
        
        OptimalStockSettings settings = new OptimalStockSettings(
            book, 
            dto.getOptimalStockLevel(), 
            dto.getReorderPoint(), 
            dto.getSafetyStock(), 
            dto.getEconomicOrderQuantity()
        );
        
        settings.setObsolescenceFactor(dto.getObsolescenceFactor());
        settings.setTrendFactor(dto.getTrendFactor());
        settings.setSeasonalityFactor(dto.getSeasonalityFactor());
        
        return optimalStockSettingsRepository.save(settings);
    }

    /**
     * Get books that need reordering
     */
    @Transactional(readOnly = true)
    public List<OptimalStockDto> getBooksNeedingReorder() {
        LocalDate today = LocalDate.now();
        List<OptimalStockSettings> booksNeedingReorder = 
            optimalStockSettingsRepository.findBooksNeedingReorder(today);
        
        return booksNeedingReorder.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    /**
     * Convert OptimalStockSettings to DTO
     */
    private OptimalStockDto convertToDto(OptimalStockSettings settings) {
        Book book = settings.getBook();
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBookId(book.getId());
        int currentStock = inventoryOpt.map(Inventory::getTotalStock).orElse(0);
        
        OptimalStockDto dto = new OptimalStockDto(
            book.getId(), 
            book.getTitle(), 
            currentStock,
            settings.getOptimalStockLevel(), 
            settings.getReorderPoint(), 
            settings.getSafetyStock()
        );
        
        dto.setEconomicOrderQuantity(settings.getEconomicOrderQuantity());
        dto.setObsolescenceFactor(settings.getObsolescenceFactor());
        dto.setTrendFactor(settings.getTrendFactor());
        dto.setSeasonalityFactor(settings.getSeasonalityFactor());
        dto.setValidFrom(settings.getValidFrom());
        dto.setValidTo(settings.getValidTo());
        dto.setStockStatus(determineStockStatus(currentStock, settings.getOptimalStockLevel(), settings.getReorderPoint()));
        
        return dto;
    }

    /**
     * Calculate optimal levels for all books
     * For use in integrated analysis
     */
    @Transactional(readOnly = true)
    public List<com.techbookstore.app.dto.OptimalStockLevel> calculateOptimalLevels() {
        logger.info("Calculating optimal levels for all books");
        
        List<Book> books = bookRepository.findAll();
        return books.stream()
            .map(book -> {
                try {
                    OptimalStockDto optimalDto = calculateOptimalStock(book.getId());
                    
                    com.techbookstore.app.dto.OptimalStockLevel level = new com.techbookstore.app.dto.OptimalStockLevel();
                    level.setBookId(book.getId());
                    level.setBookTitle(book.getTitle());
                    level.setOptimalStock(optimalDto.getOptimalStockLevel());
                    level.setSafetyStock(optimalDto.getSafetyStock());
                    level.setReorderPoint(optimalDto.getReorderPoint());
                    
                    // Get current stock
                    Optional<Inventory> inventory = inventoryRepository.findByBookId(book.getId());
                    level.setCurrentStock(inventory.map(Inventory::getTotalStock).orElse(0));
                    
                    // Set calculation method and recommendation
                    level.setCalculationMethod("EOQ");
                    level.setLeadTimeDays(7); // Default lead time
                    
                    // Determine recommendation
                    Integer currentStock = level.getCurrentStock();
                    Integer optimalStock = level.getOptimalStock();
                    
                    if (currentStock < optimalStock * 0.8) {
                        level.setRecommendation("INCREASE");
                    } else if (currentStock > optimalStock * 1.2) {
                        level.setRecommendation("DECREASE");
                    } else {
                        level.setRecommendation("MAINTAIN");
                    }
                    
                    return level;
                } catch (Exception e) {
                    logger.warn("Failed to calculate optimal level for book {}: {}", book.getId(), e.getMessage());
                    return null;
                }
            })
            .filter(level -> level != null)
            .collect(Collectors.toList());
    }

    /**
     * Inner class for EOQ calculation results
     */
    private static class EOQCalculationResult {
        private final int eoq;
        private final double averageDemand;

        public EOQCalculationResult(int eoq, double averageDemand) {
            this.eoq = eoq;
            this.averageDemand = averageDemand;
        }

        public int getEoq() { return eoq; }
        public double getAverageDemand() { return averageDemand; }
    }
}