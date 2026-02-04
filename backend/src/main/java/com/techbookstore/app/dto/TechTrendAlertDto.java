package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for tech trend alerts specific to technical bookstore.
 */
public class TechTrendAlertDto {
    
    private String techCategory;
    private String alertType; // "RISING", "FALLING", "LOW_STOCK", "DEAD_STOCK", "NEW_TECH"
    private String severity; // "HIGH", "MEDIUM", "LOW"
    private String message;
    private BigDecimal impactValue;
    private Double changePercent;
    private LocalDate detectedDate;
    private String actionRequired;
    
    // Constructors
    public TechTrendAlertDto() {}
    
    public TechTrendAlertDto(String techCategory, String alertType, String severity, String message, 
                            BigDecimal impactValue, Double changePercent, LocalDate detectedDate, String actionRequired) {
        this.techCategory = techCategory;
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
        this.impactValue = impactValue;
        this.changePercent = changePercent;
        this.detectedDate = detectedDate;
        this.actionRequired = actionRequired;
    }
    
    // Getters and Setters
    public String getTechCategory() { return techCategory; }
    public void setTechCategory(String techCategory) { this.techCategory = techCategory; }
    
    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }
    
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public BigDecimal getImpactValue() { return impactValue; }
    public void setImpactValue(BigDecimal impactValue) { this.impactValue = impactValue; }
    
    public Double getChangePercent() { return changePercent; }
    public void setChangePercent(Double changePercent) { this.changePercent = changePercent; }
    
    public LocalDate getDetectedDate() { return detectedDate; }
    public void setDetectedDate(LocalDate detectedDate) { this.detectedDate = detectedDate; }
    
    public String getActionRequired() { return actionRequired; }
    public void setActionRequired(String actionRequired) { this.actionRequired = actionRequired; }
}