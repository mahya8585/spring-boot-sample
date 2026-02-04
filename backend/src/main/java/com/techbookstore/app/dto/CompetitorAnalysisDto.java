package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for competitor analysis foundation - 競合分析基盤
 */
public class CompetitorAnalysisDto {
    
    private LocalDate analysisDate;
    private String analysisScope; // CATEGORY, MARKET, PRICE, TECHNOLOGY
    private List<CompetitorMetric> competitorMetrics;
    private MarketPositioning marketPositioning;
    private List<CompetitiveAdvantage> competitiveAdvantages;
    private List<ThreatAnalysis> threats;
    private StrategicRecommendations strategicRecommendations;
    
    // Constructors
    public CompetitorAnalysisDto() {}
    
    public CompetitorAnalysisDto(LocalDate analysisDate, String analysisScope) {
        this.analysisDate = analysisDate;
        this.analysisScope = analysisScope;
    }
    
    // Inner classes for competitor analysis
    public static class CompetitorMetric {
        private String competitorType; // DIRECT, INDIRECT, ONLINE, TRADITIONAL
        private String competitorName;
        private String categoryCode;
        private BigDecimal estimatedMarketShare;
        private String pricingStrategy; // PREMIUM, COMPETITIVE, DISCOUNT
        private String strengthAreas;
        private String weaknessAreas;
        private String competitiveRating; // HIGH_THREAT, MEDIUM_THREAT, LOW_THREAT
        
        public CompetitorMetric() {}
        
        public CompetitorMetric(String competitorType, String competitorName, String categoryCode, 
                              BigDecimal estimatedMarketShare) {
            this.competitorType = competitorType;
            this.competitorName = competitorName;
            this.categoryCode = categoryCode;
            this.estimatedMarketShare = estimatedMarketShare;
        }
        
        // Getters and Setters
        public String getCompetitorType() { return competitorType; }
        public void setCompetitorType(String competitorType) { this.competitorType = competitorType; }
        public String getCompetitorName() { return competitorName; }
        public void setCompetitorName(String competitorName) { this.competitorName = competitorName; }
        public String getCategoryCode() { return categoryCode; }
        public void setCategoryCode(String categoryCode) { this.categoryCode = categoryCode; }
        public BigDecimal getEstimatedMarketShare() { return estimatedMarketShare; }
        public void setEstimatedMarketShare(BigDecimal estimatedMarketShare) { this.estimatedMarketShare = estimatedMarketShare; }
        public String getPricingStrategy() { return pricingStrategy; }
        public void setPricingStrategy(String pricingStrategy) { this.pricingStrategy = pricingStrategy; }
        public String getStrengthAreas() { return strengthAreas; }
        public void setStrengthAreas(String strengthAreas) { this.strengthAreas = strengthAreas; }
        public String getWeaknessAreas() { return weaknessAreas; }
        public void setWeaknessAreas(String weaknessAreas) { this.weaknessAreas = weaknessAreas; }
        public String getCompetitiveRating() { return competitiveRating; }
        public void setCompetitiveRating(String competitiveRating) { this.competitiveRating = competitiveRating; }
    }
    
    public static class MarketPositioning {
        private BigDecimal ourMarketShare;
        private String positioningStrategy; // LEADER, CHALLENGER, FOLLOWER, NICHE
        private List<String> differentiators;
        private List<String> competitiveGaps;
        private String overallCompetitivePosition;
        private String strategicRecommendation;
        
        public MarketPositioning() {}
        
        public MarketPositioning(BigDecimal ourMarketShare, String positioningStrategy, 
                               String overallCompetitivePosition) {
            this.ourMarketShare = ourMarketShare;
            this.positioningStrategy = positioningStrategy;
            this.overallCompetitivePosition = overallCompetitivePosition;
        }
        
        // Getters and Setters
        public BigDecimal getOurMarketShare() { return ourMarketShare; }
        public void setOurMarketShare(BigDecimal ourMarketShare) { this.ourMarketShare = ourMarketShare; }
        public String getPositioningStrategy() { return positioningStrategy; }
        public void setPositioningStrategy(String positioningStrategy) { this.positioningStrategy = positioningStrategy; }
        public List<String> getDifferentiators() { return differentiators; }
        public void setDifferentiators(List<String> differentiators) { this.differentiators = differentiators; }
        public List<String> getCompetitiveGaps() { return competitiveGaps; }
        public void setCompetitiveGaps(List<String> competitiveGaps) { this.competitiveGaps = competitiveGaps; }
        public String getOverallCompetitivePosition() { return overallCompetitivePosition; }
        public void setOverallCompetitivePosition(String overallCompetitivePosition) { this.overallCompetitivePosition = overallCompetitivePosition; }
        public String getStrategicRecommendation() { return strategicRecommendation; }
        public void setStrategicRecommendation(String strategicRecommendation) { this.strategicRecommendation = strategicRecommendation; }
    }
    
    public static class CompetitiveAdvantage {
        private String advantageType; // PRICING, SELECTION, SERVICE, TECHNOLOGY, LOCATION
        private String description;
        private String strengthLevel; // STRONG, MODERATE, WEAK
        private String sustainability; // SUSTAINABLE, TEMPORARY, AT_RISK
        private String leverageOpportunity;
        
        public CompetitiveAdvantage() {}
        
