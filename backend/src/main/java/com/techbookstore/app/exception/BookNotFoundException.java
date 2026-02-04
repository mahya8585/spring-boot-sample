package com.techbookstore.app.exception;

/**
 * Exception thrown when a book is not found.
 */
public class BookNotFoundException extends RuntimeException {
    
    public BookNotFoundException(String message) {
        super(message);
    }
    
    public BookNotFoundException(Long bookId) {
        super("Book not found with ID: " + bookId);
    }
}