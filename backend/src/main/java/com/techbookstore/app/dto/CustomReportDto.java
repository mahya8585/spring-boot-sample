package com.techbookstore.app.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for custom report creation and management.
 * カスタムレポート作成・管理用DTO
 */
public class CustomReportDto {
    
    private String reportId;
    private String reportName;
    private String reportType;
    private String description;
    private Map<String, Object> reportData;
    private LocalDate createdDate;
    private String createdBy;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED
    private String errorMessage;
    private List<String> tags;
    private Map<String, Object> metadata;
    
    // Constructors
    public CustomReportDto() {}
    
    public CustomReportDto(String reportName, String reportType, String createdBy) {
        this.reportName = reportName;
        this.reportType = reportType;
        this.createdBy = createdBy;
        this.createdDate = LocalDate.now();
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    
    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Map<String, Object> getReportData() { return reportData; }
    public void setReportData(Map<String, Object> reportData) { this.reportData = reportData; }
    
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}