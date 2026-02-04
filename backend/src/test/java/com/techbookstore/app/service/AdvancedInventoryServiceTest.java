package com.techbookstore.app.service;

import com.techbookstore.app.dto.InventoryTransactionDto;
import com.techbookstore.app.entity.*;
import com.techbookstore.app.repository.InventoryRepository;
import com.techbookstore.app.repository.InventoryReservationRepository;
import com.techbookstore.app.repository.InventoryTransactionRepository;
import com.techbookstore.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdvancedInventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private InventoryTransactionRepository transactionRepository;

    @Mock
    private InventoryReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdvancedInventoryService advancedInventoryService;

    private Inventory testInventory;
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = new User("testuser", "Test User", "test@example.com", "STAFF");
        testUser.setId(1L);

        testBook = new Book();
        testBook.setId(1L);
        testBook.setTitle("Test Book");

        testInventory = new Inventory(testBook);
        testInventory.setId(1L);
        testInventory.setStoreStock(10);
        testInventory.setWarehouseStock(5);
        testInventory.setReservedCount(0);
    }

    @Test
    void transferStock_StoreToWarehouse_Success() {
        // Arrange
        AdvancedInventoryService.StockTransferRequest request = new AdvancedInventoryService.StockTransferRequest();
        request.setInventoryId(1L);
        request.setTransferType("STORE_TO_WAREHOUSE");
        request.setQuantity(3);
        request.setReason("Transfer test");

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));
        when(userRepository.findByUsername("system")).thenReturn(Optional.of(testUser));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);
        when(transactionRepository.save(any(InventoryTransaction.class))).thenReturn(new InventoryTransaction());

        // Act
        AdvancedInventoryService.TransferResult result = advancedInventoryService.transferStock(request);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Transfer completed successfully", result.getMessage());
        assertEquals(7, testInventory.getStoreStock()); // 10 - 3
        assertEquals(8, testInventory.getWarehouseStock()); // 5 + 3
        
        verify(inventoryRepository).save(testInventory);
        verify(transactionRepository).save(any(InventoryTransaction.class));
    }

    @Test
    void reserveStock_Success() {
        // Arrange
        AdvancedInventoryService.StockReservationRequest request = new AdvancedInventoryService.StockReservationRequest();
        request.setInventoryId(1L);
        request.setOrderId(100L);
        request.setQuantity(2);
        request.setReservationType("ORDER");

        InventoryReservation savedReservation = new InventoryReservation(testInventory, 2, "ORDER");
        savedReservation.setId(1L);
        savedReservation.setOrderId(100L);

        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(testInventory));
        when(userRepository.findByUsername("system")).thenReturn(Optional.of(testUser));
        when(reservationRepository.save(any(InventoryReservation.class))).thenReturn(savedReservation);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);
        when(transactionRepository.save(any(InventoryTransaction.class))).thenReturn(new InventoryTransaction());

        // Act
        AdvancedInventoryService.ReservationResult result = advancedInventoryService.reserveStock(request);

        // Assert
        assertTrue(result.isSuccess());
        assertEquals("Reservation created successfully", result.getMessage());
        assertEquals(2, testInventory.getReservedCount());
        
        verify(reservationRepository).save(any(InventoryReservation.class));
        verify(inventoryRepository).save(testInventory);
        verify(transactionRepository).save(any(InventoryTransaction.class));
    }

    @Test
    void getTransactionHistory_ReturnsTransactions() {
        // Arrange
        List<InventoryTransaction> transactions = new ArrayList<>();
        InventoryTransaction transaction = new InventoryTransaction(testInventory, TransactionType.RECEIVE, 5, 0, 5, testUser);
        transaction.setId(1L);
        transactions.add(transaction);

        when(transactionRepository.findByInventoryIdOrderByExecutedAtDesc(1L)).thenReturn(transactions);

        // Act
        List<InventoryTransactionDto> result = advancedInventoryService.getTransactionHistory(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals(TransactionType.RECEIVE, result.get(0).getType());
        assertEquals(5, result.get(0).getQuantity());
    }

    @Test
    void releaseReservation_Success() {
        // Arrange
        InventoryReservation reservation = new InventoryReservation(testInventory, 2, "ORDER");
        reservation.setId(1L);
        reservation.setStatus("ACTIVE");

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any(InventoryReservation.class))).thenReturn(reservation);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        testInventory.setReservedCount(2);

        // Act
        advancedInventoryService.releaseReservation(1L);

        // Assert
        assertEquals("RELEASED", reservation.getStatus());
        assertNotNull(reservation.getReleasedAt());
        assertEquals(0, testInventory.getReservedCount());
        
        verify(reservationRepository).save(reservation);
        verify(inventoryRepository).save(testInventory);
    }
}