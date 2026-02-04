package com.techbookstore.app.exception;

/**
 * Exception thrown when there is insufficient inventory for an order.
 */
public class InsufficientInventoryException extends RuntimeException {
    
    public InsufficientInventoryException(String message) {
        super(message);
    }
    
    public InsufficientInventoryException(String bookTitle, int requested, int available) {
        super(String.format("Insufficient inventory for '%s': requested %d, available %d", 
                           bookTitle, requested, available));
    }
}