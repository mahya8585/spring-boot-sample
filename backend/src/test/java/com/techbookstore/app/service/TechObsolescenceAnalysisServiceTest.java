package com.techbookstore.app.service;

import com.techbookstore.app.entity.Book;
import com.techbookstore.app.entity.ObsolescenceAssessment;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.ObsolescenceAssessmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TechObsolescenceAnalysisServiceTest {

    @Mock
    private ObsolescenceAssessmentRepository obsolescenceRepository;

    @Mock
    private BookRepository bookRepository;

    private TechObsolescenceAnalysisService obsolescenceService;

    @BeforeEach
    void setUp() {
        obsolescenceService = new TechObsolescenceAnalysisService(
            obsolescenceRepository, bookRepository
        );
    }

    @Test
    void testPerformObsolescenceAnalysis() {
        // Given
        LocalDate assessmentDate = LocalDate.now();
        
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Modern AI with Python");
        book1.setPublicationDate(LocalDate.now().minusYears(1));
        
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Legacy Flash Development");
        book2.setPublicationDate(LocalDate.now().minusYears(10));
        
        List<Book> books = Arrays.asList(book1, book2);
        
        ObsolescenceAssessment assessment1 = new ObsolescenceAssessment();
        assessment1.setId(1L);
        assessment1.setBook(book1);
        assessment1.setRiskLevel("LOW");
        
        ObsolescenceAssessment assessment2 = new ObsolescenceAssessment();
        assessment2.setId(2L);
        assessment2.setBook(book2);
        assessment2.setRiskLevel("HIGH");
        
        when(bookRepository.findAll()).thenReturn(books);
        when(obsolescenceRepository.save(any(ObsolescenceAssessment.class)))
            .thenReturn(assessment1)
            .thenReturn(assessment2);
        
        // When
        List<ObsolescenceAssessment> results = obsolescenceService.performObsolescenceAnalysis(assessmentDate);
        
        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    void testDetermineTechLifecycleStage() {
        // Given
        LocalDate assessmentDate = LocalDate.now();
        
        Book emergingBook = new Book();
        emergingBook.setPublicationDate(LocalDate.now().minusMonths(6));
        
        Book growthBook = new Book();
        growthBook.setPublicationDate(LocalDate.now().minusYears(2));
        
        Book matureBook = new Book();
        matureBook.setPublicationDate(LocalDate.now().minusYears(5));
        
        Book decliningBook = new Book();
        decliningBook.setPublicationDate(LocalDate.now().minusYears(10));
        
        // When & Then
        assertEquals("EMERGING", obsolescenceService.determineTechLifecycleStage(emergingBook, assessmentDate));
        assertEquals("GROWTH", obsolescenceService.determineTechLifecycleStage(growthBook, assessmentDate));
        assertEquals("MATURE", obsolescenceService.determineTechLifecycleStage(matureBook, assessmentDate));
        assertEquals("DECLINING", obsolescenceService.determineTechLifecycleStage(decliningBook, assessmentDate));
    }

    @Test
    void testAnalyzeBook() {
        // Given
        LocalDate assessmentDate = LocalDate.now();
        
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Legacy Java Framework");
        book.setPublicationDate(LocalDate.now().minusYears(8));
        
        // When
        ObsolescenceAssessment assessment = obsolescenceService.analyzeBook(book, assessmentDate);
        
        // Then
        assertNotNull(assessment);
        assertEquals(book, assessment.getBook());
        assertNotNull(assessment.getRiskLevel());
        assertNotNull(assessment.getRiskScore());
        assertNotNull(assessment.getMonthsToObsolescence());
        assertTrue(assessment.getRiskScore().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(assessment.getRiskScore().compareTo(BigDecimal.valueOf(100)) <= 0);
    }

    @Test
    void testGetAnalysisByRiskLevel() {
        // Given
        LocalDate assessmentDate = LocalDate.now();
        
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("High Risk Book");
        
        ObsolescenceAssessment highRiskAssessment = new ObsolescenceAssessment();
        highRiskAssessment.setBook(book1);
        highRiskAssessment.setRiskLevel("HIGH");
        highRiskAssessment.setRiskScore(BigDecimal.valueOf(85.0));
        
        when(obsolescenceRepository.findByRiskLevelAndAssessmentDate("HIGH", assessmentDate))
            .thenReturn(Arrays.asList(highRiskAssessment));
        when(obsolescenceRepository.findByRiskLevelAndAssessmentDate("MEDIUM", assessmentDate))
            .thenReturn(Arrays.asList());
        when(obsolescenceRepository.findByRiskLevelAndAssessmentDate("LOW", assessmentDate))
            .thenReturn(Arrays.asList());
        
        // When
        Map<String, List<ObsolescenceAssessment>> riskAnalysis = 
            obsolescenceService.getAnalysisByRiskLevel(assessmentDate);
        
        // Then
        assertNotNull(riskAnalysis);
        assertTrue(riskAnalysis.containsKey("HIGH"));
        assertTrue(riskAnalysis.containsKey("MEDIUM"));
        assertTrue(riskAnalysis.containsKey("LOW"));
        assertEquals(1, riskAnalysis.get("HIGH").size());
        assertEquals(0, riskAnalysis.get("MEDIUM").size());
        assertEquals(0, riskAnalysis.get("LOW").size());
    }
}