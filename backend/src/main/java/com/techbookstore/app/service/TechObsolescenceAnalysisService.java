package com.techbookstore.app.service;

import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.ObsolescenceAssessment;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.ObsolescenceAssessmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for Technology Obsolescence Risk Analysis
 * 技術陳腐化リスク分析サービス
 */
@Service
@Transactional
public class TechObsolescenceAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(TechObsolescenceAnalysisService.class);

    private final ObsolescenceAssessmentRepository obsolescenceRepository;
    private final BookRepository bookRepository;

    // Risk score weights (total must equal 1.0)
    private static final double PUBLICATION_YEAR_WEIGHT = 0.3;
    private static final double TECH_TREND_WEIGHT = 0.4;
    private static final double MARKET_DEMAND_WEIGHT = 0.2;
    private static final double COMPETITION_WEIGHT = 0.1;

    // Tech lifecycle thresholds (in years since publication)
    private static final int EMERGING_MAX_YEARS = 1;
    private static final int GROWTH_MAX_YEARS = 3;
    private static final int MATURE_MAX_YEARS = 7;
    // DECLINING is beyond MATURE_MAX_YEARS
    
    // Risk level thresholds
    private static final double HIGH_RISK_THRESHOLD = 70.0;
    private static final double MEDIUM_RISK_THRESHOLD = 40.0;
    
    // Obsolescence time estimation constants
    private static final int VERY_HIGH_RISK_MONTHS = 6;
    private static final int HIGH_RISK_MONTHS = 12;
    private static final int MEDIUM_RISK_MONTHS = 24;
    private static final int LOW_MEDIUM_RISK_MONTHS = 48;
    private static final int LOW_RISK_MONTHS = 96;

    public TechObsolescenceAnalysisService(ObsolescenceAssessmentRepository obsolescenceRepository,
                                         BookRepository bookRepository) {
        this.obsolescenceRepository = obsolescenceRepository;
        this.bookRepository = bookRepository;
    }

    /**
     * Perform comprehensive obsolescence risk analysis for all books
     * 全書籍の陳腐化リスク分析を実行
     */
    public List<ObsolescenceAssessment> performObsolescenceAnalysis(LocalDate assessmentDate) {
        if (assessmentDate == null) {
            throw new IllegalArgumentException("Assessment date cannot be null");
        }
        
        logger.info("Starting tech obsolescence analysis for date: {}", assessmentDate);

        try {
            List<Book> books = bookRepository.findAll();
            
            if (books.isEmpty()) {
                logger.warn("No books found for obsolescence analysis");
                return new ArrayList<>();
            }
            
            List<ObsolescenceAssessment> assessments = new ArrayList<>();

            for (Book book : books) {
                try {
                    ObsolescenceAssessment assessment = analyzeBook(book, assessmentDate);
                    assessments.add(obsolescenceRepository.save(assessment));
                } catch (Exception e) {
                    logger.error("Failed to analyze obsolescence for book ID: {}", book.getId(), e);
                    // Continue with other books
                }
            }

            logger.info("Completed obsolescence analysis for {} books", assessments.size());
            return assessments;
            
        } catch (Exception e) {
            logger.error("Failed to perform obsolescence analysis for date: {}", assessmentDate, e);
            throw new RuntimeException("Obsolescence analysis failed", e);
        }
    }

    /**
     * Analyze individual book for obsolescence risk
     * 個別書籍の陳腐化リスク分析
     */
    public ObsolescenceAssessment analyzeBook(Book book, LocalDate assessmentDate) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null");
        }
        if (assessmentDate == null) {
            throw new IllegalArgumentException("Assessment date cannot be null");
        }
        
        try {
            // Calculate risk score components
            BigDecimal publicationYearScore = calculatePublicationYearScore(book, assessmentDate);
            BigDecimal techTrendScore = calculateTechTrendScore(book);
            BigDecimal marketDemandScore = calculateMarketDemandScore(book);
            BigDecimal competitionScore = calculateCompetitionScore(book);

            // Calculate weighted total risk score
            BigDecimal totalRiskScore = publicationYearScore.multiply(BigDecimal.valueOf(PUBLICATION_YEAR_WEIGHT))
                .add(techTrendScore.multiply(BigDecimal.valueOf(TECH_TREND_WEIGHT)))
                .add(marketDemandScore.multiply(BigDecimal.valueOf(MARKET_DEMAND_WEIGHT)))
                .add(competitionScore.multiply(BigDecimal.valueOf(COMPETITION_WEIGHT)))
                .setScale(2, RoundingMode.HALF_UP);

            // Validate risk score is within expected range
            if (totalRiskScore.compareTo(BigDecimal.ZERO) < 0 || totalRiskScore.compareTo(BigDecimal.valueOf(100)) > 0) {
                logger.warn("Risk score out of range for book ID {}: {}", book.getId(), totalRiskScore);
                totalRiskScore = totalRiskScore.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
            }

            // Determine risk level
            String riskLevel = determineRiskLevel(totalRiskScore);

            // Calculate months to obsolescence
            Integer monthsToObsolescence = calculateMonthsToObsolescence(book, totalRiskScore, assessmentDate);

            return new ObsolescenceAssessment(book, riskLevel, monthsToObsolescence, totalRiskScore, assessmentDate);
            
        } catch (Exception e) {
            logger.error("Failed to analyze book ID: {}", book.getId(), e);
            // Return a safe default assessment
            return new ObsolescenceAssessment(book, "MEDIUM", 24, BigDecimal.valueOf(50.0), assessmentDate);
        }
    }

    /**
     * Calculate publication year score (newer = lower risk)
     * 出版年スコア計算（新しいほど低リスク）
     */
    private BigDecimal calculatePublicationYearScore(Book book, LocalDate assessmentDate) {
        if (book.getPublicationDate() == null) {
            return BigDecimal.valueOf(50.0); // Default medium risk
        }

        int yearsOld = assessmentDate.getYear() - book.getPublicationDate().getYear();
        
        // Score increases with age (higher score = higher risk)
        if (yearsOld <= 1) {
            return BigDecimal.valueOf(10.0); // Very new, low risk
        } else if (yearsOld <= 3) {
            return BigDecimal.valueOf(25.0); // Recent, low-medium risk
        } else if (yearsOld <= 5) {
            return BigDecimal.valueOf(50.0); // Medium risk
        } else if (yearsOld <= 10) {
            return BigDecimal.valueOf(75.0); // High risk
        } else {
            return BigDecimal.valueOf(90.0); // Very old, very high risk
        }
    }

    /**
     * Calculate technology trend score based on category
     * カテゴリに基づく技術トレンドスコア計算
     */
    private BigDecimal calculateTechTrendScore(Book book) {
        String title = book.getTitle().toLowerCase();
        
        // High-demand technologies (lower risk score)
        if (title.contains("ai") || title.contains("machine learning") || title.contains("python") || 
            title.contains("cloud") || title.contains("docker") || title.contains("kubernetes")) {
            return BigDecimal.valueOf(15.0);
        }
        // Stable technologies (medium risk score)
        else if (title.contains("java") || title.contains("javascript") || title.contains("react") || 
                 title.contains("spring") || title.contains("database")) {
            return BigDecimal.valueOf(35.0);
        }
        // Declining technologies (high risk score)
        else if (title.contains("flash") || title.contains("silverlight") || title.contains("jquery") || 
                 title.contains("php 5") || title.contains("legacy")) {
            return BigDecimal.valueOf(85.0);
        }
        // Unknown/Other technologies (medium risk score)
        else {
            return BigDecimal.valueOf(50.0);
        }
    }

    /**
     * Calculate market demand score
     * 市場需要スコア計算
     */
    private BigDecimal calculateMarketDemandScore(Book book) {
        // Mock implementation - in real system, analyze sales trends, search volumes, job postings, etc.
        Random random = new Random(book.getId());
        
        // Simulate market demand analysis
        double demandFactor = random.nextDouble();
        
        if (demandFactor > 0.7) {
            return BigDecimal.valueOf(20.0); // High demand, low risk
        } else if (demandFactor > 0.4) {
            return BigDecimal.valueOf(45.0); // Medium demand, medium risk
        } else {
            return BigDecimal.valueOf(75.0); // Low demand, high risk
        }
    }

    /**
     * Calculate competition score
     * 競合スコア計算
     */
    private BigDecimal calculateCompetitionScore(Book book) {
        // Mock implementation - in real system, analyze competitor books, market saturation
        Random random = new Random(book.getId() + 1000);
        
        // Simulate competition analysis
        double competitionLevel = random.nextDouble();
        
        if (competitionLevel > 0.8) {
            return BigDecimal.valueOf(80.0); // High competition, high risk
        } else if (competitionLevel > 0.5) {
            return BigDecimal.valueOf(50.0); // Medium competition, medium risk
        } else {
            return BigDecimal.valueOf(25.0); // Low competition, low risk
        }
    }

    /**
     * Determine risk level based on total score
     * 総合スコアに基づくリスクレベル判定
     */
    private String determineRiskLevel(BigDecimal riskScore) {
        double score = riskScore.doubleValue();
        
        if (score >= HIGH_RISK_THRESHOLD) {
            return "HIGH";
        } else if (score >= MEDIUM_RISK_THRESHOLD) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    /**
     * Calculate estimated months to obsolescence
     * 陳腐化までの推定月数計算
     */
    private Integer calculateMonthsToObsolescence(Book book, BigDecimal riskScore, LocalDate assessmentDate) {
        double score = riskScore.doubleValue();
        
        // Base months calculation using constants
        int baseMonths;
        if (score >= 80.0) {
            baseMonths = VERY_HIGH_RISK_MONTHS; // Very high risk - 6 months
        } else if (score >= HIGH_RISK_THRESHOLD) {
            baseMonths = HIGH_RISK_MONTHS; // High risk - 1 year
        } else if (score >= 50.0) {
            baseMonths = MEDIUM_RISK_MONTHS; // Medium risk - 2 years
        } else if (score >= 30.0) {
            baseMonths = LOW_MEDIUM_RISK_MONTHS; // Low-medium risk - 4 years
        } else {
            baseMonths = LOW_RISK_MONTHS; // Low risk - 8 years
        }
        
        // Adjust based on publication date
        if (book.getPublicationDate() != null) {
            int yearsOld = assessmentDate.getYear() - book.getPublicationDate().getYear();
            // Older books obsolete faster
            if (yearsOld > 5) {
                baseMonths = Math.max(3, baseMonths - (yearsOld - 5) * 6);
            }
        }
        
        return baseMonths;
    }

    /**
     * Determine technology lifecycle stage
     * 技術ライフサイクル段階判定
     */
    public String determineTechLifecycleStage(Book book, LocalDate assessmentDate) {
        if (book.getPublicationDate() == null) {
            return "MATURE"; // Default for unknown publication date
        }
        
        int yearsOld = assessmentDate.getYear() - book.getPublicationDate().getYear();
        
        if (yearsOld <= EMERGING_MAX_YEARS) {
            return "EMERGING";
        } else if (yearsOld <= GROWTH_MAX_YEARS) {
            return "GROWTH";
        } else if (yearsOld <= MATURE_MAX_YEARS) {
            return "MATURE";
        } else {
            return "DECLINING";
        }
    }

    /**
     * Get high-risk items requiring immediate action
     * 即座の対応が必要な高リスクアイテムを取得
     */
    @Transactional(readOnly = true)
    public List<ObsolescenceAssessment> getHighRiskItems(Integer withinMonths) {
        return obsolescenceRepository.findHighRiskItemsWithinMonths(withinMonths);
    }

    /**
     * Get obsolescence analysis by risk level
     * リスクレベル別陳腐化分析を取得
     */
    @Transactional(readOnly = true)
    public Map<String, List<ObsolescenceAssessment>> getAnalysisByRiskLevel(LocalDate assessmentDate) {
        Map<String, List<ObsolescenceAssessment>> riskAnalysis = new HashMap<>();
        
        String[] riskLevels = {"HIGH", "MEDIUM", "LOW"};
        
        for (String riskLevel : riskLevels) {
            List<ObsolescenceAssessment> assessments = obsolescenceRepository
                .findByRiskLevelAndAssessmentDate(riskLevel, assessmentDate);
            riskAnalysis.put(riskLevel, assessments);
        }
        
        return riskAnalysis;
    }

    /**
     * Get technology lifecycle distribution
     * 技術ライフサイクル分布を取得
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getTechLifecycleDistribution(LocalDate assessmentDate) {
        List<Book> books = bookRepository.findAll();
        
        return books.stream()
            .collect(Collectors.groupingBy(
                book -> determineTechLifecycleStage(book, assessmentDate),
                Collectors.counting()
            ));
    }
}