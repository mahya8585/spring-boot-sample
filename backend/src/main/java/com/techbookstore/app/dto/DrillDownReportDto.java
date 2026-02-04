package com.techbookstore.app.dto;

import java.time.LocalDate;
import java.util.Map;

/**
 * DTO for drill-down reports.
 * ドリルダウンレポート用DTO
 */
public class DrillDownReportDto {
    
    private String reportType;
    private String drillDownDimension;
    private Map<String, Object> filters;
    private Map<String, Object> data;
    private LocalDate generatedDate;
    private String generatedBy;
    private int depth;
    
    // Constructors
    public DrillDownReportDto() {}
    
    public DrillDownReportDto(String reportType, String drillDownDimension, 
                             Map<String, Object> filters, Map<String, Object> data) {
        this.reportType = reportType;
        this.drillDownDimension = drillDownDimension;
        this.filters = filters;
        this.data = data;
        this.generatedDate = LocalDate.now();
    }
    
    // Getters and Setters
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public String getDrillDownDimension() { return drillDownDimension; }
    public void setDrillDownDimension(String drillDownDimension) { this.drillDownDimension = drillDownDimension; }
    
    public Map<String, Object> getFilters() { return filters; }
    public void setFilters(Map<String, Object> filters) { this.filters = filters; }
    
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
    
    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }
    
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    
    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }
}