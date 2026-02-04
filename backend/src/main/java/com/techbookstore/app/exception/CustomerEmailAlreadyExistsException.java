package com.techbookstore.app.exception;

/**
 * Exception thrown when customer email already exists.
 */
public class CustomerEmailAlreadyExistsException extends RuntimeException {
    
    public CustomerEmailAlreadyExistsException(String email) {
        super("Customer with email already exists: " + email);
    }
}