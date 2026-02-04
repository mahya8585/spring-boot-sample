package com.techbookstore.app.exception;

/**
 * Exception thrown when inventory is not found for a book.
 */
public class InventoryNotFoundException extends RuntimeException {
    
    public InventoryNotFoundException(String message) {
        super(message);
    }
    
    public InventoryNotFoundException(Long bookId) {
        super("Inventory not found for book ID: " + bookId);
    }
}