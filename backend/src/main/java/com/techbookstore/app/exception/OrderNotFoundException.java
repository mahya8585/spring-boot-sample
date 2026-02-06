package com.techbookstore.app.exception;

/**
 * Exception thrown when an order is not found.
 */
public class OrderNotFoundException extends RuntimeException {
    
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId);
    }
    
    public OrderNotFoundException(String field, Object value) {
        super("Order not found with %s: %s".formatted(field, value));
    }
}