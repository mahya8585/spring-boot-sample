package com.techbookstore.app.dto;

import com.techbookstore.app.entity.OrderItem;

import java.math.BigDecimal;

public class OrderItemDto {
    
    private Long id;
    private Long orderId;
    private Long bookId;
    private String bookTitle;
    private String bookIsbn13;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    
    // Constructors
    public OrderItemDto() {}
    
    public OrderItemDto(OrderItem orderItem) {
        this.id = orderItem.getId();
        this.orderId = orderItem.getOrder() != null ? orderItem.getOrder().getId() : null;
        this.bookId = orderItem.getBook() != null ? orderItem.getBook().getId() : null;
        this.bookTitle = orderItem.getBook() != null ? orderItem.getBook().getTitle() : null;
        this.bookIsbn13 = orderItem.getBook() != null ? orderItem.getBook().getIsbn13() : null;
        this.quantity = orderItem.getQuantity();
        this.unitPrice = orderItem.getUnitPrice();
        this.totalPrice = orderItem.getTotalPrice();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getOrderId() {
        return orderId;
    }
    
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
    
    public Long getBookId() {
        return bookId;
    }
    
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    
    public String getBookTitle() {
        return bookTitle;
    }
    
    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }
    
    public String getBookIsbn13() {
        return bookIsbn13;
    }
    
    public void setBookIsbn13(String bookIsbn13) {
        this.bookIsbn13 = bookIsbn13;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}