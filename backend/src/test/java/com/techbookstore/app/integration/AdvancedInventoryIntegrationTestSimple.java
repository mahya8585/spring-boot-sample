package com.techbookstore.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techbookstore.app.service.AdvancedInventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("dev")
public class AdvancedInventoryIntegrationTestSimple {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetTransactionHistoryEndpoint() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        // Test the endpoint exists and returns valid JSON for non-existent inventory
        mockMvc.perform(get("/api/v1/inventory/999/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetActiveReservationsEndpoint() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        // Test the endpoint exists and returns valid JSON for non-existent inventory
        mockMvc.perform(get("/api/v1/inventory/999/reservations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testTransferEndpointExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        AdvancedInventoryService.StockTransferRequest request = new AdvancedInventoryService.StockTransferRequest();
        request.setInventoryId(999L);
        request.setTransferType("STORE_TO_WAREHOUSE");
        request.setQuantity(1);
        request.setReason("Test transfer");

        String requestJson = objectMapper.writeValueAsString(request);

        // Test the endpoint exists (will fail due to non-existent inventory, but endpoint should be accessible)
        mockMvc.perform(post("/api/v1/inventory/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest()); // Expected due to non-existent inventory
    }

    @Test
    void testReservationEndpointExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        AdvancedInventoryService.StockReservationRequest request = new AdvancedInventoryService.StockReservationRequest();
        request.setInventoryId(999L);
        request.setQuantity(1);
        request.setReservationType("MANUAL");

        String requestJson = objectMapper.writeValueAsString(request);

        // Test the endpoint exists (will fail due to non-existent inventory, but endpoint should be accessible)
        mockMvc.perform(post("/api/v1/inventory/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest()); // Expected due to non-existent inventory
    }

    @Test
    void testBarcodeEndpointExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        String barcodeRequest = "{\"barcode\":\"978-1234567890\",\"operation\":\"LOOKUP\"}";

        // Test the endpoint exists (will fail due to unimplemented functionality)
        mockMvc.perform(post("/api/v1/inventory/barcode-scan")
                .contentType(MediaType.APPLICATION_JSON)
                .content(barcodeRequest))
                .andExpect(status().isBadRequest()); // Expected since barcode processing is not fully implemented
    }
}