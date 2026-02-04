package com.techbookstore.app.dto;

import java.time.LocalDateTime;

/**
 * DTO for report export functionality.
 * レポートエクスポート機能用DTO
 */
public class ReportExportDto {
    
    private String reportId;
    private String format; // PDF, EXCEL, CSV, JSON
    private String fileName;
    private String mimeType;
    private byte[] fileData;
    private long fileSize;
    private LocalDateTime exportDate;
    private String exportedBy;
    private String status; // PENDING, IN_PROGRESS, COMPLETED, FAILED
    private String errorMessage;
    private String downloadUrl;
    
    // Constructors
    public ReportExportDto() {}
    
    public ReportExportDto(String reportId, String format) {
        this.reportId = reportId;
        this.format = format;
        this.exportDate = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getMimeType() { return mimeType; }
    public void setMimeType(String mimeType) { this.mimeType = mimeType; }
    
    public byte[] getFileData() { return fileData; }
    public void setFileData(byte[] fileData) { 
        this.fileData = fileData;
        this.fileSize = fileData != null ? fileData.length : 0;
    }
    
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    
    public LocalDateTime getExportDate() { return exportDate; }
    public void setExportDate(LocalDateTime exportDate) { this.exportDate = exportDate; }
    
    public String getExportedBy() { return exportedBy; }
    public void setExportedBy(String exportedBy) { this.exportedBy = exportedBy; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }
}