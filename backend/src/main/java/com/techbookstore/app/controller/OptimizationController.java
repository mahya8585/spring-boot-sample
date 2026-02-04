package com.techbookstore.app.controller;

import com.techbookstore.app.dto.OptimalStockDto;
import com.techbookstore.app.dto.OrderSuggestionDto;
import com.techbookstore.app.entity.OptimalStockSettings;
import com.techbookstore.app.service.ConstraintOptimizationService;
import com.techbookstore.app.service.IntelligentOrderingService;
import com.techbookstore.app.service.OptimalStockCalculatorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for inventory optimization functionality
 * 在庫最適化機能のRESTコントローラー
 */
@RestController
@RequestMapping("/api/v1/optimization")
public class OptimizationController {

    private final OptimalStockCalculatorService optimalStockCalculatorService;
    private final IntelligentOrderingService intelligentOrderingService;
    private final ConstraintOptimizationService constraintOptimizationService;

    public OptimizationController(OptimalStockCalculatorService optimalStockCalculatorService,
                                 IntelligentOrderingService intelligentOrderingService,
                                 ConstraintOptimizationService constraintOptimizationService) {
        this.optimalStockCalculatorService = optimalStockCalculatorService;
        this.intelligentOrderingService = intelligentOrderingService;
        this.constraintOptimizationService = constraintOptimizationService;
    }

    /**
     * Get optimal stock level for a specific book
     * GET /api/v1/optimization/optimal-stock/{bookId}
     */
    @GetMapping("/optimal-stock/{bookId}")
    public ResponseEntity<OptimalStockDto> getOptimalStock(@PathVariable Long bookId) {
        OptimalStockDto optimalStock = optimalStockCalculatorService.calculateOptimalStock(bookId);
        return ResponseEntity.ok(optimalStock);
    }

    /**
     * Save optimal stock settings
     * POST /api/v1/optimization/optimal-stock
     */
    @PostMapping("/optimal-stock")
    public ResponseEntity<OptimalStockSettings> saveOptimalStockSettings(@RequestBody OptimalStockDto dto) {
        OptimalStockSettings settings = optimalStockCalculatorService.saveOptimalStockSettings(dto);
        return ResponseEntity.ok(settings);
    }

    /**
     * Get intelligent order suggestions
     * POST /api/v1/optimization/order-suggestions
     */
    @PostMapping("/order-suggestions")
    public ResponseEntity<OrderSuggestionDto> getIntelligentOrderSuggestions(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Long> bookIds = (List<Long>) request.get("bookIds");
        String orderType = (String) request.getOrDefault("orderType", "OPTIMIZED");
        
        OrderSuggestionDto suggestion = intelligentOrderingService.generateOrderSuggestions(bookIds, orderType);
        return ResponseEntity.ok(suggestion);
    }

