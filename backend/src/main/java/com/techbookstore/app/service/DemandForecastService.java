package com.techbookstore.app.service;

import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.DemandForecast;
import com.techbookstore.app.entity.ForecastAccuracy;
import com.techbookstore.app.entity.Order;
import com.techbookstore.app.repository.DemandForecastRepository;
import com.techbookstore.app.repository.ForecastAccuracyRepository;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.OrderRepository;
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
 * Advanced Demand Forecasting Service with 5 algorithms
 * 5つのアルゴリズムを使用した高度な需要予測サービス
 */
@Service
@Transactional
public class DemandForecastService {

    private static final Logger logger = LoggerFactory.getLogger(DemandForecastService.class);

    private final DemandForecastRepository demandForecastRepository;
    private final ForecastAccuracyRepository forecastAccuracyRepository;
    private final BookRepository bookRepository;
    private final OrderRepository orderRepository;
    private final SeasonalAnalysisService seasonalAnalysisService;
    private final TechTrendAnalysisService techTrendAnalysisService;
    
    // Seasonal factors cache
    private final Map<String, BigDecimal> seasonalFactors;

    public DemandForecastService(DemandForecastRepository demandForecastRepository,
                                ForecastAccuracyRepository forecastAccuracyRepository,
                                BookRepository bookRepository,
                                OrderRepository orderRepository,
                                SeasonalAnalysisService seasonalAnalysisService,
                                TechTrendAnalysisService techTrendAnalysisService) {
        this.demandForecastRepository = demandForecastRepository;
        this.forecastAccuracyRepository = forecastAccuracyRepository;
        this.bookRepository = bookRepository;
        this.orderRepository = orderRepository;
        this.seasonalAnalysisService = seasonalAnalysisService;
        this.techTrendAnalysisService = techTrendAnalysisService;
        
        // Initialize seasonal factors
        this.seasonalFactors = new HashMap<>();
        this.seasonalFactors.put("JANUARY", BigDecimal.valueOf(1.1));
        this.seasonalFactors.put("FEBRUARY", BigDecimal.valueOf(1.0));
        this.seasonalFactors.put("MARCH", BigDecimal.valueOf(1.3));
        this.seasonalFactors.put("APRIL", BigDecimal.valueOf(1.2));
        this.seasonalFactors.put("MAY", BigDecimal.valueOf(1.0));
        this.seasonalFactors.put("JUNE", BigDecimal.valueOf(0.9));
        this.seasonalFactors.put("JULY", BigDecimal.valueOf(1.1));
        this.seasonalFactors.put("AUGUST", BigDecimal.valueOf(1.2));
        this.seasonalFactors.put("SEPTEMBER", BigDecimal.valueOf(1.4));
        this.seasonalFactors.put("OCTOBER", BigDecimal.valueOf(1.3));
        this.seasonalFactors.put("NOVEMBER", BigDecimal.valueOf(1.2));
        this.seasonalFactors.put("DECEMBER", BigDecimal.valueOf(1.0));
    }

    /**
     * Generate demand forecast using all available algorithms
     * 全てのアルゴリズムを使用して需要予測を生成
     */
    public List<DemandForecast> generateForecast(Long bookId, LocalDate forecastDate, int periodMonths) {
        logger.info("Generating demand forecast for book {} for {} months starting from {}", bookId, periodMonths, forecastDate);
        
        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new RuntimeException("Book not found: " + bookId));

        List<DemandForecast> forecasts = new ArrayList<>();
        
        // 1. Moving Average
        DemandForecast movingAverage = calculateMovingAverage(book, forecastDate, periodMonths);
        forecasts.add(movingAverage);
        
        // 2. Exponential Smoothing
        DemandForecast exponentialSmoothing = calculateExponentialSmoothing(book, forecastDate, periodMonths);
        forecasts.add(exponentialSmoothing);
        
        // 3. Linear Regression
        DemandForecast linearRegression = calculateLinearRegression(book, forecastDate, periodMonths);
        forecasts.add(linearRegression);
        
