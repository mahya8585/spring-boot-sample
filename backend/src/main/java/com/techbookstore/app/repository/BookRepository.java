package com.techbookstore.app.repository;

import com.techbookstore.app.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn13(String isbn13);

    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.titleEn) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(b.isbn13) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Temporarily disabled due to missing entity relationships
    // @Query("SELECT b FROM Book b JOIN b.bookCategories bc WHERE bc.category.id = :categoryId")
    // Page<Book> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT b FROM Book b WHERE b.level = :level")
    Page<Book> findByLevel(@Param("level") Book.TechLevel level, Pageable pageable);

    // Temporarily disabled due to missing entity relationships  
    // @Query("SELECT b FROM Book b JOIN b.bookAuthors ba WHERE ba.author.id = :authorId")
    // List<Book> findByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT b FROM Book b WHERE b.publisher.id = :publisherId")
    Page<Book> findByPublisherId(@Param("publisherId") Long publisherId, Pageable pageable);

    // Temporarily disabled due to missing entity property
    // @Query("SELECT b FROM Book b ORDER BY b.createdAt DESC")
    // List<Book> findLatestBooks(Pageable pageable);
}