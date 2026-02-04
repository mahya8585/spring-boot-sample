package com.techbookstore.app.service;

import com.techbookstore.app.dto.InventoryAnalysisDto;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SeasonalAnalysisServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private OrderRepository orderRepository;

    private SeasonalAnalysisService seasonalService;

    @BeforeEach
    void setUp() {
        seasonalService = new SeasonalAnalysisService(bookRepository, orderRepository);
    }

    @Test
    void testGenerateSeasonalInventoryTrend() {
        // When
        InventoryAnalysisDto.SeasonalInventoryTrend trend = seasonalService.generateSeasonalInventoryTrend();
        
        // Then
        assertNotNull(trend);
        assertNotNull(trend.getSeason());
        assertNotNull(trend.getExpectedDemandIncrease());
        assertNotNull(trend.getTopCategories());
        assertNotNull(trend.getRecommendation());
        
        assertTrue(trend.getExpectedDemandIncrease().compareTo(BigDecimal.ONE) >= 0);
        assertFalse(trend.getTopCategories().isEmpty());
    }

    @Test
    void testAnalyzeCategorySeasonality() {
        // Given
        String categoryCode = "JAVA";
        
        // When
        SeasonalAnalysisService.SeasonalCategoryAnalysis analysis = 
            seasonalService.analyzeCategorySeasonality(categoryCode);
        
        // Then
        assertNotNull(analysis);
        assertEquals(categoryCode, analysis.getCategoryCode());
        assertNotNull(analysis.getSeasonalIndices());
        assertNotNull(analysis.getPeakSeason());
        assertNotNull(analysis.getLowSeason());
        assertNotNull(analysis.getVolatility());
        
        // Should have seasonal indices for all seasons
        Map<String, BigDecimal> indices = analysis.getSeasonalIndices();
        assertTrue(indices.containsKey("SPRING"));
        assertTrue(indices.containsKey("SUMMER"));
        assertTrue(indices.containsKey("FALL"));
        assertTrue(indices.containsKey("WINTER"));
        
        // Volatility should be non-negative
        assertTrue(analysis.getVolatility().compareTo(BigDecimal.ZERO) >= 0);
    }

    @Test
    void testAnalyzeSeasonalPatterns() {
        // When
        Map<String, SeasonalAnalysisService.SeasonalCategoryAnalysis> patterns = 
            seasonalService.analyzeSeasonalPatterns();
        
        // Then
        assertNotNull(patterns);
        assertFalse(patterns.isEmpty());
        
        // Should contain major tech categories
        assertTrue(patterns.containsKey("JAVA"));
        assertTrue(patterns.containsKey("PYTHON"));
        assertTrue(patterns.containsKey("JAVASCRIPT"));
        
        // Each analysis should be valid
        for (SeasonalAnalysisService.SeasonalCategoryAnalysis analysis : patterns.values()) {
            assertNotNull(analysis.getCategoryCode());
            assertNotNull(analysis.getSeasonalIndices());
            assertNotNull(analysis.getPeakSeason());
            assertNotNull(analysis.getLowSeason());
        }
    }

    @Test
    void testGenerateSeasonalRecommendations() {
        // When
        List<SeasonalAnalysisService.SeasonalInventoryRecommendation> recommendations = 
            seasonalService.generateSeasonalRecommendations();
        
        // Then
        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());
        
        // Check each recommendation
        for (SeasonalAnalysisService.SeasonalInventoryRecommendation rec : recommendations) {
            assertNotNull(rec.getCategoryCode());
            assertNotNull(rec.getCurrentSeason());
            assertNotNull(rec.getNextSeason());
            assertNotNull(rec.getRecommendedAction());
            assertNotNull(rec.getReason());
        }
    }

    @Test
    void testSeasonalIndicesAreValid() {
        // Test known categories
        String[] categories = {"JAVA", "PYTHON", "JAVASCRIPT", "DATABASE"};
        
        for (String category : categories) {
            SeasonalAnalysisService.SeasonalCategoryAnalysis analysis = 
                seasonalService.analyzeCategorySeasonality(category);
            
            Map<String, BigDecimal> indices = analysis.getSeasonalIndices();
            
            // All seasonal indices should be positive and reasonable (between 0.5 and 2.0)
            for (BigDecimal index : indices.values()) {
                assertTrue(index.compareTo(BigDecimal.valueOf(0.5)) >= 0, 
                          "Seasonal index too low: " + index);
                assertTrue(index.compareTo(BigDecimal.valueOf(2.0)) <= 0, 
                          "Seasonal index too high: " + index);
            }
        }
    }

    @Test
    void testUnknownCategoryHandling() {
        // Given
        String unknownCategory = "UNKNOWN_TECH";
        
        // When
        SeasonalAnalysisService.SeasonalCategoryAnalysis analysis = 
            seasonalService.analyzeCategorySeasonality(unknownCategory);
        
        // Then
        assertNotNull(analysis);
        assertEquals(unknownCategory, analysis.getCategoryCode());
        
        // Should have default seasonal pattern
        Map<String, BigDecimal> indices = analysis.getSeasonalIndices();
        assertEquals(4, indices.size()); // All four seasons
        assertNotNull(analysis.getPeakSeason());
        assertNotNull(analysis.getLowSeason());
    }
}