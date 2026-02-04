package com.techbookstore.app.repository;

import com.techbookstore.app.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    @Query("SELECT a FROM Author a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.nameKana) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Author> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
}