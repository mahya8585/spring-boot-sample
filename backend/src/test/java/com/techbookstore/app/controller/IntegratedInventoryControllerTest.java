package com.techbookstore.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbookstore.app.dto.IntegratedAnalysisRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for IntegratedInventoryController
 * Tests Phase 4 integrated analysis endpoints
 */
@SpringBootTest
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.profiles.active=test",
    "spring.cache.type=simple"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class IntegratedInventoryControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testComprehensiveAnalysis() throws Exception {
        
        IntegratedAnalysisRequest request = new IntegratedAnalysisRequest();
        request.setCategory("JAVA");
        request.setIncludeOptimization(true);
        request.setAsyncExecution(false);

        mockMvc.perform(post("/api/v1/inventory/integrated/comprehensive-analysis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.analysisId").exists())
                .andExpect(jsonPath("$.baseReport").exists())
                .andExpect(jsonPath("$.advancedAnalysis").exists())
                .andExpect(jsonPath("$.optimization").exists())
                .andExpect(jsonPath("$.performanceMetrics").exists());
    }

    @Test
    public void testRealtimeDashboard() throws Exception {
        
        IntegratedAnalysisRequest request = new IntegratedAnalysisRequest();
        request.setCategory("JAVASCRIPT");

        mockMvc.perform(post("/api/v1/inventory/integrated/realtime-dashboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.inventorySummary").exists())
                .andExpect(jsonPath("$.kpis").exists())
                .andExpect(jsonPath("$.trends").exists())
                .andExpect(jsonPath("$.systemHealth").exists())
                .andExpect(jsonPath("$.generated_at").exists());
    }

    @Test
    public void testBatchOptimization() throws Exception {
        
        IntegratedAnalysisRequest request = new IntegratedAnalysisRequest();
        request.setCategory("PYTHON");
        request.setForecastHorizon(30);

        mockMvc.perform(post("/api/v1/inventory/integrated/batch-optimization")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobId").exists())
                .andExpect(jsonPath("$.status").value("COMPLETED"))
                .andExpect(jsonPath("$.recommendations").exists())
                .andExpect(jsonPath("$.performanceMetrics").exists());
    }

    @Test
    public void testHealthCheck() throws Exception {
        
        mockMvc.perform(get("/api/v1/inventory/integrated/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("healthy"))
                .andExpect(jsonPath("$.services").exists())
                .andExpect(jsonPath("$.metrics").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testMetrics() throws Exception {
        
        mockMvc.perform(get("/api/v1/inventory/integrated/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response_time_avg").exists())
                .andExpect(jsonPath("$.cache_hit_rate").exists())
                .andExpect(jsonPath("$.concurrent_users").exists())
                .andExpect(jsonPath("$.daily_analyses").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    public void testDashboardWithEmptyRequest() throws Exception {
        
        mockMvc.perform(post("/api/v1/inventory/integrated/realtime-dashboard")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kpis").exists());
    }
}