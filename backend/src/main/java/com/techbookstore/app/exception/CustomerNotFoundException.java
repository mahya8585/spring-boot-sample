package com.techbookstore.app.exception;

/**
 * Exception thrown when a customer is not found.
 */
public class CustomerNotFoundException extends RuntimeException {
    
    public CustomerNotFoundException(String message) {
        super(message);
    }
    
    public CustomerNotFoundException(Long customerId) {
        super("Customer not found with ID: " + customerId);
    }
    
    public CustomerNotFoundException(String field, Object value) {
        super("Customer not found with %s: %s".formatted(field, value));
    }
}