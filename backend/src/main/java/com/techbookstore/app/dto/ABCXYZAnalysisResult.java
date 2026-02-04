package com.techbookstore.app.dto;

import java.math.BigDecimal;

/**
 * DTO for ABC/XYZ Analysis results
 * Represents the analysis outcome for a specific book
 */
public class ABCXYZAnalysisResult {
    
    private Long bookId;
    private String bookTitle;
    private String abcClassification; // A, B, C
    private String xyzClassification; // X, Y, Z
    private BigDecimal salesValue;
    private BigDecimal variationCoefficient;
    private Integer currentStock;
    private String analysisDate;
    
    // Constructors
    public ABCXYZAnalysisResult() {}
    
    public ABCXYZAnalysisResult(Long bookId, String bookTitle, String abcClassification, 
                               String xyzClassification, BigDecimal salesValue, 
                               BigDecimal variationCoefficient) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.abcClassification = abcClassification;
        this.xyzClassification = xyzClassification;
        this.salesValue = salesValue;
        this.variationCoefficient = variationCoefficient;
    }
    
    // Getters and setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public String getAbcClassification() { return abcClassification; }
    public void setAbcClassification(String abcClassification) { this.abcClassification = abcClassification; }
    
    public String getXyzClassification() { return xyzClassification; }
    public void setXyzClassification(String xyzClassification) { this.xyzClassification = xyzClassification; }
    
    public BigDecimal getSalesValue() { return salesValue; }
    public void setSalesValue(BigDecimal salesValue) { this.salesValue = salesValue; }
    
    public BigDecimal getVariationCoefficient() { return variationCoefficient; }
    public void setVariationCoefficient(BigDecimal variationCoefficient) { this.variationCoefficient = variationCoefficient; }
    
    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }
    
    public String getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(String analysisDate) { this.analysisDate = analysisDate; }
}