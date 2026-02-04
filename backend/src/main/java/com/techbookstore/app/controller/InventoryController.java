package com.techbookstore.app.controller;

import com.techbookstore.app.dto.InventoryDto;
import com.techbookstore.app.dto.InventoryReservationDto;
import com.techbookstore.app.dto.InventoryTransactionDto;
import com.techbookstore.app.entity.Inventory;
import com.techbookstore.app.repository.InventoryRepository;
import com.techbookstore.app.service.AdvancedInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private AdvancedInventoryService advancedInventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryDto>> getAllInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();
        List<InventoryDto> inventoryDtos = inventories.stream()
                .map(InventoryDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(inventoryDtos);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<InventoryDto> getInventoryByBookId(@PathVariable Long bookId) {
        Optional<Inventory> inventory = inventoryRepository.findByBookId(bookId);
        if (inventory.isPresent()) {
            return ResponseEntity.ok(new InventoryDto(inventory.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/alerts")
    public ResponseEntity<List<InventoryDto>> getInventoryAlerts() {
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems();
        List<InventoryDto> inventoryDtos = lowStockItems.stream()
                .map(InventoryDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(inventoryDtos);
    }

    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryDto>> getOutOfStockItems() {
        List<Inventory> outOfStockItems = inventoryRepository.findOutOfStockItems();
        List<InventoryDto> inventoryDtos = outOfStockItems.stream()
                .map(InventoryDto::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(inventoryDtos);
    }

    @PostMapping("/receive")
    public ResponseEntity<InventoryDto> receiveStock(@RequestBody ReceiveStockRequest request) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBookId(request.getBookId());
        
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            
            if ("STORE".equals(request.getLocation())) {
                inventory.setStoreStock(inventory.getStoreStock() + request.getQuantity());
            } else {
                inventory.setWarehouseStock(inventory.getWarehouseStock() + request.getQuantity());
            }
            
            inventory.setLastReceivedDate(LocalDate.now());
            Inventory savedInventory = inventoryRepository.save(inventory);
            return ResponseEntity.ok(new InventoryDto(savedInventory));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<InventoryDto> sellStock(@RequestBody SellStockRequest request) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBookId(request.getBookId());
        
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            
            // Fixed: Check if enough stock is available AND store stock is sufficient
            if (inventory.getAvailableStock() >= request.getQuantity() &&
                inventory.getStoreStock() >= request.getQuantity()) {
                inventory.setStoreStock(inventory.getStoreStock() - request.getQuantity());
                inventory.setLastSoldDate(LocalDate.now());
                Inventory savedInventory = inventoryRepository.save(inventory);
                return ResponseEntity.ok(new InventoryDto(savedInventory));
            } else {
                return ResponseEntity.badRequest().build(); // Insufficient stock
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/adjust")
    public ResponseEntity<InventoryDto> adjustStock(@RequestBody AdjustStockRequest request) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByBookId(request.getBookId());
        
        if (inventoryOpt.isPresent()) {
            Inventory inventory = inventoryOpt.get();
            inventory.setStoreStock(request.getStoreStock());
            inventory.setWarehouseStock(request.getWarehouseStock());
            Inventory savedInventory = inventoryRepository.save(inventory);
            return ResponseEntity.ok(new InventoryDto(savedInventory));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Request DTOs
    public static class ReceiveStockRequest {
        private Long bookId;
        private Integer quantity;
        private String location; // STORE or WAREHOUSE

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
    }

    public static class SellStockRequest {
        private Long bookId;
        private Integer quantity;

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class AdjustStockRequest {
        private Long bookId;
        private Integer storeStock;
        private Integer warehouseStock;

        public Long getBookId() { return bookId; }
        public void setBookId(Long bookId) { this.bookId = bookId; }
        public Integer getStoreStock() { return storeStock; }
        public void setStoreStock(Integer storeStock) { this.storeStock = storeStock; }
        public Integer getWarehouseStock() { return warehouseStock; }
        public void setWarehouseStock(Integer warehouseStock) { this.warehouseStock = warehouseStock; }
    }

    // New endpoints for advanced inventory management

    @GetMapping("/{inventoryId}/transactions")
    public ResponseEntity<List<InventoryTransactionDto>> getTransactionHistory(@PathVariable Long inventoryId) {
        List<InventoryTransactionDto> transactions = advancedInventoryService.getTransactionHistory(inventoryId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{inventoryId}/reservations")
    public ResponseEntity<List<InventoryReservationDto>> getActiveReservations(@PathVariable Long inventoryId) {
        List<InventoryReservationDto> reservations = advancedInventoryService.getActiveReservations(inventoryId);
        return ResponseEntity.ok(reservations);
    }

    @PostMapping("/transfers")
    public ResponseEntity<AdvancedInventoryService.TransferResult> transferStock(
            @RequestBody AdvancedInventoryService.StockTransferRequest request) {
        try {
            AdvancedInventoryService.TransferResult result = advancedInventoryService.transferStock(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            AdvancedInventoryService.TransferResult errorResult = 
                new AdvancedInventoryService.TransferResult(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    @PostMapping("/reservations")
    public ResponseEntity<AdvancedInventoryService.ReservationResult> reserveStock(
            @RequestBody AdvancedInventoryService.StockReservationRequest request) {
        try {
            AdvancedInventoryService.ReservationResult result = advancedInventoryService.reserveStock(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            AdvancedInventoryService.ReservationResult errorResult = 
                new AdvancedInventoryService.ReservationResult(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(errorResult);
        }
    }

    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<String> releaseReservation(@PathVariable Long reservationId) {
        try {
            advancedInventoryService.releaseReservation(reservationId);
            return ResponseEntity.ok("Reservation released successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/barcode-scan")
    public ResponseEntity<InventoryDto> processBarcodeScanned(@RequestBody BarcodeScanRequest request) {
        try {
            InventoryDto result = advancedInventoryService.processBarcodeScanned(request.getBarcode(), request.getOperation());
            return ResponseEntity.ok(result);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Request DTOs for new endpoints
    public static class BarcodeScanRequest {
        private String barcode;
        private String operation;

        public String getBarcode() { return barcode; }
        public void setBarcode(String barcode) { this.barcode = barcode; }
        public String getOperation() { return operation; }
        public void setOperation(String operation) { this.operation = operation; }
    }
}