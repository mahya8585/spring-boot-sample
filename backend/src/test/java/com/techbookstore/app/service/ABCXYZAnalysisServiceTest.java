package com.techbookstore.app.service;

import com.techbookstore.app.entity.ABCXYZAnalysis;
import com.techbookstore.app.entity.Book;
import com.techbookstore.app.repository.ABCXYZAnalysisRepository;
import com.techbookstore.app.repository.BookRepository;
import com.techbookstore.app.repository.InventoryRepository;
import com.techbookstore.app.repository.OrderRepository;
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
class ABCXYZAnalysisServiceTest {

    @Mock
    private ABCXYZAnalysisRepository abcxyzRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private OrderRepository orderRepository;

    private ABCXYZAnalysisService abcxyzAnalysisService;

    @BeforeEach
    void setUp() {
        abcxyzAnalysisService = new ABCXYZAnalysisService(
            abcxyzRepository, bookRepository, inventoryRepository, orderRepository
        );
    }

    @Test
    void testPerformAnalysis() {
        // Given
        LocalDate analysisDate = LocalDate.now();
        
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Java: The Complete Reference");
        
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Python Crash Course");
        
        List<Book> books = Arrays.asList(book1, book2);
        
        ABCXYZAnalysis analysis1 = new ABCXYZAnalysis();
        analysis1.setId(1L);
        analysis1.setBook(book1);
        analysis1.setAbcCategory("A");
        analysis1.setXyzCategory("X");
        
        ABCXYZAnalysis analysis2 = new ABCXYZAnalysis();
        analysis2.setId(2L);
        analysis2.setBook(book2);
        analysis2.setAbcCategory("B");
        analysis2.setXyzCategory("Y");
        
        when(bookRepository.findAll()).thenReturn(books);
        when(abcxyzRepository.save(any(ABCXYZAnalysis.class)))
            .thenReturn(analysis1)
            .thenReturn(analysis2);
        
        // When
        List<ABCXYZAnalysis> results = abcxyzAnalysisService.performAnalysis(analysisDate);
        
        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    void testGetAnalysisByCategory() {
        // Given
        LocalDate analysisDate = LocalDate.now();
        
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Java Book");
        
        ABCXYZAnalysis analysis = new ABCXYZAnalysis();
        analysis.setBook(book1);
        analysis.setAbcCategory("A");
        analysis.setXyzCategory("X");
        analysis.setSalesContribution(BigDecimal.valueOf(15.5));
        analysis.setDemandVariability(BigDecimal.valueOf(0.3));
        
        when(abcxyzRepository.findByCombinedCategoryAndAnalysisDate("A", "X", analysisDate))
            .thenReturn(Arrays.asList(analysis));
        
        // When
        Map<String, List<ABCXYZAnalysis>> categoryResults = 
            abcxyzAnalysisService.getAnalysisByCategory(analysisDate);
        
        // Then
        assertNotNull(categoryResults);
        assertTrue(categoryResults.containsKey("AX"));
        assertEquals(1, categoryResults.get("AX").size());
        assertEquals("A", categoryResults.get("AX").getFirst().getAbcCategory());
        assertEquals("X", categoryResults.get("AX").getFirst().getXyzCategory());
    }
}