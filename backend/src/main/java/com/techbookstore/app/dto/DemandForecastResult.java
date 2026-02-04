package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for demand forecast results
 * Contains forecast data for a specific book and time period
 */
public class DemandForecastResult {
    
    private Long bookId;
    private String bookTitle;
    private LocalDate forecastDate;
    private Integer forecastedDemand;
    private BigDecimal confidenceLevel;
    private String algorithm; // EMA, LINEAR, SEASONAL, etc.
    private BigDecimal accuracy;
    private Integer actualDemand; // for historical validation
    
    // Forecast horizon details
    private Integer horizon; // days ahead
    private BigDecimal upperBound;
    private BigDecimal lowerBound;
    
    // Constructors
    public DemandForecastResult() {}
    
    public DemandForecastResult(Long bookId, String bookTitle, LocalDate forecastDate, 
                               Integer forecastedDemand, BigDecimal confidenceLevel, String algorithm) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.forecastDate = forecastDate;
        this.forecastedDemand = forecastedDemand;
        this.confidenceLevel = confidenceLevel;
        this.algorithm = algorithm;
    }
    
    // Getters and setters
    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }
    
    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }
    
    public Integer getForecastedDemand() { return forecastedDemand; }
    public void setForecastedDemand(Integer forecastedDemand) { this.forecastedDemand = forecastedDemand; }
    
    public BigDecimal getConfidenceLevel() { return confidenceLevel; }
    public void setConfidenceLevel(BigDecimal confidenceLevel) { this.confidenceLevel = confidenceLevel; }
    
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    
    public BigDecimal getAccuracy() { return accuracy; }
    public void setAccuracy(BigDecimal accuracy) { this.accuracy = accuracy; }
    
    public Integer getActualDemand() { return actualDemand; }
    public void setActualDemand(Integer actualDemand) { this.actualDemand = actualDemand; }
    
    public Integer getHorizon() { return horizon; }
    public void setHorizon(Integer horizon) { this.horizon = horizon; }
    
    public BigDecimal getUpperBound() { return upperBound; }
    public void setUpperBound(BigDecimal upperBound) { this.upperBound = upperBound; }
    
    public BigDecimal getLowerBound() { return lowerBound; }
    public void setLowerBound(BigDecimal lowerBound) { this.lowerBound = lowerBound; }
}