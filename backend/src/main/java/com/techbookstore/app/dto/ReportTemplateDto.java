package com.techbookstore.app.dto;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO for report templates.
 * レポートテンプレート用DTO
 */
public class ReportTemplateDto {
    
    private String templateId;
    private String templateName;
    private String description;
    private List<String> parameters;
    private List<String> outputFormats;
    private String category;
    private LocalDate createdDate;
    private String createdBy;
    private boolean isActive;
    
    // Constructors
    public ReportTemplateDto() {}
    
    public ReportTemplateDto(String templateName, String description, 
                           List<String> parameters, List<String> outputFormats) {
        this.templateName = templateName;
        this.description = description;
        this.parameters = parameters;
        this.outputFormats = outputFormats;
        this.isActive = true;
    }
    
    public ReportTemplateDto(String templateId, String templateName, String description, 
                           List<String> parameters, List<String> outputFormats) {
        this(templateName, description, parameters, outputFormats);
        this.templateId = templateId;
    }
    
    // Getters and Setters
    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getParameters() { return parameters; }
    public void setParameters(List<String> parameters) { this.parameters = parameters; }
    
    public List<String> getOutputFormats() { return outputFormats; }
    public void setOutputFormats(List<String> outputFormats) { this.outputFormats = outputFormats; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public LocalDate getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDate createdDate) { this.createdDate = createdDate; }
    
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}