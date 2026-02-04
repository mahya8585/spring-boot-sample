package com.techbookstore.app.controller;

import com.techbookstore.app.dto.ForecastAccuracyDto;
import com.techbookstore.app.entity.DemandForecast;
import com.techbookstore.app.entity.ForecastAccuracy;
import com.techbookstore.app.service.DemandForecastService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for demand forecasting functionality
 * 需要予測機能のRESTコントローラー
 */
@RestController
@RequestMapping("/api/v1/forecasts")
public class ForecastController {

    private final DemandForecastService demandForecastService;

    public ForecastController(DemandForecastService demandForecastService) {
        this.demandForecastService = demandForecastService;
    }

    /**
     * Generate demand forecast for a specific book
     * POST /api/v1/forecasts/demand/generate
     */
    @PostMapping("/demand/generate")
    public ResponseEntity<List<DemandForecast>> generateDemandForecast(
            @RequestParam Long bookId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastDate,
            @RequestParam(defaultValue = "3") int periodMonths) {
        
        List<DemandForecast> forecasts = demandForecastService.generateForecast(bookId, forecastDate, periodMonths);
        return ResponseEntity.ok(forecasts);
    }

    /**
     * Get forecast accuracy report
     * GET /api/v1/forecasts/accuracy-report
     */
    @GetMapping("/accuracy-report")
    public ResponseEntity<List<ForecastAccuracyDto>> getForecastAccuracyReport(
            @RequestParam(required = false) String algorithm,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        // Default date range if not provided
        LocalDate finalFromDate = (fromDate != null) ? fromDate : LocalDate.now().minusMonths(3);
        LocalDate finalToDate = (toDate != null) ? toDate : LocalDate.now();
        
        List<ForecastAccuracyDto> accuracyReports;
        
        if (algorithm != null) {
            // Get accuracy for specific algorithm
            ForecastAccuracy accuracy = demandForecastService.evaluateAccuracy(algorithm, finalFromDate, finalToDate);
            ForecastAccuracyDto dto = convertToDto(accuracy);
            accuracyReports = Collections.singletonList(dto);
        } else {
            // Get accuracy for all algorithms
            String[] algorithms = {"MOVING_AVERAGE", "EXPONENTIAL_SMOOTHING", "LINEAR_REGRESSION", "SEASONAL_ADJUSTED", "ENSEMBLE"};
            accuracyReports = Arrays.asList(algorithms).stream()
                .map(alg -> {
                    ForecastAccuracy accuracy = demandForecastService.evaluateAccuracy(alg, finalFromDate, finalToDate);
                    return convertToDto(accuracy);
                })
                .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(accuracyReports);
    }

    /**
     * Get forecasts for multiple books
     * GET /api/v1/forecasts/bulk
     */
    @GetMapping("/bulk")
    public ResponseEntity<List<DemandForecast>> getBulkForecasts(
            @RequestParam List<Long> bookIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate forecastDate,
            @RequestParam(defaultValue = "3") int periodMonths) {
        
        List<DemandForecast> allForecasts = bookIds.stream()
            .flatMap(bookId -> demandForecastService.generateForecast(bookId, forecastDate, periodMonths).stream())
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(allForecasts);
    }

    /**
     * Get forecast comparison between algorithms
     * GET /api/v1/forecasts/compare
     */
    @GetMapping("/compare")
    public ResponseEntity<List<ForecastAccuracyDto>> compareAlgorithms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        
        String[] algorithms = {"MOVING_AVERAGE", "EXPONENTIAL_SMOOTHING", "LINEAR_REGRESSION", "SEASONAL_ADJUSTED", "ENSEMBLE"};
        
        List<ForecastAccuracyDto> comparisons = Arrays.asList(algorithms).stream()
            .map(algorithm -> {
                ForecastAccuracy accuracy = demandForecastService.evaluateAccuracy(algorithm, fromDate, toDate);
                return convertToDto(accuracy);
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(comparisons);
    }

    /**
     * Convert ForecastAccuracy entity to DTO
     */
    private ForecastAccuracyDto convertToDto(ForecastAccuracy accuracy) {
        ForecastAccuracyDto dto = new ForecastAccuracyDto(
            accuracy.getAlgorithm(),
            accuracy.getMae(),
            accuracy.getMape(),
            accuracy.getRmse()
        );
        dto.setEvaluationPeriodStart(accuracy.getEvaluationPeriodStart());
        dto.setEvaluationPeriodEnd(accuracy.getEvaluationPeriodEnd());
        return dto;
    }
}