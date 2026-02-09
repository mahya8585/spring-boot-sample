package com.techbookstore.app.service;

import com.techbookstore.app.dto.InventoryDto;
import com.techbookstore.app.dto.InventoryReservationDto;
import com.techbookstore.app.dto.InventoryTransactionDto;
import com.techbookstore.app.entity.*;
import com.techbookstore.app.exception.InventoryNotFoundException;
import com.techbookstore.app.exception.InsufficientInventoryException;
import com.techbookstore.app.repository.InventoryRepository;
import com.techbookstore.app.repository.InventoryReservationRepository;
import com.techbookstore.app.repository.InventoryTransactionRepository;
import com.techbookstore.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Advanced inventory service for enhanced inventory management operations
 */
@Service
@Transactional
public class AdvancedInventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryTransactionRepository transactionRepository;

    @Autowired
    private InventoryReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Process barcode scan operation
     */
    public InventoryDto processBarcodeScanned(String barcode, String operation) {
        // For now, assume barcode is ISBN and find inventory by book
        // In real implementation, you'd have a barcode-to-book mapping
        throw new UnsupportedOperationException("Barcode scanning not yet implemented");
    }

    /**
     * Transfer stock between store and warehouse
     */
    public TransferResult transferStock(StockTransferRequest request) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(request.getInventoryId());
        if (inventoryOpt.isEmpty()) {
            throw new InventoryNotFoundException(request.getInventoryId());
        }

        Inventory inventory = inventoryOpt.get();
        User executedBy = getCurrentUser();

        // Validate transfer
        if ("STORE_TO_WAREHOUSE".equals(request.getTransferType())) {
            if (inventory.getStoreStock() < request.getQuantity()) {
                throw new InsufficientInventoryException("Insufficient store stock for transfer");
            }
        } else if ("WAREHOUSE_TO_STORE".equals(request.getTransferType())) {
            if (inventory.getWarehouseStock() < request.getQuantity()) {
                throw new InsufficientInventoryException("Insufficient warehouse stock for transfer");
            }
        }

        // Record transaction before changes
        int beforeTotal = inventory.getTotalStock();
        
        // Execute transfer
        if ("STORE_TO_WAREHOUSE".equals(request.getTransferType())) {
            inventory.setStoreStock(inventory.getStoreStock() - request.getQuantity());
            inventory.setWarehouseStock(inventory.getWarehouseStock() + request.getQuantity());
        } else {
            inventory.setWarehouseStock(inventory.getWarehouseStock() - request.getQuantity());
            inventory.setStoreStock(inventory.getStoreStock() + request.getQuantity());
        }

        Inventory savedInventory = inventoryRepository.save(inventory);

        // Create transaction record
        InventoryTransaction transaction = new InventoryTransaction(
            inventory, TransactionType.TRANSFER, request.getQuantity(),
            beforeTotal, inventory.getTotalStock(), executedBy
        );
        transaction.setReason(request.getReason());
        transaction.setStatus(TransactionStatus.APPROVED); // Auto-approve for now
        transactionRepository.save(transaction);

        return new TransferResult(true, "Transfer completed successfully", new InventoryDto(savedInventory));
    }

    /**
     * Reserve stock for orders
     */
    public ReservationResult reserveStock(StockReservationRequest request) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findById(request.getInventoryId());
        if (inventoryOpt.isEmpty()) {
            throw new InventoryNotFoundException(request.getInventoryId());
        }

        Inventory inventory = inventoryOpt.get();
        
        if (inventory.getAvailableStock() < request.getQuantity()) {
            throw new InsufficientInventoryException("Insufficient available stock for reservation");
        }

        // Create reservation
        InventoryReservation reservation = new InventoryReservation(
            inventory, request.getQuantity(), request.getReservationType()
        );
        reservation.setOrderId(request.getOrderId());
        reservation.setCustomerId(request.getCustomerId());
        reservation.setReservedUntil(request.getReservedUntil());

        InventoryReservation savedReservation = reservationRepository.save(reservation);

        // Update inventory reserved count
        inventory.setReservedCount(inventory.getReservedCount() + request.getQuantity());
        inventoryRepository.save(inventory);

        // Create transaction record
        User executedBy = getCurrentUser();
        InventoryTransaction transaction = new InventoryTransaction(
            inventory, TransactionType.RESERVE, request.getQuantity(),
            inventory.getAvailableStock() + request.getQuantity(), inventory.getAvailableStock(), executedBy
        );
        transaction.setReason("Stock reservation for order: " + request.getOrderId());
        transaction.setStatus(TransactionStatus.APPROVED);
        transactionRepository.save(transaction);

        return new ReservationResult(true, "Reservation created successfully", new InventoryReservationDto(savedReservation));
    }

    /**
     * Release stock reservation
     */
    public void releaseReservation(Long reservationId) {
        Optional<InventoryReservation> reservationOpt = reservationRepository.findById(reservationId);
        if (reservationOpt.isEmpty()) {
            throw new RuntimeException("Reservation not found: " + reservationId);
        }

        InventoryReservation reservation = reservationOpt.get();
        if (!"ACTIVE".equals(reservation.getStatus())) {
            throw new RuntimeException("Reservation is not active");
        }

        // Release reservation
        reservation.release();
        reservationRepository.save(reservation);

        // Update inventory reserved count
        Inventory inventory = reservation.getInventory();
        inventory.setReservedCount(inventory.getReservedCount() - reservation.getReservedQuantity());
        inventoryRepository.save(inventory);
    }

    /**
     * Get transaction history for inventory item
     */
    public List<InventoryTransactionDto> getTransactionHistory(Long inventoryId) {
        List<InventoryTransaction> transactions = transactionRepository.findByInventoryIdOrderByExecutedAtDesc(inventoryId);
        return transactions.stream()
                .map(InventoryTransactionDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get active reservations for inventory item
     */
    public List<InventoryReservationDto> getActiveReservations(Long inventoryId) {
        List<InventoryReservation> reservations = reservationRepository.findByInventoryIdAndStatus(inventoryId, "ACTIVE");
        return reservations.stream()
                .map(InventoryReservationDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get current user (temporary implementation)
     */
    private User getCurrentUser() {
        // For now, return a default system user
        // In real implementation, this would get user from security context
        return userRepository.findByUsername("system")
                .orElseGet(() -> {
                    User systemUser = new User("system", "System User", "system@techbookstore.com", "SYSTEM");
                    return userRepository.save(systemUser);
                });
    }

    // Request/Response classes
    public static class StockTransferRequest {
        private Long inventoryId;
        private String transferType; // STORE_TO_WAREHOUSE, WAREHOUSE_TO_STORE
        private Integer quantity;
        private String reason;

        // Getters and setters
        public Long getInventoryId() { return inventoryId; }
        public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }
        public String getTransferType() { return transferType; }
        public void setTransferType(String transferType) { this.transferType = transferType; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class StockReservationRequest {
        private Long inventoryId;
        private Long orderId;
        private Long customerId;
        private Integer quantity;
        private String reservationType;
        private LocalDateTime reservedUntil;

        // Getters and setters
        public Long getInventoryId() { return inventoryId; }
        public void setInventoryId(Long inventoryId) { this.inventoryId = inventoryId; }
        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getReservationType() { return reservationType; }
        public void setReservationType(String reservationType) { this.reservationType = reservationType; }
        public LocalDateTime getReservedUntil() { return reservedUntil; }
        public void setReservedUntil(LocalDateTime reservedUntil) { this.reservedUntil = reservedUntil; }
    }

    public static class TransferResult {
        private boolean success;
        private String message;
        private InventoryDto inventory;

        public TransferResult(boolean success, String message, InventoryDto inventory) {
            this.success = success;
            this.message = message;
            this.inventory = inventory;
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public InventoryDto getInventory() { return inventory; }
        public void setInventory(InventoryDto inventory) { this.inventory = inventory; }
    }

    public static class ReservationResult {
        private boolean success;
        private String message;
        private InventoryReservationDto reservation;

        public ReservationResult(boolean success, String message, InventoryReservationDto reservation) {
            this.success = success;
            this.message = message;
            this.reservation = reservation;
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public InventoryReservationDto getReservation() { return reservation; }
        public void setReservation(InventoryReservationDto reservation) { this.reservation = reservation; }
    }
}