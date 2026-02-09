package com.techbookstore.app.service;

import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.Inventory;
import com.techbookstore.app.entity.Order;
import com.techbookstore.app.entity.OrderItem;
import com.techbookstore.app.exception.BookNotFoundException;
import com.techbookstore.app.exception.InsufficientInventoryException;
import com.techbookstore.app.exception.InvalidOrderStatusException;
import com.techbookstore.app.exception.InventoryNotFoundException;
import com.techbookstore.app.exception.OrderNotFoundException;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.InventoryRepository;
import com.techbookstore.app.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing orders and order-related operations.
 */
@Service
@Transactional
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final InventoryRepository inventoryRepository;
    
    /**
     * Constructor injection for dependencies.
     */
    public OrderService(OrderRepository orderRepository, 
                       BookRepository bookRepository, 
                       InventoryRepository inventoryRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.inventoryRepository = inventoryRepository;
    }
    
    /**
     * Creates a new order with order items.
     * 
     * @param order the order to create
     * @return the created order
     * @throws BookNotFoundException if any book in the order is not found
     */
    public Order createOrder(Order order) {
        logger.info("Creating new order with {} items", order.getOrderItems().size());
        
        // Generate order number
        order.setOrderNumber(generateOrderNumber());
        
        // Calculate total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : order.getOrderItems()) {
            // Validate book exists
            Optional<Book> bookOpt = bookRepository.findById(item.getBook().getId());
            if (bookOpt.isEmpty()) {
                throw new BookNotFoundException(item.getBook().getId());
            }
            
            Book book = bookOpt.get();
            item.setBook(book);
            item.setUnitPrice(book.getSellingPrice());
            item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setOrder(order);
            
            totalAmount = totalAmount.add(item.getTotalPrice());
        }
        
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        logger.info("Created order {} with total amount {}", savedOrder.getOrderNumber(), totalAmount);
        return savedOrder;
    }
    
    /**
     * Confirms an order and reserves inventory.
     * 
     * @param orderId the ID of the order to confirm
     * @return the confirmed order
     * @throws OrderNotFoundException if the order is not found
     * @throws InvalidOrderStatusException if the order is not in PENDING status
     * @throws InventoryNotFoundException if inventory is not found for any book
     * @throws InsufficientInventoryException if there's insufficient stock for any book
     */
    public Order confirmOrder(Long orderId) {
        logger.info("Confirming order with ID: {}", orderId);
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }
        
        Order order = orderOpt.get();
        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new InvalidOrderStatusException(order.getStatus().toString(), "CONFIRMED");
        }
        
        // Check inventory and reserve stock
        for (OrderItem item : order.getOrderItems()) {
            Optional<Inventory> inventoryOpt = inventoryRepository.findByBookId(item.getBook().getId());
            if (inventoryOpt.isEmpty()) {
                throw new InventoryNotFoundException(item.getBook().getId());
            }
            
            Inventory inventory = inventoryOpt.get();
            if (inventory.getAvailableStock() < item.getQuantity()) {
                throw new InsufficientInventoryException(
                    item.getBook().getTitle(), 
                    item.getQuantity(), 
                    inventory.getAvailableStock()
                );
            }
            
            // Reserve stock (reduce store stock)
            inventory.setStoreStock(inventory.getStoreStock() - item.getQuantity());
            inventoryRepository.save(inventory);
        }
        
        order.setStatus(Order.OrderStatus.CONFIRMED);
        order.setConfirmedDate(LocalDateTime.now());
        Order confirmedOrder = orderRepository.save(order);
        
        logger.info("Confirmed order {}", order.getOrderNumber());
        return confirmedOrder;
    }
    
    /**
     * Updates the status of an order.
     * 
     * @param orderId the ID of the order to update
     * @param newStatus the new status to set
     * @return the updated order
     * @throws OrderNotFoundException if the order is not found
     * @throws InvalidOrderStatusException if the status transition is invalid
     */
    public Order updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        logger.info("Updating order {} status to {}", orderId, newStatus);
        
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) {
            throw new OrderNotFoundException(orderId);
        }
        
        Order order = orderOpt.get();
        
        // Validate status transition
        if (!isValidStatusTransition(order.getStatus(), newStatus)) {
            throw new InvalidOrderStatusException(order.getStatus().toString(), newStatus.toString());
        }
        
        order.setStatus(newStatus);
        
        // Set specific timestamps based on status
        switch (newStatus) {
            case CONFIRMED:
                order.setConfirmedDate(LocalDateTime.now());
                break;
            case SHIPPED:
                order.setShippedDate(LocalDateTime.now());
                break;
            case DELIVERED:
                order.setDeliveredDate(LocalDateTime.now());
                break;
        }
        
        Order updatedOrder = orderRepository.save(order);
        logger.info("Updated order {} status to {}", order.getOrderNumber(), newStatus);
        return updatedOrder;
    }
    
    /**
     * Retrieves orders with optional filters.
     * 
     * @param status filter by order status
     * @param type filter by order type
     * @param customerId filter by customer ID
     * @param startDate filter by start date
     * @param endDate filter by end date
     * @param pageable pagination information
     * @return page of filtered orders
     */
    public Page<Order> getOrdersWithFilters(Order.OrderStatus status,
                                          Order.OrderType type,
                                          Long customerId,
                                          LocalDateTime startDate,
                                          LocalDateTime endDate,
                                          Pageable pageable) {
        return orderRepository.findOrdersWithFilters(status, type, customerId, startDate, endDate, pageable);
    }
    
    /**
     * Searches orders by keyword.
     * 
     * @param keyword the search keyword
     * @param pageable pagination information
     * @return page of matching orders
     */
    public Page<Order> searchOrders(String keyword, Pageable pageable) {
        return orderRepository.findByKeyword(keyword, pageable);
    }
    
    /**
     * Retrieves an order by ID.
     * 
     * @param id the order ID
     * @return optional order
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    /**
     * Retrieves an order by order number.
     * 
     * @param orderNumber the order number
     * @return optional order
     */
    @Transactional(readOnly = true)
    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber);
    }
    
    /**
     * Retrieves all orders for a customer.
     * 
     * @param customerId the customer ID
     * @return list of orders
     */
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerId(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
    
    /**
     * Gets the count of orders by status.
     * 
     * @param status the order status
     * @return count of orders
     */
    @Transactional(readOnly = true)
    public Long getOrderCountByStatus(Order.OrderStatus status) {
        return orderRepository.countByStatus(status);
    }
    
    /**
     * Generates a unique order number with format ORD-YYYYMMDD-XXXX.
     * This method is synchronized to prevent race conditions in order number generation.
     * 
     * @return the generated order number
     */
    private synchronized String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long count = orderRepository.count() + 1;
        return "ORD-%s-%04d".formatted(date, count);
    }
    
    /**
     * Validates if a status transition is allowed.
     * 
     * @param currentStatus the current order status
     * @param newStatus the new status to transition to
     * @return true if transition is valid, false otherwise
     */
    private boolean isValidStatusTransition(Order.OrderStatus currentStatus, Order.OrderStatus newStatus) {
        if (currentStatus == newStatus) {
            return true;
        }
        
        switch (currentStatus) {
            case PENDING:
                return newStatus == Order.OrderStatus.CONFIRMED || newStatus == Order.OrderStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == Order.OrderStatus.PICKING || newStatus == Order.OrderStatus.CANCELLED;
            case PICKING:
                return newStatus == Order.OrderStatus.SHIPPED || newStatus == Order.OrderStatus.CANCELLED;
            case SHIPPED:
                return newStatus == Order.OrderStatus.DELIVERED;
            case DELIVERED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }
}