        public CompetitiveAdvantage(String advantageType, String description, String strengthLevel) {
            this.advantageType = advantageType;
            this.description = description;
            this.strengthLevel = strengthLevel;
        }
        
        // Getters and Setters
        public String getAdvantageType() { return advantageType; }
        public void setAdvantageType(String advantageType) { this.advantageType = advantageType; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStrengthLevel() { return strengthLevel; }
        public void setStrengthLevel(String strengthLevel) { this.strengthLevel = strengthLevel; }
        public String getSustainability() { return sustainability; }
        public void setSustainability(String sustainability) { this.sustainability = sustainability; }
        public String getLeverageOpportunity() { return leverageOpportunity; }
        public void setLeverageOpportunity(String leverageOpportunity) { this.leverageOpportunity = leverageOpportunity; }
    }
    
    public static class ThreatAnalysis {
        private String threatType; // NEW_ENTRANT, SUBSTITUTE, PRICE_WAR, TECHNOLOGY_DISRUPTION
        private String threatSource;
        private String severity; // HIGH, MEDIUM, LOW
        private String probability; // HIGH, MEDIUM, LOW
        private String timeframe; // IMMEDIATE, SHORT_TERM, LONG_TERM
        private String mitigationStrategy;
        private String contingencyPlan;
        
        public ThreatAnalysis() {}
        
        public ThreatAnalysis(String threatType, String threatSource, String severity, String probability) {
            this.threatType = threatType;
            this.threatSource = threatSource;
            this.severity = severity;
            this.probability = probability;
        }
        
        // Getters and Setters
        public String getThreatType() { return threatType; }
        public void setThreatType(String threatType) { this.threatType = threatType; }
        public String getThreatSource() { return threatSource; }
        public void setThreatSource(String threatSource) { this.threatSource = threatSource; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getProbability() { return probability; }
        public void setProbability(String probability) { this.probability = probability; }
        public String getTimeframe() { return timeframe; }
        public void setTimeframe(String timeframe) { this.timeframe = timeframe; }
        public String getMitigationStrategy() { return mitigationStrategy; }
        public void setMitigationStrategy(String mitigationStrategy) { this.mitigationStrategy = mitigationStrategy; }
        public String getContingencyPlan() { return contingencyPlan; }
        public void setContingencyPlan(String contingencyPlan) { this.contingencyPlan = contingencyPlan; }
    }
    
    public static class StrategicRecommendations {
        private List<String> immediateActions;
        private List<String> shortTermStrategies;
        private List<String> longTermStrategies;
        private String investmentPriorities;
        private String competitiveResponsePlan;
        private String differentiationStrategy;
        
        public StrategicRecommendations() {}
        
        public StrategicRecommendations(List<String> immediateActions, List<String> shortTermStrategies, 
                                      List<String> longTermStrategies) {
            this.immediateActions = immediateActions;
            this.shortTermStrategies = shortTermStrategies;
            this.longTermStrategies = longTermStrategies;
        }
        
        // Getters and Setters
        public List<String> getImmediateActions() { return immediateActions; }
        public void setImmediateActions(List<String> immediateActions) { this.immediateActions = immediateActions; }
        public List<String> getShortTermStrategies() { return shortTermStrategies; }
        public void setShortTermStrategies(List<String> shortTermStrategies) { this.shortTermStrategies = shortTermStrategies; }
        public List<String> getLongTermStrategies() { return longTermStrategies; }
        public void setLongTermStrategies(List<String> longTermStrategies) { this.longTermStrategies = longTermStrategies; }
        public String getInvestmentPriorities() { return investmentPriorities; }
        public void setInvestmentPriorities(String investmentPriorities) { this.investmentPriorities = investmentPriorities; }
        public String getCompetitiveResponsePlan() { return competitiveResponsePlan; }
        public void setCompetitiveResponsePlan(String competitiveResponsePlan) { this.competitiveResponsePlan = competitiveResponsePlan; }
        public String getDifferentiationStrategy() { return differentiationStrategy; }
        public void setDifferentiationStrategy(String differentiationStrategy) { this.differentiationStrategy = differentiationStrategy; }
    }
    
    // Main class getters and setters
    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }
    public String getAnalysisScope() { return analysisScope; }
    public void setAnalysisScope(String analysisScope) { this.analysisScope = analysisScope; }
    public List<CompetitorMetric> getCompetitorMetrics() { return competitorMetrics; }
    public void setCompetitorMetrics(List<CompetitorMetric> competitorMetrics) { this.competitorMetrics = competitorMetrics; }
    public MarketPositioning getMarketPositioning() { return marketPositioning; }
    public void setMarketPositioning(MarketPositioning marketPositioning) { this.marketPositioning = marketPositioning; }
    public List<CompetitiveAdvantage> getCompetitiveAdvantages() { return competitiveAdvantages; }
    public void setCompetitiveAdvantages(List<CompetitiveAdvantage> competitiveAdvantages) { this.competitiveAdvantages = competitiveAdvantages; }
    public List<ThreatAnalysis> getThreats() { return threats; }
    public void setThreats(List<ThreatAnalysis> threats) { this.threats = threats; }
    public StrategicRecommendations getStrategicRecommendations() { return strategicRecommendations; }
    public void setStrategicRecommendations(StrategicRecommendations strategicRecommendations) { this.strategicRecommendations = strategicRecommendations; }
}