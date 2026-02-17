package com.techbookstore.app.service;

import com.techbookstore.app.dto.*;
import com.techbookstore.app.entity.AggregationCache;
import com.techbookstore.app.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Service class for generating reports and analytics.
 */
@Service
@Transactional(readOnly = true)
public class ReportService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReportService.class);
    
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final InventoryRepository inventoryRepository;
    private final BookRepository bookRepository;
    private final AggregationCacheRepository cacheRepository;
    
    /**
     * Constructor injection for dependencies.
     */
    public ReportService(OrderRepository orderRepository, CustomerRepository customerRepository,
                        InventoryRepository inventoryRepository, BookRepository bookRepository,
                        AggregationCacheRepository cacheRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.inventoryRepository = inventoryRepository;
        this.bookRepository = bookRepository;
        this.cacheRepository = cacheRepository;
    }
    
    /**
     * Generate sales report for the specified date range.
     */
    public SalesReportDto generateSalesReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating sales report from {} to {}", startDate, endDate);
        
        // Calculate basic metrics
        BigDecimal totalRevenue = calculateTotalRevenue(startDate, endDate);
        Integer totalOrders = calculateTotalOrders(startDate, endDate);
        BigDecimal averageOrderValue = totalOrders > 0 ? 
            totalRevenue.divide(new BigDecimal(totalOrders), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        
        // Create report
        SalesReportDto report = new SalesReportDto(startDate, endDate, totalRevenue, totalOrders, averageOrderValue);
        
        // Add trends (simplified for now - using mock data)
        report.setTrends(generateSalesTrends(startDate, endDate));
        
        // Add rankings (simplified for now - using mock data)
        report.setRankings(generateSalesRankings(null, 10));
        
        // Add breakdown
        report.setBreakdown(generateSalesBreakdown(startDate, endDate));
        
        return report;
    }
    
    /**
     * Generate inventory report.
     */
    public InventoryReportDto generateInventoryReport() {
        return generateInventoryReport(null, null, null, null, null, null);
    }

    /**
     * Generate enhanced inventory report with filtering capabilities.
     * Phase 1 enhancement with comprehensive filtering and analytics.
     */
    public InventoryReportDto generateInventoryReport(String category, String level, String publisher, 
                                                     String stockStatus, String priceRange, Integer publicationYear) {
        logger.info("Generating enhanced inventory report with filters - category: {}, level: {}, publisher: {}, stockStatus: {}, priceRange: {}, publicationYear: {}", 
                   category, level, publisher, stockStatus, priceRange, publicationYear);
        
        LocalDate reportDate = LocalDate.now();
        
        // Calculate filtered metrics
        InventoryMetrics metrics = calculateInventoryMetrics(category, level, publisher, stockStatus, priceRange, publicationYear);
        
        InventoryReportDto report = new InventoryReportDto(reportDate, metrics.getTotalProducts(), 
                                                          metrics.getLowStockCount(), metrics.getOutOfStockCount(), 
                                                          metrics.getTotalInventoryValue());
        
        // Add filtered inventory items
        report.setItems(generateFilteredInventoryItems(category, level, publisher, stockStatus, priceRange, publicationYear));
        
        // Add reorder suggestions based on filters
        report.setReorderSuggestions(generateFilteredReorderSuggestions(category, level, publisher));
        
        // Add enhanced turnover summary with analytics
        report.setTurnoverSummary(generateEnhancedTurnoverSummary(category));
        
        // Add new analytics fields
        report.setDeadStockItems(metrics.getDeadStockCount());
        report.setDeadStockValue(metrics.getDeadStockValue());
        report.setAverageTurnoverRate(metrics.getAverageTurnoverRate());
        report.setObsolescenceRiskIndex(metrics.getObsolescenceRiskIndex());
        
        return report;
    }

    /**
     * Calculate inventory metrics with filtering.
     */
    private InventoryMetrics calculateInventoryMetrics(String category, String level, String publisher, 
                                                      String stockStatus, String priceRange, Integer publicationYear) {
        // This would normally query the database with filters
        // For Phase 1, using enhanced mock data with realistic calculations
        
        Integer totalProducts = Math.toIntExact(bookRepository.count());
        
        // Apply filtering logic (simplified for Phase 1)
        if (category != null) totalProducts = (int)(totalProducts * 0.7);
        if (level != null) totalProducts = (int)(totalProducts * 0.6);
        if (publisher != null) totalProducts = (int)(totalProducts * 0.5);
        
        // Calculate enhanced metrics
        Integer lowStockCount = Math.max(1, totalProducts / 10);
        Integer outOfStockCount = Math.max(0, totalProducts / 20);
        Integer deadStockCount = Math.max(0, totalProducts / 15);
        
        BigDecimal totalInventoryValue = new BigDecimal(totalProducts * 3500);
        BigDecimal deadStockValue = totalInventoryValue.multiply(new BigDecimal("0.08"));
        Double averageTurnoverRate = 4.2 + (ThreadLocalRandom.current().nextDouble() * 2 - 1); // 3.2 - 5.2 range
        Double obsolescenceRiskIndex = ThreadLocalRandom.current().nextDouble() * 100; // 0-100 scale
        
        return new InventoryMetrics(totalProducts, lowStockCount, outOfStockCount, deadStockCount,
                                   totalInventoryValue, deadStockValue, averageTurnoverRate, obsolescenceRiskIndex);
    }

    /**
     * Generate enhanced turnover summary with category analysis.
     */
    private InventoryReportDto.InventoryTurnoverSummary generateEnhancedTurnoverSummary(String category) {
        Double averageTurnover = 4.2;
        String highestCategory = "Java";
        String lowestCategory = "Database";
        
        if (category != null) {
            // Adjust based on category filter
            switch (category.toLowerCase()) {
                case "java":
                    averageTurnover = 5.1;
                    highestCategory = "Spring";
                    lowestCategory = "Legacy Java";
                    break;
                case "python":
                    averageTurnover = 4.8;
                    highestCategory = "AI/ML";
                    lowestCategory = "Python 2.x";
                    break;
                case "javascript":
                    averageTurnover = 4.5;
                    highestCategory = "React";
                    lowestCategory = "jQuery";
                    break;
                default:
                    averageTurnover = 3.8;
            }
        }
        
        return new InventoryReportDto.InventoryTurnoverSummary(averageTurnover, highestCategory, lowestCategory);
    }

    /**
     * Generate filtered inventory items.
     */
    private List<InventoryReportDto.InventoryItem> generateFilteredInventoryItems(String category, String level, 
                                                                                 String publisher, String stockStatus, 
                                                                                 String priceRange, Integer publicationYear) {
        // Enhanced mock data with filtering simulation
        List<InventoryReportDto.InventoryItem> items = generateInventoryItems();
        
        // Apply basic filtering (simplified for Phase 1)
        if (category != null) {
            items = items.stream()
                   .filter(item -> item.getCategory().toLowerCase().contains(category.toLowerCase()))
                   .collect(java.util.stream.Collectors.toList());
        }
        
        if (stockStatus != null) {
            items = items.stream()
                   .filter(item -> item.getStockStatus().equals(stockStatus))
                   .collect(java.util.stream.Collectors.toList());
        }
        
        return items;
    }

    /**
     * Generate filtered reorder suggestions.
     */
    private List<InventoryReportDto.ReorderSuggestion> generateFilteredReorderSuggestions(String category, String level, String publisher) {
        List<InventoryReportDto.ReorderSuggestion> suggestions = generateReorderSuggestions();
        
        // Apply category filter if specified
        // Note: ReorderSuggestion doesn't have category field, so we filter by title patterns for now
        if (category != null) {
            suggestions = suggestions.stream()
                         .filter(s -> s.getTitle().toLowerCase().contains(category.toLowerCase()))
                         .collect(java.util.stream.Collectors.toList());
        }
        
        return suggestions;
    }

    /**
     * Inner class for inventory metrics calculation results.
     */
    private static class InventoryMetrics {
        private final Integer totalProducts;
        private final Integer lowStockCount;
        private final Integer outOfStockCount;
        private final Integer deadStockCount;
        private final BigDecimal totalInventoryValue;
        private final BigDecimal deadStockValue;
        private final Double averageTurnoverRate;
        private final Double obsolescenceRiskIndex;
        
        public InventoryMetrics(Integer totalProducts, Integer lowStockCount, Integer outOfStockCount, 
                               Integer deadStockCount, BigDecimal totalInventoryValue, BigDecimal deadStockValue,
                               Double averageTurnoverRate, Double obsolescenceRiskIndex) {
            this.totalProducts = totalProducts;
            this.lowStockCount = lowStockCount;
            this.outOfStockCount = outOfStockCount;
            this.deadStockCount = deadStockCount;
            this.totalInventoryValue = totalInventoryValue;
            this.deadStockValue = deadStockValue;
            this.averageTurnoverRate = averageTurnoverRate;
            this.obsolescenceRiskIndex = obsolescenceRiskIndex;
        }
        
        // Getters
        public Integer getTotalProducts() { return totalProducts; }
        public Integer getLowStockCount() { return lowStockCount; }
        public Integer getOutOfStockCount() { return outOfStockCount; }
        public Integer getDeadStockCount() { return deadStockCount; }
        public BigDecimal getTotalInventoryValue() { return totalInventoryValue; }
        public BigDecimal getDeadStockValue() { return deadStockValue; }
        public Double getAverageTurnoverRate() { return averageTurnoverRate; }
        public Double getObsolescenceRiskIndex() { return obsolescenceRiskIndex; }
    }
    
    /**
     * Generate customer analytics report.
     */
    public CustomerAnalyticsDto generateCustomerAnalytics() {
        logger.info("Generating customer analytics report");
        
        LocalDate reportDate = LocalDate.now();
        
        // Calculate basic metrics
        Integer totalCustomers = Math.toIntExact(customerRepository.count());
        Integer activeCustomers = Math.toIntExact(customerRepository.count()) - 5; // Mock calculation
        BigDecimal averageCustomerValue = new BigDecimal("5500.00"); // Mock data
        
        CustomerAnalyticsDto report = new CustomerAnalyticsDto(reportDate, totalCustomers, 
                                                              activeCustomers, averageCustomerValue);
        
        // Add customer segments
        report.setSegments(generateCustomerSegments());
        
        // Add RFM analysis
        report.setRfmAnalysis(generateRFMAnalysis());
        
        // Add trends
        report.setTrends(generateCustomerTrends());
        
        return report;
    }
    
    /**
     * Generate dashboard KPIs.
     */
    public DashboardKpiDto generateDashboardKpis() {
        logger.info("Generating dashboard KPIs");
        
        LocalDate reportDate = LocalDate.now();
        DashboardKpiDto dashboard = new DashboardKpiDto(reportDate);
        
        // Revenue KPIs
        DashboardKpiDto.RevenueKpis revenueKpis = new DashboardKpiDto.RevenueKpis(
            new BigDecimal("2500.00"), // Today
            new BigDecimal("18000.00"), // Week
            new BigDecimal("75000.00"), // Month
            new BigDecimal("900000.00"), // Year
            12.5 // Growth %
        );
        dashboard.setRevenue(revenueKpis);
        
        // Order KPIs
        DashboardKpiDto.OrderKpis orderKpis = new DashboardKpiDto.OrderKpis(
            8, // Today
            45, // Week
            180, // Month
            new BigDecimal("420.00"), // AOV
            8.3 // Growth %
        );
        dashboard.setOrders(orderKpis);
        
        // Customer KPIs
        Integer totalCustomers = Math.toIntExact(customerRepository.count());
        DashboardKpiDto.CustomerKpis customerKpis = new DashboardKpiDto.CustomerKpis(
            totalCustomers,
            15, // New this month
            totalCustomers - 5, // Active
            85.2, // Retention %
            5.8 // Growth %
        );
        dashboard.setCustomers(customerKpis);
        
        // Inventory KPIs
        Integer totalProducts = Math.toIntExact(bookRepository.count());
        DashboardKpiDto.InventoryKpis inventoryKpis = new DashboardKpiDto.InventoryKpis(
            totalProducts,
            5, // Low stock
            2, // Out of stock
            new BigDecimal("150000.00"), // Total value
            4.2 // Turnover
        );
        dashboard.setInventory(inventoryKpis);
        
        // Tech Trend KPIs (tech bookstore specific)
        DashboardKpiDto.TechTrendKpis techTrendKpis = calculateTechTrendKpis();
        dashboard.setTechTrends(techTrendKpis);
        
        // Trends
        dashboard.setTrends(generateTrendSummaries());
        
        return dashboard;
    }
    
    // Helper methods for calculations with caching
    private BigDecimal calculateTotalRevenue(LocalDate startDate, LocalDate endDate) {
        // Check cache first
        String cacheKey = "revenue_" + startDate + "_" + endDate;
        Optional<AggregationCache> cached = cacheRepository.findByKeyName(cacheKey);
        
        if (cached.isPresent() && 
            cached.get().getCreatedAt().isAfter(LocalDateTime.now().minusHours(1))) {
            // Return cached value if less than 1 hour old
            return new BigDecimal(cached.get().getValueData());
        }
        
        // Simplified calculation - in production would query order repository
        BigDecimal revenue = new BigDecimal("45000.00");
        
        // Cache the result
        AggregationCache cache = new AggregationCache(cacheKey, revenue.toString(), 
                                                      LocalDateTime.now().plusHours(1));
        cacheRepository.save(cache);
        
        return revenue;
    }
    
    private Integer calculateTotalOrders(LocalDate startDate, LocalDate endDate) {
        // Check cache first
        String cacheKey = "orders_" + startDate + "_" + endDate;
        Optional<AggregationCache> cached = cacheRepository.findByKeyName(cacheKey);
        
        if (cached.isPresent() && 
            cached.get().getCreatedAt().isAfter(LocalDateTime.now().minusHours(1))) {
            return Integer.valueOf(cached.get().getValueData());
        }
        
        // Simplified calculation - in production would query order repository
        Integer orders = 125;
        
        // Cache the result
        AggregationCache cache = new AggregationCache(cacheKey, orders.toString(), 
                                                      LocalDateTime.now().plusHours(1));
        cacheRepository.save(cache);
        
        return orders;
    }
    
    private List<SalesReportDto.SalesTrendItem> generateSalesTrends(LocalDate startDate, LocalDate endDate) {
        List<SalesReportDto.SalesTrendItem> trends = new ArrayList<>();
        // Generate sample trend data
        LocalDate current = startDate;
        while (!current.isAfter(endDate) && trends.size() < 30) {
            trends.add(new SalesReportDto.SalesTrendItem(
                current, 
                new BigDecimal(1500 + (int)(ThreadLocalRandom.current().nextDouble() * 1000)), 
                4 + (int)(ThreadLocalRandom.current().nextDouble() * 8)
            ));
            current = current.plusDays(1);
        }
        return trends;
    }
    
    private SalesReportDto.SalesBreakdown generateSalesBreakdown(LocalDate startDate, LocalDate endDate) {
        return new SalesReportDto.SalesBreakdown(
            new BigDecimal("28000.00"), // Online
            new BigDecimal("12000.00"), // Walk-in
            new BigDecimal("5000.00")   // Phone
        );
    }
    
    private List<InventoryReportDto.InventoryItem> generateInventoryItems() {
        return Arrays.asList(
            new InventoryReportDto.InventoryItem(1L, "Javaプログラミング入門", "Java", 15, 10, "OK", new BigDecimal("2880.00"), new BigDecimal("43200.00")),
            new InventoryReportDto.InventoryItem(2L, "Spring Boot実践ガイド", "Spring", 8, 10, "LOW", new BigDecimal("4050.00"), new BigDecimal("32400.00")),
            new InventoryReportDto.InventoryItem(3L, "React開発現場のテクニック", "React", 12, 8, "OK", new BigDecimal("3420.00"), new BigDecimal("41040.00")),
            new InventoryReportDto.InventoryItem(4L, "Python機械学習プログラミング", "Python", 0, 5, "OUT", new BigDecimal("3780.00"), new BigDecimal("0.00")),
            new InventoryReportDto.InventoryItem(5L, "AWSクラウド設計・構築ガイド", "AWS", 6, 8, "LOW", new BigDecimal("3240.00"), new BigDecimal("19440.00"))
        );
    }
    
    private List<InventoryReportDto.ReorderSuggestion> generateReorderSuggestions() {
        return Arrays.asList(
            new InventoryReportDto.ReorderSuggestion(4L, "Python機械学習プログラミング", 0, 10, "HIGH", 0),
            new InventoryReportDto.ReorderSuggestion(2L, "Spring Boot実践ガイド", 8, 5, "MEDIUM", 12),
            new InventoryReportDto.ReorderSuggestion(5L, "AWSクラウド設計・構築ガイド", 6, 5, "MEDIUM", 18)
        );
    }
    
    private List<CustomerAnalyticsDto.CustomerSegment> generateCustomerSegments() {
        return Arrays.asList(
            new CustomerAnalyticsDto.CustomerSegment("Premium", 15, new BigDecimal("45000.00"), 25.0, "High-value customers"),
            new CustomerAnalyticsDto.CustomerSegment("Regular", 35, new BigDecimal("82000.00"), 58.3, "Standard customers"),
            new CustomerAnalyticsDto.CustomerSegment("New", 10, new BigDecimal("12000.00"), 16.7, "Recent customers")
        );
    }
    
    private CustomerAnalyticsDto.RFMAnalysis generateRFMAnalysis() {
        List<CustomerAnalyticsDto.RFMSegment> segments = Arrays.asList(
            new CustomerAnalyticsDto.RFMSegment("Champions", 5, 5, 5, 8),
            new CustomerAnalyticsDto.RFMSegment("Loyal", 4, 4, 4, 12),
            new CustomerAnalyticsDto.RFMSegment("Potential", 3, 3, 3, 15),
            new CustomerAnalyticsDto.RFMSegment("At Risk", 2, 2, 2, 10)
        );
        
        return new CustomerAnalyticsDto.RFMAnalysis(segments, 8, 12, 15, 10);
    }
    
    private CustomerAnalyticsDto.CustomerTrends generateCustomerTrends() {
        List<CustomerAnalyticsDto.CustomerTrendItem> newCustomers = new ArrayList<>();
        List<CustomerAnalyticsDto.CustomerTrendItem> returningCustomers = new ArrayList<>();
        
        LocalDate current = LocalDate.now().minusDays(30);
        for (int i = 0; i < 30; i++) {
            newCustomers.add(new CustomerAnalyticsDto.CustomerTrendItem(current, 1 + (int)(ThreadLocalRandom.current().nextDouble() * 3), new BigDecimal("1500.00")));
            returningCustomers.add(new CustomerAnalyticsDto.CustomerTrendItem(current, 3 + (int)(ThreadLocalRandom.current().nextDouble() * 5), new BigDecimal("3500.00")));
            current = current.plusDays(1);
        }
        
        return new CustomerAnalyticsDto.CustomerTrends(newCustomers, returningCustomers, 85.2, 14.8);
    }
    
    private List<DashboardKpiDto.TrendSummary> generateTrendSummaries() {
        return Arrays.asList(
            new DashboardKpiDto.TrendSummary("Revenue", "Month", 12.5, "UP"),
            new DashboardKpiDto.TrendSummary("Orders", "Month", 8.3, "UP"),
            new DashboardKpiDto.TrendSummary("Customers", "Month", 5.8, "UP"),
            new DashboardKpiDto.TrendSummary("AOV", "Month", 4.2, "UP")
        );
    }
    
    // Additional report generation methods
    
    /**
     * Generate sales trend report for the specified date range.
     */
    public SalesReportDto generateSalesTrendReport(LocalDate startDate, LocalDate endDate) {
        logger.info("Generating sales trend report from {} to {}", startDate, endDate);
        
        SalesReportDto report = new SalesReportDto();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTrends(generateSalesTrends(startDate, endDate));
        
        return report;
    }
    
    /**
     * Generate sales ranking report with optional category filter.
     */
    public SalesReportDto generateSalesRankingReport(String category, int limit) {
        logger.info("Generating sales ranking report for category: {}, limit: {}", category, limit);
        
        SalesReportDto report = new SalesReportDto();
        report.setRankings(generateSalesRankings(category, limit));
        
        return report;
    }
    
    /**
     * Generate inventory turnover report with optional category filter.
     */
    public InventoryReportDto generateInventoryTurnoverReport(String category) {
        logger.info("Generating inventory turnover report for category: {}", category);
        
        InventoryReportDto report = new InventoryReportDto();
        report.setTurnoverSummary(new InventoryReportDto.InventoryTurnoverSummary(
            4.2, category != null ? category : "All", "Top performing category"));
        
        return report;
    }
    
    /**
     * Generate reorder suggestions report.
     */
    public InventoryReportDto generateReorderSuggestionsReport() {
        logger.info("Generating reorder suggestions report");
        
        InventoryReportDto report = new InventoryReportDto();
        report.setReorderSuggestions(generateReorderSuggestions());
        
        return report;
    }
    
    /**
     * Generate RFM analysis report.
     */
    public CustomerAnalyticsDto generateRFMAnalysisReport() {
        logger.info("Generating RFM analysis report");
        
        CustomerAnalyticsDto report = new CustomerAnalyticsDto();
        report.setRfmAnalysis(generateRFMAnalysis());
        
        return report;
    }
    
    /**
     * Generate customer segments report.
     */
    public CustomerAnalyticsDto generateCustomerSegmentsReport() {
        logger.info("Generating customer segments report");
        
        CustomerAnalyticsDto report = new CustomerAnalyticsDto();
        report.setSegments(generateCustomerSegments());
        
        return report;
    }
    
    /**
     * Generate dashboard trends report.
     */
    public DashboardKpiDto generateDashboardTrends() {
        logger.info("Generating dashboard trends report");
        
        DashboardKpiDto dashboard = generateDashboardKpis();
        
        // Enhanced trends with tech-specific metrics
        List<DashboardKpiDto.TrendSummary> techTrends = Arrays.asList(
            new DashboardKpiDto.TrendSummary("AI/ML Sales", "Month", 24.8, "UP"),
            new DashboardKpiDto.TrendSummary("Cloud Revenue", "Month", 18.5, "UP"),
            new DashboardKpiDto.TrendSummary("Web Dev Books", "Month", 8.3, "UP"),
            new DashboardKpiDto.TrendSummary("Legacy Tech", "Month", -12.1, "DOWN"),
            new DashboardKpiDto.TrendSummary("Innovation Index", "Month", 4.2, "UP")
        );
        
        dashboard.setTrends(techTrends);
        return dashboard;
    }
    
    /**
     * Generate tech trends report.
     */
    public SalesReportDto generateTechTrendsReport(int days) {
        logger.info("Generating tech trends report for {} days", days);
        
        LocalDate startDate = LocalDate.now().minusDays(days);
        LocalDate endDate = LocalDate.now();
        
        return generateSalesReport(startDate, endDate);
    }
    
    /**
     * Generate category trends report.
     */
    public SalesReportDto generateCategoryTrendsReport(String category, int days) {
        logger.info("Generating category trends report for {} over {} days", category, days);
        
        LocalDate startDate = LocalDate.now().minusDays(days);
        LocalDate endDate = LocalDate.now();
        
        SalesReportDto report = new SalesReportDto();
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setTrends(generateSalesTrends(startDate, endDate));
        
        return report;
    }
    
    /**
     * Generate custom report based on request parameters.
     */
    public SalesReportDto generateCustomReport(CustomReportRequest request) {
        logger.info("Generating custom report of type: {}", request.getReportType());
        
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().minusDays(30);
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.now();
        
        return generateSalesReport(startDate, endDate);
    }
    
    // Updated helper method to support filtering
    private List<SalesReportDto.SalesRankingItem> generateSalesRankings(String category, int limit) {
        List<SalesReportDto.SalesRankingItem> allRankings = Arrays.asList(
            new SalesReportDto.SalesRankingItem("Java", "Javaプログラミング入門", new BigDecimal("8640.00"), 3, 1),
            new SalesReportDto.SalesRankingItem("Spring", "Spring Boot実践ガイド", new BigDecimal("8100.00"), 2, 2),
            new SalesReportDto.SalesRankingItem("React", "React開発現場のテクニック", new BigDecimal("6840.00"), 2, 3),
            new SalesReportDto.SalesRankingItem("Python", "Python機械学習プログラミング", new BigDecimal("7560.00"), 2, 4),
            new SalesReportDto.SalesRankingItem("AWS", "AWSクラウド設計・構築ガイド", new BigDecimal("6480.00"), 2, 5)
        );
        
        return allRankings.stream()
                .filter(item -> category == null || item.getCategory().equalsIgnoreCase(category))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculate tech trend KPIs specific to technical bookstore.
     */
    private DashboardKpiDto.TechTrendKpis calculateTechTrendKpis() {
        // Tech bookstore specific trend calculations
        // In a real implementation, this would query actual data from database
        return new DashboardKpiDto.TechTrendKpis(
            "AI/Machine Learning", // Top rising tech
            24.8, // Rising growth %
            "jQuery", // Top falling tech  
            -15.3, // Falling decline %
            3, // Emerging tech count
            2, // Obsolete tech count
            new BigDecimal("8.5"), // Tech category diversity index
            92.4 // Innovation index
        );
    }
    
    /**
     * Generate tech trend alerts for the dashboard.
     */
    public List<TechTrendAlertDto> getTechTrendAlerts() {
        logger.info("Generating tech trend alerts");
        
        List<TechTrendAlertDto> alerts = new ArrayList<>();
        
        // Sample alerts for tech bookstore
        alerts.add(new TechTrendAlertDto(
            "AI/Machine Learning",
            "RISING",
            "HIGH",
            "AI/機械学習関連書籍の売上が急上昇中（+24.8%）",
            new BigDecimal("12500.00"),
            24.8,
            LocalDate.now(),
            "在庫拡充を検討してください"
        ));
        
        alerts.add(new TechTrendAlertDto(
            "React",
            "LOW_STOCK", 
            "MEDIUM",
            "React関連書籍の在庫が少なくなっています",
            new BigDecimal("8400.00"),
            null,
            LocalDate.now(),
            "発注が必要です"
        ));
        
        alerts.add(new TechTrendAlertDto(
            "jQuery",
            "FALLING",
            "LOW", 
            "jQuery関連書籍の需要が減少傾向（-15.3%）",
            new BigDecimal("3200.00"),
            -15.3,
            LocalDate.now(),
            "在庫調整を検討してください"
        ));
        
        return alerts;
    }
}