    /**
     * Get simple order suggestions (legacy endpoint)
     * POST /api/v1/optimization/order-suggestions-simple
     */
    @PostMapping("/order-suggestions-simple")
    public ResponseEntity<List<OptimalStockDto>> getOrderSuggestions(@RequestBody List<Long> bookIds) {
        List<OptimalStockDto> suggestions = bookIds.stream()
            .map(optimalStockCalculatorService::calculateOptimalStock)
            .filter(stock -> "REORDER_NEEDED".equals(stock.getStockStatus()) || "UNDERSTOCK".equals(stock.getStockStatus()))
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get books that need reordering
     * GET /api/v1/optimization/reorder-needed
     */
    @GetMapping("/reorder-needed")
    public ResponseEntity<List<OptimalStockDto>> getBooksNeedingReorder() {
        List<OptimalStockDto> booksNeedingReorder = optimalStockCalculatorService.getBooksNeedingReorder();
        return ResponseEntity.ok(booksNeedingReorder);
    }

    /**
     * Get constraint analysis summary
     * GET /api/v1/optimization/constraint-analysis
     */
    @GetMapping("/constraint-analysis")
    public ResponseEntity<Map<String, Object>> getConstraintAnalysis() {
        List<OptimalStockDto> allBooksNeedingReorder = optimalStockCalculatorService.getBooksNeedingReorder();
        
        // Calculate summary statistics
        int totalBooksNeedingReorder = allBooksNeedingReorder.size();
        double totalEstimatedCost = allBooksNeedingReorder.stream()
            .filter(book -> book.getEstimatedCost() != null)
            .mapToDouble(book -> book.getEstimatedCost().doubleValue())
            .sum();
        
        double totalEstimatedRevenue = allBooksNeedingReorder.stream()
            .filter(book -> book.getEstimatedRevenue() != null)
            .mapToDouble(book -> book.getEstimatedRevenue().doubleValue())
            .sum();
        
        // Count by status
        long reorderNeeded = allBooksNeedingReorder.stream()
            .filter(book -> "REORDER_NEEDED".equals(book.getStockStatus()))
            .count();
        
        long understock = allBooksNeedingReorder.stream()
            .filter(book -> "UNDERSTOCK".equals(book.getStockStatus()))
            .count();
        
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalBooksNeedingReorder", totalBooksNeedingReorder);
        analysis.put("totalEstimatedCost", totalEstimatedCost);
        analysis.put("totalEstimatedRevenue", totalEstimatedRevenue);
        analysis.put("estimatedProfit", totalEstimatedRevenue - totalEstimatedCost);
        
        Map<String, Long> statusBreakdown = new HashMap<>();
        statusBreakdown.put("reorderNeeded", reorderNeeded);
        statusBreakdown.put("understock", understock);
        analysis.put("statusBreakdown", statusBreakdown);
        
        analysis.put("recommendations", generateRecommendations(allBooksNeedingReorder));
        
        return ResponseEntity.ok(analysis);
    }

    /**
     * Advanced constraint optimization
     * POST /api/v1/optimization/constraint-optimize
     */
    @PostMapping("/constraint-optimize")
    public ResponseEntity<ConstraintOptimizationService.OptimizationResult> optimizeWithConstraints(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Long> bookIds = (List<Long>) request.get("bookIds");
        
        // Get stock analysis for books
        List<OptimalStockDto> candidateBooks = bookIds.stream()
            .map(optimalStockCalculatorService::calculateOptimalStock)
            .collect(java.util.stream.Collectors.toList());
        
        // Create constraints
        ConstraintOptimizationService.OptimizationConstraints constraints = 
            constraintOptimizationService.createDefaultConstraints();
        
        // Apply any custom constraints from request
        if (request.containsKey("maxBudget")) {
            constraints.setMaxBudget(java.math.BigDecimal.valueOf(((Number) request.get("maxBudget")).doubleValue()));
        }
        if (request.containsKey("maxItems")) {
            constraints.setMaxItems(((Number) request.get("maxItems")).intValue());
        }
        if (request.containsKey("priorityFocus")) {
            constraints.setPriorityFocus((String) request.get("priorityFocus"));
        }
        
        ConstraintOptimizationService.OptimizationResult result = 
            constraintOptimizationService.optimizeBookSelection(candidateBooks, constraints);
        
        return ResponseEntity.ok(result);
    }

    /**
     * Bulk calculate optimal stock for multiple books
     * POST /api/v1/optimization/bulk-calculate
     */
    @PostMapping("/bulk-calculate")
    public ResponseEntity<List<OptimalStockDto>> bulkCalculateOptimalStock(@RequestBody List<Long> bookIds) {
        List<OptimalStockDto> results = bookIds.stream()
            .map(optimalStockCalculatorService::calculateOptimalStock)
            .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(results);
    }

    /**
     * Generate recommendations based on current inventory state
     */
    private List<String> generateRecommendations(List<OptimalStockDto> booksNeedingReorder) {
        List<String> recommendations = new java.util.ArrayList<>();
        
        if (booksNeedingReorder.isEmpty()) {
            recommendations.add("All books are optimally stocked");
            return recommendations;
        }
        
        // Priority recommendations
        long criticalBooks = booksNeedingReorder.stream()
            .filter(book -> "REORDER_NEEDED".equals(book.getStockStatus()))
            .count();
        
        if (criticalBooks > 0) {
            recommendations.add(String.format("Immediate action required: %d books need reordering", criticalBooks));
        }
        
        // Budget recommendations
        double totalCost = booksNeedingReorder.stream()
            .filter(book -> book.getEstimatedCost() != null)
            .mapToDouble(book -> book.getEstimatedCost().doubleValue())
            .sum();
        
        if (totalCost > 10000) {
            recommendations.add("Consider prioritizing high-value or fast-moving items due to high total cost");
        }
        
        // Seasonal recommendations
        java.time.LocalDate now = java.time.LocalDate.now();
        if (now.getMonthValue() >= 8 && now.getMonthValue() <= 10) {
            recommendations.add("Consider increasing orders due to back-to-school season");
        } else if (now.getMonthValue() == 12 || now.getMonthValue() == 1) {
            recommendations.add("Review year-end inventory levels and New Year learning trends");
        }
        
        return recommendations;
    }
}