        // 4. Seasonal Adjustment
        DemandForecast seasonalAdjustment = calculateSeasonalAdjustment(book, forecastDate, periodMonths);
        forecasts.add(seasonalAdjustment);
        
        // 5. Ensemble Forecast
        DemandForecast ensembleForecast = calculateEnsembleForecast(forecasts, book, forecastDate);
        forecasts.add(ensembleForecast);
        
        // Save all forecasts
        forecasts = demandForecastRepository.saveAll(forecasts);
        
        logger.info("Generated {} forecasts for book {}", forecasts.size(), bookId);
        return forecasts;
    }

    /**
     * 1. Moving Average Algorithm - 移動平均法
     */
    private DemandForecast calculateMovingAverage(Book book, LocalDate forecastDate, int periodMonths) {
        LocalDate startDate = forecastDate.minusMonths(6); // Use last 6 months
        List<Order> historicalOrders = orderRepository.findByOrderDateBetween(
            startDate.atStartOfDay(), forecastDate.atStartOfDay());
        
        // Calculate average demand for this book
        double averageDemand = historicalOrders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .filter(item -> item.getBook().getId().equals(book.getId()))
            .mapToInt(item -> item.getQuantity())
            .average()
            .orElse(0.0);
        
        int predictedDemand = Math.max(1, (int) Math.round(averageDemand * periodMonths));
        
        return new DemandForecast(book, forecastDate, predictedDemand, "MOVING_AVERAGE", new java.math.BigDecimal("0.70"));
    }

    /**
     * 2. Exponential Smoothing Algorithm - 指数平滑法
     */
    private DemandForecast calculateExponentialSmoothing(Book book, LocalDate forecastDate, int periodMonths) {
        double alpha = 0.3; // Smoothing factor
        LocalDate startDate = forecastDate.minusMonths(12); // Use last 12 months
        
        List<Order> historicalOrders = orderRepository.findByOrderDateBetween(
            startDate.atStartOfDay(), forecastDate.atStartOfDay());
        
        // Get monthly demand data
        Map<LocalDate, Integer> monthlyDemand = getMonthlyDemandData(book, historicalOrders);
        
        if (monthlyDemand.isEmpty()) {
            return new DemandForecast(book, forecastDate, 1, "EXPONENTIAL_SMOOTHING", new java.math.BigDecimal("0.60"));
        }
        
        // Apply exponential smoothing
        double smoothedValue = monthlyDemand.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
        
        for (Integer demand : monthlyDemand.values()) {
            smoothedValue = alpha * demand + (1 - alpha) * smoothedValue;
        }
        
        int predictedDemand = Math.max(1, (int) Math.round(smoothedValue * periodMonths));
        
        return new DemandForecast(book, forecastDate, predictedDemand, "EXPONENTIAL_SMOOTHING", new java.math.BigDecimal("0.75"));
    }

    /**
     * 3. Linear Regression Algorithm - 線形回帰
     */
    private DemandForecast calculateLinearRegression(Book book, LocalDate forecastDate, int periodMonths) {
        LocalDate startDate = forecastDate.minusMonths(12);
        List<Order> historicalOrders = orderRepository.findByOrderDateBetween(
            startDate.atStartOfDay(), forecastDate.atStartOfDay());
        
        Map<LocalDate, Integer> monthlyDemand = getMonthlyDemandData(book, historicalOrders);
        
        if (monthlyDemand.size() < 3) {
            return new DemandForecast(book, forecastDate, 1, "LINEAR_REGRESSION", new java.math.BigDecimal("0.50"));
        }
        
        // Simple linear regression calculation
        List<LocalDate> dates = new ArrayList<>(monthlyDemand.keySet());
        dates.sort(LocalDate::compareTo);
        
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        int n = dates.size();
        
        for (int i = 0; i < n; i++) {
            double x = i + 1; // Time index
            double y = monthlyDemand.get(dates.get(i));
            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumX2 += x * x;
        }
        
        double slope = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double intercept = (sumY - slope * sumX) / n;
        
        // Predict for next period
        double nextX = n + periodMonths;
        double prediction = slope * nextX + intercept;
        
        int predictedDemand = Math.max(1, (int) Math.round(prediction));
        
        return new DemandForecast(book, forecastDate, predictedDemand, "LINEAR_REGRESSION", new java.math.BigDecimal("0.65"));
    }

    /**
     * 4. Seasonal Adjustment Algorithm - 季節性調整
     */
    private DemandForecast calculateSeasonalAdjustment(Book book, LocalDate forecastDate, int periodMonths) {
        // Get base forecast using moving average
        DemandForecast baseForecast = calculateMovingAverage(book, forecastDate, periodMonths);
        
        // Apply seasonal adjustment based on book category
        String categoryCode = "GENERAL"; // Simplified - we'll enhance this later
        
        // Get seasonal factor for the forecast month
        BigDecimal seasonalFactor = getSeasonalFactor(categoryCode, forecastDate.getMonth().toString());
        
        int adjustedDemand = BigDecimal.valueOf(baseForecast.getPredictedDemand())
            .multiply(seasonalFactor)
            .setScale(0, RoundingMode.HALF_UP)
            .intValue();
        
        return new DemandForecast(book, forecastDate, Math.max(1, adjustedDemand), "SEASONAL_ADJUSTED", new java.math.BigDecimal("0.80"));
    }

    /**
     * 5. Ensemble Forecast - アンサンブル予測
     */
    private DemandForecast calculateEnsembleForecast(List<DemandForecast> forecasts, Book book, LocalDate forecastDate) {
        // Weighted average based on algorithm confidence
        Map<String, Double> weights = new HashMap<>();
        weights.put("MOVING_AVERAGE", 0.25);
        weights.put("EXPONENTIAL_SMOOTHING", 0.30);
        weights.put("LINEAR_REGRESSION", 0.25);
        weights.put("SEASONAL_ADJUSTED", 0.20);
        
        double weightedSum = 0.0;
        double totalWeight = 0.0;
        
        for (DemandForecast forecast : forecasts) {
            String algorithm = forecast.getAlgorithm();
            if (weights.containsKey(algorithm)) {
                double weight = weights.get(algorithm);
                weightedSum += forecast.getPredictedDemand() * weight;
                totalWeight += weight;
            }
        }
        
        int ensemblePrediction = totalWeight > 0 
            ? (int) Math.round(weightedSum / totalWeight)
            : 1;
        
        return new DemandForecast(book, forecastDate, Math.max(1, ensemblePrediction), "ENSEMBLE", new java.math.BigDecimal("0.85"));
    }

    /**
     * Helper method to get monthly demand data
     */
    private Map<LocalDate, Integer> getMonthlyDemandData(Book book, List<Order> orders) {
        return orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .filter(item -> item.getBook().getId().equals(book.getId()))
            .collect(Collectors.groupingBy(
                item -> LocalDate.of(
                    item.getOrder().getOrderDate().getYear(),
                    item.getOrder().getOrderDate().getMonth(),
                    1
                ),
                Collectors.summingInt(item -> item.getQuantity())
            ));
    }

    /**
     * Helper method to get seasonal factor
     */
    private BigDecimal getSeasonalFactor(String categoryCode, String month) {
        // Simplified seasonal factors based on tech book patterns
        return seasonalFactors.getOrDefault(month, BigDecimal.ONE);
    }

    /**
     * Evaluate forecast accuracy
     */
    @Transactional(readOnly = true)
    public ForecastAccuracy evaluateAccuracy(String algorithm, LocalDate fromDate, LocalDate toDate) {
        logger.info("Evaluating accuracy for algorithm {} from {} to {}", algorithm, fromDate, toDate);
        
        List<DemandForecast> forecasts = demandForecastRepository.findByAlgorithmAndForecastDate(algorithm, fromDate);
        
        if (forecasts.isEmpty()) {
            return new ForecastAccuracy(algorithm, BigDecimal.ZERO, BigDecimal.valueOf(100), BigDecimal.ZERO, fromDate, toDate);
        }
        
        // Calculate accuracy metrics (simplified implementation)
        double totalError = forecasts.stream()
            .mapToDouble(f -> Math.abs(f.getPredictedDemand() - getActualDemand(f.getBook(), f.getForecastDate())))
            .sum();
        
        double mae = totalError / forecasts.size();
        double mape = forecasts.stream()
            .mapToDouble(f -> {
                int actual = getActualDemand(f.getBook(), f.getForecastDate());
                return actual > 0 ? Math.abs(f.getPredictedDemand() - actual) / (double) actual * 100 : 0;
            })
            .average()
            .orElse(0.0);
        
        double rmse = Math.sqrt(forecasts.stream()
            .mapToDouble(f -> Math.pow(f.getPredictedDemand() - getActualDemand(f.getBook(), f.getForecastDate()), 2))
            .average()
            .orElse(0.0));
        
        return new ForecastAccuracy(
            algorithm,
            BigDecimal.valueOf(mae).setScale(2, RoundingMode.HALF_UP),
            BigDecimal.valueOf(mape).setScale(2, RoundingMode.HALF_UP),
            BigDecimal.valueOf(rmse).setScale(2, RoundingMode.HALF_UP),
            fromDate,
            toDate
        );
    }

    /**
     * Generate ensemble forecasts for integrated analysis
     * Simplified method for Phase 4 integration
     */
    public List<com.techbookstore.app.dto.DemandForecastResult> generateEnsembleForecasts(Integer horizonDays) {
        logger.info("Generating ensemble forecasts for {} days horizon", horizonDays);
        
        List<com.techbookstore.app.dto.DemandForecastResult> results = new ArrayList<>();
        List<Book> books = bookRepository.findAll();
        
        LocalDate forecastDate = LocalDate.now().plusDays(horizonDays);
        
        for (Book book : books) {
            try {
                // Simplified forecast calculation
                com.techbookstore.app.dto.DemandForecastResult forecast = 
                    new com.techbookstore.app.dto.DemandForecastResult();
                forecast.setBookId(book.getId());
                forecast.setBookTitle(book.getTitle());
                forecast.setForecastDate(forecastDate);
                forecast.setHorizon(horizonDays);
                forecast.setAlgorithm("ENSEMBLE");
                
                // Simple forecast based on average demand
                int averageDemand = calculateAverageDemand(book);
                forecast.setForecastedDemand(averageDemand);
                forecast.setConfidenceLevel(BigDecimal.valueOf(75.0)); // Default confidence
                
                // Set bounds
                forecast.setUpperBound(BigDecimal.valueOf(averageDemand * 1.2));
                forecast.setLowerBound(BigDecimal.valueOf(averageDemand * 0.8));
                
                results.add(forecast);
                
            } catch (Exception e) {
                logger.warn("Failed to generate forecast for book {}: {}", book.getId(), e.getMessage());
            }
        }
        
        return results;
    }
    
    /**
     * Calculate simple average demand for a book
     */
    private int calculateAverageDemand(Book book) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(90); // 3 months
        
        List<Order> orders = orderRepository.findByOrderDateBetween(
            startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        int totalDemand = orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .filter(item -> item.getBook().getId().equals(book.getId()))
            .mapToInt(item -> item.getQuantity())
            .sum();
        
        // Average per month over 3 months
        return Math.max(1, totalDemand / 3);
    }

    /**
     * Helper method to get actual demand for a book on a specific date
     */
    private int getActualDemand(Book book, LocalDate date) {
        LocalDate startDate = date.withDayOfMonth(1);
        LocalDate endDate = date.withDayOfMonth(date.lengthOfMonth());
        
        List<Order> orders = orderRepository.findByOrderDateBetween(
            startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        return orders.stream()
            .flatMap(order -> order.getOrderItems().stream())
            .filter(item -> item.getBook().getId().equals(book.getId()))
            .mapToInt(item -> item.getQuantity())
            .sum();
    }
}