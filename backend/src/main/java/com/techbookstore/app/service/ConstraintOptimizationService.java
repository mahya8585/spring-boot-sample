package com.techbookstore.app.service;

import com.techbookstore.app.dto.OptimalStockDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for multi-objective constraint optimization
 * 多目的制約最適化サービス
 */
@Service
public class ConstraintOptimizationService {

    private static final Logger logger = LoggerFactory.getLogger(ConstraintOptimizationService.class);

    /**
     * Optimization constraints
     */
    public static class OptimizationConstraints {
        private BigDecimal maxBudget;
        private Integer maxItems;
        private Integer maxWeight; // kg
        private BigDecimal minProfitMargin;
        private String priorityFocus; // "PROFIT", "CASH_FLOW", "RISK_MINIMIZATION"

        public OptimizationConstraints() {
            this.maxBudget = BigDecimal.valueOf(50000);
            this.maxItems = 100;
            this.maxWeight = 1000;
            this.minProfitMargin = BigDecimal.valueOf(0.15);
            this.priorityFocus = "PROFIT";
        }

        // Getters and setters
        public BigDecimal getMaxBudget() { return maxBudget; }
        public void setMaxBudget(BigDecimal maxBudget) { this.maxBudget = maxBudget; }

        public Integer getMaxItems() { return maxItems; }
        public void setMaxItems(Integer maxItems) { this.maxItems = maxItems; }

        public Integer getMaxWeight() { return maxWeight; }
        public void setMaxWeight(Integer maxWeight) { this.maxWeight = maxWeight; }

        public BigDecimal getMinProfitMargin() { return minProfitMargin; }
        public void setMinProfitMargin(BigDecimal minProfitMargin) { this.minProfitMargin = minProfitMargin; }

        public String getPriorityFocus() { return priorityFocus; }
        public void setPriorityFocus(String priorityFocus) { this.priorityFocus = priorityFocus; }
    }

    /**
     * Optimization result
     */
    public static class OptimizationResult {
        private List<OptimalStockDto> selectedBooks;
        private BigDecimal totalCost;
        private BigDecimal totalRevenue;
        private BigDecimal totalProfit;
        private Integer totalItems;
        private Double optimizationScore;
        private List<String> constraintViolations;
        private Map<String, Object> metrics;

        public OptimizationResult() {
            this.selectedBooks = new ArrayList<>();
            this.constraintViolations = new ArrayList<>();
            this.metrics = new HashMap<>();
        }

        // Getters and setters
        public List<OptimalStockDto> getSelectedBooks() { return selectedBooks; }
        public void setSelectedBooks(List<OptimalStockDto> selectedBooks) { this.selectedBooks = selectedBooks; }

        public BigDecimal getTotalCost() { return totalCost; }
        public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

        public BigDecimal getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }

        public BigDecimal getTotalProfit() { return totalProfit; }
        public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }

        public Integer getTotalItems() { return totalItems; }
        public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

        public Double getOptimizationScore() { return optimizationScore; }
        public void setOptimizationScore(Double optimizationScore) { this.optimizationScore = optimizationScore; }

        public List<String> getConstraintViolations() { return constraintViolations; }
        public void setConstraintViolations(List<String> constraintViolations) { this.constraintViolations = constraintViolations; }

        public Map<String, Object> getMetrics() { return metrics; }
        public void setMetrics(Map<String, Object> metrics) { this.metrics = metrics; }
    }

    /**
     * Optimize book selection using multi-objective optimization
     * 多目的最適化による書籍選択最適化
     */
    public OptimizationResult optimizeBookSelection(List<OptimalStockDto> candidateBooks, 
                                                   OptimizationConstraints constraints) {
        logger.info("Starting constraint optimization for {} candidate books", candidateBooks.size());

        OptimizationResult result = new OptimizationResult();

        // Filter books that meet minimum criteria
        List<OptimalStockDto> viableBooks = candidateBooks.stream()
            .filter(this::isViableForOrdering)
            .collect(Collectors.toList());

        logger.info("Filtered to {} viable books for optimization", viableBooks.size());

        // Apply multi-objective optimization
        switch (constraints.getPriorityFocus()) {
            case "PROFIT":
                result = optimizeForProfit(viableBooks, constraints);
                break;
            case "CASH_FLOW":
                result = optimizeForCashFlow(viableBooks, constraints);
                break;
            case "RISK_MINIMIZATION":
                result = optimizeForRiskMinimization(viableBooks, constraints);
                break;
            default:
                result = optimizeForProfit(viableBooks, constraints);
                break;
        }

        // Calculate optimization score
        result.setOptimizationScore(calculateOptimizationScore(result, constraints));

        // Validate constraints
        validateConstraints(result, constraints);

        logger.info("Optimization completed. Selected {} books with total cost {}", 
                   result.getSelectedBooks().size(), result.getTotalCost());

        return result;
    }

    /**
     * Optimize for maximum profit
     */
    private OptimizationResult optimizeForProfit(List<OptimalStockDto> books, 
                                               OptimizationConstraints constraints) {
        // Sort by profit potential (profit per unit cost)
        List<OptimalStockDto> sortedBooks = books.stream()
            .sorted((a, b) -> {
                double profitRatioA = calculateProfitRatio(a);
                double profitRatioB = calculateProfitRatio(b);
                return Double.compare(profitRatioB, profitRatioA); // Descending
            })
            .collect(Collectors.toList());

        return selectBooksWithConstraints(sortedBooks, constraints);
    }

    /**
     * Optimize for cash flow efficiency
     */
    private OptimizationResult optimizeForCashFlow(List<OptimalStockDto> books, 
                                                  OptimizationConstraints constraints) {
        // Sort by cash flow efficiency (revenue / cost ratio with urgency factor)
        List<OptimalStockDto> sortedBooks = books.stream()
            .sorted((a, b) -> {
                double cashFlowScoreA = calculateCashFlowScore(a);
                double cashFlowScoreB = calculateCashFlowScore(b);
                return Double.compare(cashFlowScoreB, cashFlowScoreA); // Descending
            })
            .collect(Collectors.toList());

        return selectBooksWithConstraints(sortedBooks, constraints);
    }

    /**
     * Optimize for risk minimization
     */
    private OptimizationResult optimizeForRiskMinimization(List<OptimalStockDto> books, 
                                                          OptimizationConstraints constraints) {
        // Sort by risk score (lower risk first)
        List<OptimalStockDto> sortedBooks = books.stream()
            .sorted((a, b) -> {
                double riskScoreA = calculateRiskScore(a);
                double riskScoreB = calculateRiskScore(b);
                return Double.compare(riskScoreA, riskScoreB); // Ascending (lower risk first)
            })
            .collect(Collectors.toList());

        return selectBooksWithConstraints(sortedBooks, constraints);
    }

    /**
     * Select books while respecting all constraints
     */
    private OptimizationResult selectBooksWithConstraints(List<OptimalStockDto> sortedBooks, 
                                                         OptimizationConstraints constraints) {
        OptimizationResult result = new OptimizationResult();
        List<OptimalStockDto> selectedBooks = new ArrayList<>();
        
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalItems = 0;
        int totalWeight = 0;

        for (OptimalStockDto book : sortedBooks) {
            // Check constraints
            BigDecimal bookCost = book.getEstimatedCost() != null ? book.getEstimatedCost() : BigDecimal.ZERO;
            BigDecimal bookRevenue = book.getEstimatedRevenue() != null ? book.getEstimatedRevenue() : BigDecimal.ZERO;
            int bookQuantity = book.getRecommendedOrderQuantity() != null ? book.getRecommendedOrderQuantity() : 1;
            int bookWeight = estimateBookWeight(book) * bookQuantity;

            // Check if adding this book violates constraints
            if (totalCost.add(bookCost).compareTo(constraints.getMaxBudget()) <= 0 &&
                totalItems + bookQuantity <= constraints.getMaxItems() &&
                totalWeight + bookWeight <= constraints.getMaxWeight()) {
                
                selectedBooks.add(book);
                totalCost = totalCost.add(bookCost);
                totalRevenue = totalRevenue.add(bookRevenue);
                totalItems += bookQuantity;
                totalWeight += bookWeight;
            }
        }

        result.setSelectedBooks(selectedBooks);
        result.setTotalCost(totalCost);
        result.setTotalRevenue(totalRevenue);
        result.setTotalProfit(totalRevenue.subtract(totalCost));
        result.setTotalItems(totalItems);

        // Add metrics
        result.getMetrics().put("totalWeight", totalWeight);
        result.getMetrics().put("utilizationRate", (double) selectedBooks.size() / sortedBooks.size());
        result.getMetrics().put("budgetUtilization", totalCost.divide(constraints.getMaxBudget(), 4, RoundingMode.HALF_UP));

        return result;
    }

    /**
     * Check if a book is viable for ordering
     */
    private boolean isViableForOrdering(OptimalStockDto book) {
        // Must have a reorder recommendation or be understocked
        return "REORDER_NEEDED".equals(book.getStockStatus()) || 
               "UNDERSTOCK".equals(book.getStockStatus()) ||
               (book.getRecommendedOrderQuantity() != null && book.getRecommendedOrderQuantity() > 0);
    }

    /**
     * Calculate profit ratio (profit per unit cost)
     */
    private double calculateProfitRatio(OptimalStockDto book) {
        if (book.getEstimatedCost() == null || book.getEstimatedRevenue() == null ||
            book.getEstimatedCost().compareTo(BigDecimal.ZERO) <= 0) {
            return 0.0;
        }
        
        BigDecimal profit = book.getEstimatedRevenue().subtract(book.getEstimatedCost());
        return profit.divide(book.getEstimatedCost(), 4, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Calculate cash flow score
     */
    private double calculateCashFlowScore(OptimalStockDto book) {
        double profitRatio = calculateProfitRatio(book);
        
        // Add urgency multiplier
        double urgencyMultiplier = 1.0;
        if ("REORDER_NEEDED".equals(book.getStockStatus())) {
            urgencyMultiplier = 2.0;
        } else if ("UNDERSTOCK".equals(book.getStockStatus())) {
            urgencyMultiplier = 1.5;
        }
        
        return profitRatio * urgencyMultiplier;
    }

    /**
     * Calculate risk score (lower is better)
     */
    private double calculateRiskScore(OptimalStockDto book) {
        double riskScore = 1.0;
        
        // Obsolescence risk
        if (book.getObsolescenceFactor() != null) {
            riskScore += (1.0 - book.getObsolescenceFactor().doubleValue());
        }
        
        // Seasonal risk (if it's off-season)
        if (book.getSeasonalityFactor() != null && 
            book.getSeasonalityFactor().compareTo(BigDecimal.valueOf(0.9)) < 0) {
            riskScore += 0.5;
        }
        
        // Stock level risk
        if ("REORDER_NEEDED".equals(book.getStockStatus())) {
            riskScore += 0.3; // Slight risk increase for critical stock
        }
        
        return riskScore;
    }

    /**
     * Estimate book weight in kg (simplified)
     */
    private int estimateBookWeight(OptimalStockDto book) {
        // Simplified weight estimation - in practice would use actual book data
        return 1; // Assume 1kg per book
    }

    /**
     * Calculate optimization score
     */
    private double calculateOptimizationScore(OptimizationResult result, OptimizationConstraints constraints) {
        if (result.getTotalRevenue().compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        
        // Base score: profit margin
        double profitMargin = result.getTotalProfit().divide(result.getTotalRevenue(), 4, RoundingMode.HALF_UP).doubleValue();
        double score = profitMargin * 100;
        
        // Bonus for constraint utilization
        double budgetUtilization = result.getTotalCost().divide(constraints.getMaxBudget(), 4, RoundingMode.HALF_UP).doubleValue();
        score += budgetUtilization * 20; // Up to 20 points for efficient budget use
        
        // Penalty for constraint violations
        score -= result.getConstraintViolations().size() * 10;
        
        return Math.max(0, Math.min(100, score));
    }

    /**
     * Validate constraints and add violations to result
     */
    private void validateConstraints(OptimizationResult result, OptimizationConstraints constraints) {
        List<String> violations = new ArrayList<>();
        
        if (result.getTotalCost().compareTo(constraints.getMaxBudget()) > 0) {
            violations.add("Budget constraint exceeded");
        }
        
        if (result.getTotalItems() > constraints.getMaxItems()) {
            violations.add("Item count constraint exceeded");
        }
        
        if (result.getTotalRevenue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal profitMargin = result.getTotalProfit().divide(result.getTotalRevenue(), 4, RoundingMode.HALF_UP);
            if (profitMargin.compareTo(constraints.getMinProfitMargin()) < 0) {
                violations.add("Minimum profit margin constraint not met");
            }
        }
        
        result.setConstraintViolations(violations);
    }

    /**
     * Create default optimization constraints
     */
    public OptimizationConstraints createDefaultConstraints() {
        return new OptimizationConstraints();
    }

    /**
     * Analyze constraint sensitivity
     */
    public Map<String, Object> analyzeConstraintSensitivity(List<OptimalStockDto> books, 
                                                           OptimizationConstraints baseConstraints) {
        Map<String, Object> analysis = new HashMap<>();
        
        // Test budget sensitivity
        OptimizationConstraints budgetTest = new OptimizationConstraints();
        budgetTest.setMaxBudget(baseConstraints.getMaxBudget().multiply(BigDecimal.valueOf(1.2)));
        budgetTest.setMaxItems(baseConstraints.getMaxItems());
        budgetTest.setPriorityFocus(baseConstraints.getPriorityFocus());
        
        OptimizationResult budgetResult = optimizeBookSelection(books, budgetTest);
        Map<String, Object> budgetSensitivity = new HashMap<>();
        Map<String, Object> twentyPercentIncrease = new HashMap<>();
        twentyPercentIncrease.put("additionalProfit", budgetResult.getTotalProfit().subtract(
            optimizeBookSelection(books, baseConstraints).getTotalProfit()
        ));
        twentyPercentIncrease.put("additionalItems", budgetResult.getTotalItems() - 
            optimizeBookSelection(books, baseConstraints).getTotalItems());
        budgetSensitivity.put("20PercentIncrease", twentyPercentIncrease);
        analysis.put("budgetSensitivity", budgetSensitivity);
        
        return analysis;
    }
}