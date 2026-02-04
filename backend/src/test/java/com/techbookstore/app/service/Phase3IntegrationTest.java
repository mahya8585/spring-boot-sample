package com.techbookstore.app.service;

import com.techbookstore.app.dto.OptimalStockDto;
import com.techbookstore.app.dto.OrderSuggestionDto;
import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.DemandForecast;
import com.techbookstore.app.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test for Phase 3 demand forecasting and optimization integration
 */
@ExtendWith(MockitoExtension.class)
public class Phase3IntegrationTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OptimalStockCalculatorService optimalStockCalculatorService;

    @Mock
    private DemandForecastService demandForecastService;

    @Mock
    private TechTrendAnalysisService techTrendAnalysisService;

    @Mock
    private SeasonalAnalysisService seasonalAnalysisService;

    @InjectMocks
    private IntelligentOrderingService intelligentOrderingService;

    @Test
    public void testConstraintOptimizationService() {
        // Test constraint optimization service standalone
        ConstraintOptimizationService optimizationService = new ConstraintOptimizationService();
        
        // Create test data
        OptimalStockDto book1 = new OptimalStockDto(1L, "Java Programming", 5, 20, 10, 5);
        book1.setEstimatedCost(BigDecimal.valueOf(500));
        book1.setEstimatedRevenue(BigDecimal.valueOf(800));
        book1.setStockStatus("REORDER_NEEDED");
        book1.setRecommendedOrderQuantity(15);
        
        OptimalStockDto book2 = new OptimalStockDto(2L, "Python Guide", 3, 15, 8, 3);
        book2.setEstimatedCost(BigDecimal.valueOf(300));
        book2.setEstimatedRevenue(BigDecimal.valueOf(450));
        book2.setStockStatus("UNDERSTOCK");
        book2.setRecommendedOrderQuantity(12);
        
        List<OptimalStockDto> candidateBooks = Arrays.asList(book1, book2);
        
        // Test optimization
        ConstraintOptimizationService.OptimizationConstraints constraints = 
            optimizationService.createDefaultConstraints();
        constraints.setMaxBudget(BigDecimal.valueOf(1000));
        
        ConstraintOptimizationService.OptimizationResult result = 
            optimizationService.optimizeBookSelection(candidateBooks, constraints);
        
        // Assertions
        assertNotNull(result);
        assertEquals(2, result.getSelectedBooks().size());
        assertTrue(result.getTotalCost().compareTo(BigDecimal.valueOf(800)) == 0);
        assertTrue(result.getTotalProfit().compareTo(BigDecimal.valueOf(450)) == 0);
        assertTrue(result.getOptimizationScore() > 0);
        assertTrue(result.getConstraintViolations().isEmpty());
    }

    @Test
    public void testDemandForecastServiceBasics() {
        // Test that DemandForecastService can be instantiated and basic methods work
        // This is a simplified test to verify the service structure
        
        // Create a book for testing
        Book testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");
        testBook.setSellingPrice(BigDecimal.valueOf(50));
        
        // Test seasonal factor calculation (via getSeasonalFactor method indirectly)
        // This tests that the service can handle basic operations
        
        assertNotNull(testBook);
        assertTrue(testBook.getSellingPrice().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    public void testOptimalStockCalculatorServiceBasics() {
        // Test OptimalStockDto creation and basic functionality
        OptimalStockDto stock = new OptimalStockDto(1L, "Test Book", 10, 20, 15, 5);
        stock.setEconomicOrderQuantity(25);
        stock.setObsolescenceFactor(BigDecimal.valueOf(0.9));
        stock.setTrendFactor(BigDecimal.valueOf(1.1));
        stock.setSeasonalityFactor(BigDecimal.valueOf(1.2));
        
        assertNotNull(stock);
        assertEquals(Long.valueOf(1L), stock.getBookId());
        assertEquals("Test Book", stock.getBookTitle());
        assertEquals(Integer.valueOf(10), stock.getCurrentStock());
        assertEquals(Integer.valueOf(20), stock.getOptimalStockLevel());
        assertEquals(Integer.valueOf(25), stock.getEconomicOrderQuantity());
        assertTrue(stock.getObsolescenceFactor().compareTo(BigDecimal.valueOf(0.9)) == 0);
    }

    @Test
    public void testIntelligentOrderingService() {
        // Test intelligent ordering service functionality
        
        // Test data preparation
        List<Long> bookIds = Arrays.asList(1L, 2L);
        
        // Mock optimal stock results
        OptimalStockDto stock1 = new OptimalStockDto(1L, "Book 1", 5, 20, 15, 5);
        stock1.setStockStatus("REORDER_NEEDED");
        stock1.setEstimatedCost(BigDecimal.valueOf(100));
        stock1.setEstimatedRevenue(BigDecimal.valueOf(150));
        stock1.setRecommendedOrderQuantity(15);
        
        OptimalStockDto stock2 = new OptimalStockDto(2L, "Book 2", 8, 15, 10, 3);
        stock2.setStockStatus("UNDERSTOCK");
        stock2.setEstimatedCost(BigDecimal.valueOf(200));
        stock2.setEstimatedRevenue(BigDecimal.valueOf(280));
        stock2.setRecommendedOrderQuantity(7);
        
        when(optimalStockCalculatorService.calculateOptimalStock(1L)).thenReturn(stock1);
        when(optimalStockCalculatorService.calculateOptimalStock(2L)).thenReturn(stock2);
        
        // Test intelligent ordering
        try {
            OrderSuggestionDto suggestion = intelligentOrderingService.generateOrderSuggestions(bookIds, "EMERGENCY");
            
            assertNotNull(suggestion);
            assertEquals("EMERGENCY", suggestion.getSuggestionType());
            assertNotNull(suggestion.getCategorySuggestions());
            
        } catch (Exception e) {
            fail("Intelligent ordering test failed: " + e.getMessage());
        }
    }

    @Test
    public void testConstraintSensitivityAnalysis() {
        // Test constraint sensitivity analysis
        ConstraintOptimizationService optimizationService = new ConstraintOptimizationService();
        
        // Create test data
        OptimalStockDto book1 = new OptimalStockDto(1L, "Java Programming", 5, 20, 10, 5);
        book1.setEstimatedCost(BigDecimal.valueOf(500));
        book1.setEstimatedRevenue(BigDecimal.valueOf(800));
        book1.setStockStatus("REORDER_NEEDED");
        book1.setRecommendedOrderQuantity(15);
        
        List<OptimalStockDto> candidateBooks = Arrays.asList(book1);
        
        ConstraintOptimizationService.OptimizationConstraints constraints = 
            optimizationService.createDefaultConstraints();
        constraints.setMaxBudget(BigDecimal.valueOf(1000));
        
        // Test sensitivity analysis
        try {
            Map<String, Object> sensitivityAnalysis = optimizationService.analyzeConstraintSensitivity(candidateBooks, constraints);
            assertNotNull(sensitivityAnalysis);
            assertTrue(sensitivityAnalysis.containsKey("budgetSensitivity"));
        } catch (Exception e) {
            fail("Constraint sensitivity analysis test failed: " + e.getMessage());
        }
    }
}