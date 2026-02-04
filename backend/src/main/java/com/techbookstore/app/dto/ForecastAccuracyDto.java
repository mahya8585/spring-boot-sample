package com.techbookstore.app.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * DTO for forecast results and accuracy metrics
 * 予測結果・精度メトリクスDTO
 */
public class ForecastAccuracyDto {

    private String algorithm;
    private BigDecimal mae;
    private BigDecimal mape;
    private BigDecimal rmse;
    private LocalDate evaluationPeriodStart;
    private LocalDate evaluationPeriodEnd;
    private Map<String, BigDecimal> algorithmComparison;
    private List<AccuracyTrend> accuracyTrends;

    // Constructors
    public ForecastAccuracyDto() {}

    public ForecastAccuracyDto(String algorithm, BigDecimal mae, BigDecimal mape, BigDecimal rmse) {
        this.algorithm = algorithm;
        this.mae = mae;
        this.mape = mape;
        this.rmse = rmse;
    }

    // Getters and Setters
    public String getAlgorithm() { return algorithm; }
    public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }

    public BigDecimal getMae() { return mae; }
    public void setMae(BigDecimal mae) { this.mae = mae; }

    public BigDecimal getMape() { return mape; }
    public void setMape(BigDecimal mape) { this.mape = mape; }

    public BigDecimal getRmse() { return rmse; }
    public void setRmse(BigDecimal rmse) { this.rmse = rmse; }

    public LocalDate getEvaluationPeriodStart() { return evaluationPeriodStart; }
    public void setEvaluationPeriodStart(LocalDate evaluationPeriodStart) { this.evaluationPeriodStart = evaluationPeriodStart; }

    public LocalDate getEvaluationPeriodEnd() { return evaluationPeriodEnd; }
    public void setEvaluationPeriodEnd(LocalDate evaluationPeriodEnd) { this.evaluationPeriodEnd = evaluationPeriodEnd; }

    public Map<String, BigDecimal> getAlgorithmComparison() { return algorithmComparison; }
    public void setAlgorithmComparison(Map<String, BigDecimal> algorithmComparison) { this.algorithmComparison = algorithmComparison; }

    public List<AccuracyTrend> getAccuracyTrends() { return accuracyTrends; }
    public void setAccuracyTrends(List<AccuracyTrend> accuracyTrends) { this.accuracyTrends = accuracyTrends; }

    /**
     * Inner class for accuracy trend data
     */
    public static class AccuracyTrend {
        private LocalDate date;
        private BigDecimal mapeValue;
        private String algorithm;

        public AccuracyTrend() {}

        public AccuracyTrend(LocalDate date, BigDecimal mapeValue, String algorithm) {
            this.date = date;
            this.mapeValue = mapeValue;
            this.algorithm = algorithm;
        }

        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public BigDecimal getMapeValue() { return mapeValue; }
        public void setMapeValue(BigDecimal mapeValue) { this.mapeValue = mapeValue; }

        public String getAlgorithm() { return algorithm; }
        public void setAlgorithm(String algorithm) { this.algorithm = algorithm; }
    }
}