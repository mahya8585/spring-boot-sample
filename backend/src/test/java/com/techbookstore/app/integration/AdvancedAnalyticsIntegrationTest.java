package com.techbookstore.app.integration;

import com.techbookstore.app.dto.InventoryAnalysisDto;
import com.techbookstore.app.entity.ABCXYZAnalysis;
import com.techbookstore.app.entity.ObsolescenceAssessment;
import com.techbookstore.app.service.ABCXYZAnalysisService;
import com.techbookstore.app.service.SeasonalAnalysisService;
import com.techbookstore.app.service.SeasonalAnalysisService.SeasonalCategoryAnalysis;
import com.techbookstore.app.service.SeasonalAnalysisService.SeasonalInventoryRecommendation;
import com.techbookstore.app.service.TechObsolescenceAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for the advanced analytics services
 * 高度分析サービスの統合テスト
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class AdvancedAnalyticsIntegrationTest {

    @Autowired
    private ABCXYZAnalysisService abcxyzAnalysisService;

    @Autowired
    private TechObsolescenceAnalysisService obsolescenceAnalysisService;

    @Autowired
    private SeasonalAnalysisService seasonalAnalysisService;

    @Test
    public void testABCXYZAnalysisFullWorkflow() {
        System.out.println("=== Testing ABC/XYZ Analysis Workflow ===");
        
        LocalDate analysisDate = LocalDate.now();
        
        // Perform ABC/XYZ analysis
        List<ABCXYZAnalysis> results = abcxyzAnalysisService.performAnalysis(analysisDate);
        
        assertNotNull(results, "ABC/XYZ analysis results should not be null");
        System.out.println("✅ ABC/XYZ Analysis completed with " + results.size() + " results");
        
        // Validate analysis results
        for (ABCXYZAnalysis analysis : results) {
            assertNotNull(analysis.getAbcCategory(), "ABC category should be assigned");
            assertNotNull(analysis.getXyzCategory(), "XYZ category should be assigned");
            assertTrue(analysis.getSalesContribution().doubleValue() >= 0, "Sales contribution should be non-negative");
            assertTrue(analysis.getDemandVariability().doubleValue() >= 0, "Demand variability should be non-negative");
            
            System.out.println("  - Book: " + analysis.getBook().getTitle() + 
                             " | ABC: " + analysis.getAbcCategory() + 
                             " | XYZ: " + analysis.getXyzCategory() +
                             " | Sales: " + analysis.getSalesContribution() + "%" +
                             " | Variability: " + analysis.getDemandVariability());
        }
        
        // Test categorization retrieval
        Map<String, List<ABCXYZAnalysis>> categoryResults = abcxyzAnalysisService.getAnalysisByCategory(analysisDate);
        
        assertNotNull(categoryResults, "Category results should not be null");
        System.out.println("✅ Category breakdown available for " + categoryResults.size() + " combinations");
        
        // Validate all 9 ABC/XYZ combinations
        String[] abcCategories = {"A", "B", "C"};
        String[] xyzCategories = {"X", "Y", "Z"};
        
        for (String abc : abcCategories) {
            for (String xyz : xyzCategories) {
                String combination = abc + xyz;
                assertTrue(categoryResults.containsKey(combination), 
                          "Should contain combination: " + combination);
            }
        }
        
        System.out.println("✅ All 9 ABC/XYZ combinations validated");
    }

    @Test
    public void testObsolescenceAnalysisFullWorkflow() {
        System.out.println("\n=== Testing Tech Obsolescence Analysis Workflow ===");
        
        LocalDate assessmentDate = LocalDate.now();
        
        // Perform obsolescence analysis
        List<ObsolescenceAssessment> assessments = obsolescenceAnalysisService.performObsolescenceAnalysis(assessmentDate);
        
        assertNotNull(assessments, "Obsolescence assessments should not be null");
        System.out.println("✅ Obsolescence Analysis completed with " + assessments.size() + " assessments");
        
        // Validate assessment results
        for (ObsolescenceAssessment assessment : assessments) {
            assertNotNull(assessment.getRiskLevel(), "Risk level should be assigned");
            assertNotNull(assessment.getMonthsToObsolescence(), "Months to obsolescence should be calculated");
            assertTrue(assessment.getRiskScore().doubleValue() >= 0, "Risk score should be non-negative");
            assertTrue(assessment.getRiskScore().doubleValue() <= 100, "Risk score should not exceed 100");
            
            System.out.println("  - Book: " + assessment.getBook().getTitle() + 
                             " | Risk: " + assessment.getRiskLevel() + 
                             " | Score: " + assessment.getRiskScore() +
                             " | Months to obsolescence: " + assessment.getMonthsToObsolescence());
        }
        
        // Test risk level categorization
        Map<String, List<ObsolescenceAssessment>> riskAnalysis = 
            obsolescenceAnalysisService.getAnalysisByRiskLevel(assessmentDate);
        
        assertNotNull(riskAnalysis, "Risk analysis should not be null");
        System.out.println("✅ Risk level breakdown available for " + riskAnalysis.size() + " levels");
        
        // Validate risk level categories
        String[] riskLevels = {"HIGH", "MEDIUM", "LOW"};
        for (String riskLevel : riskLevels) {
            assertTrue(riskAnalysis.containsKey(riskLevel), 
                      "Should contain risk level: " + riskLevel);
        }
        
        System.out.println("✅ All risk levels validated");
        
        // Test tech lifecycle distribution
        Map<String, Long> lifecycleDistribution = 
            obsolescenceAnalysisService.getTechLifecycleDistribution(assessmentDate);
        
        assertNotNull(lifecycleDistribution, "Lifecycle distribution should not be null");
        System.out.println("✅ Tech lifecycle distribution calculated");
        
        for (Map.Entry<String, Long> entry : lifecycleDistribution.entrySet()) {
            System.out.println("  - " + entry.getKey() + ": " + entry.getValue() + " books");
        }
    }

    @Test
    public void testSeasonalAnalysisFullWorkflow() {
        System.out.println("\n=== Testing Seasonal Analysis Workflow ===");
        
        // Test seasonal inventory trend generation
        InventoryAnalysisDto.SeasonalInventoryTrend seasonalTrend = seasonalAnalysisService.generateSeasonalInventoryTrend();
        
        assertNotNull(seasonalTrend, "Seasonal trend should not be null");
        assertNotNull(seasonalTrend.getSeason(), "Current season should be identified");
        assertNotNull(seasonalTrend.getExpectedDemandIncrease(), "Expected demand increase should be calculated");
        
        System.out.println("✅ Seasonal trend analysis completed:");
        System.out.println("  - Current Season: " + seasonalTrend.getSeason());
        System.out.println("  - Expected Demand Increase: " + seasonalTrend.getExpectedDemandIncrease() + "x");
        System.out.println("  - Top Categories: " + seasonalTrend.getTopCategories());
        
        // Test seasonal patterns for all categories
        Map<String, SeasonalCategoryAnalysis> seasonalPatterns = seasonalAnalysisService.analyzeSeasonalPatterns();
        
        assertNotNull(seasonalPatterns, "Seasonal patterns should not be null");
        assertFalse(seasonalPatterns.isEmpty(), "Should have seasonal patterns for categories");
        
        System.out.println("✅ Seasonal patterns analyzed for " + seasonalPatterns.size() + " categories");
        
        for (Map.Entry<String, SeasonalCategoryAnalysis> entry : seasonalPatterns.entrySet()) {
            System.out.println("  - Category: " + entry.getKey());
        }
        
        // Test seasonal inventory recommendations
        List<SeasonalInventoryRecommendation> recommendations = seasonalAnalysisService.generateSeasonalRecommendations();
        
        assertNotNull(recommendations, "Seasonal recommendations should not be null");
        assertFalse(recommendations.isEmpty(), "Should have recommendations");
        
        System.out.println("✅ Seasonal recommendations generated: " + recommendations.size());
    }

    @Test
    public void testIntegratedAnalyticsWorkflow() {
        System.out.println("\n=== Testing Integrated Analytics Workflow ===");
        
        LocalDate analysisDate = LocalDate.now();
        
        // Run all analytics in sequence
        System.out.println("Running comprehensive analytics suite...");
        
        // 1. ABC/XYZ Analysis
        List<ABCXYZAnalysis> abcxyzResults = abcxyzAnalysisService.performAnalysis(analysisDate);
        System.out.println("✅ ABC/XYZ Analysis: " + abcxyzResults.size() + " items classified");
        
        // 2. Obsolescence Analysis
        List<ObsolescenceAssessment> obsolescenceResults = 
            obsolescenceAnalysisService.performObsolescenceAnalysis(analysisDate);
        System.out.println("✅ Obsolescence Analysis: " + obsolescenceResults.size() + " items assessed");
        
        // 3. Seasonal Analysis
        InventoryAnalysisDto.SeasonalInventoryTrend seasonalTrend = seasonalAnalysisService.generateSeasonalInventoryTrend();
        List<SeasonalInventoryRecommendation> seasonalRecommendations = seasonalAnalysisService.generateSeasonalRecommendations();
        System.out.println("✅ Seasonal Analysis: " + seasonalRecommendations.size() + " recommendations");
        
        // Validate integration consistency
        assertEquals(abcxyzResults.size(), obsolescenceResults.size(), 
                    "ABC/XYZ and obsolescence analysis should cover same number of books");
        
        System.out.println("✅ All analytics completed successfully!");
        System.out.println("\n=== Analytics Integration Test PASSED ===");
        System.out.println("📊 ABC/XYZ Classification: FUNCTIONAL");
        System.out.println("⚠️  Obsolescence Risk Assessment: FUNCTIONAL");
        System.out.println("🌟 Seasonal Demand Forecasting: FUNCTIONAL");
        System.out.println("🔄 Integrated Analytics Pipeline: FUNCTIONAL");
    }
}