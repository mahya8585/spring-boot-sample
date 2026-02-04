package com.techbookstore.app.repository;

import com.techbookstore.app.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    
    /**
     * Finds a customer by email address.
     * Used for email uniqueness validation and customer lookup.
     * 
     * @param email the email address to search for
     * @return optional customer if found
     */
    Optional<Customer> findByEmail(String email);
    
    /**
     * Finds customers by status.
     * 
     * @param status the customer status
     * @return list of customers with the specified status
     */
    List<Customer> findByStatus(Customer.CustomerStatus status);
    
    /**
     * Finds customers by customer type.
     * 
     * @param customerType the customer type (INDIVIDUAL or CORPORATE)
     * @return list of customers with the specified type
     */
    List<Customer> findByCustomerType(Customer.CustomerType customerType);
    
    /**
     * Finds all active customers (excludes deleted customers).
     * 
     * @return list of active customers
     */
    @Query("SELECT c FROM Customer c WHERE c.status != 'DELETED'")
    List<Customer> findAllActive();
    
    /**
     * Finds all active customers with pagination (excludes deleted customers).
     * 
     * @param pageable pagination parameters
     * @return page of active customers
     */
    @Query("SELECT c FROM Customer c WHERE c.status != 'DELETED'")
    Page<Customer> findAllActive(Pageable pageable);
    
    /**
     * Searches customers by keyword in name, email, or company name.
     * Only searches among non-deleted customers.
     * 
     * @param keyword the search keyword
     * @param pageable pagination parameters
     * @return page of customers matching the keyword
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "c.status != 'DELETED' AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Customer> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * Finds customers with optional filters.
     * Only searches among non-deleted customers.
     * 
     * @param customerType optional customer type filter
     * @param status optional status filter
     * @param startDate optional start date filter for creation date
     * @param endDate optional end date filter for creation date
     * @param pageable pagination parameters
     * @return page of filtered customers
     */
    @Query("SELECT c FROM Customer c WHERE " +
           "c.status != 'DELETED' AND " +
           "(:customerType IS NULL OR c.customerType = :customerType) AND " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:startDate IS NULL OR c.createdAt >= :startDate) AND " +
           "(:endDate IS NULL OR c.createdAt <= :endDate)")
    Page<Customer> findCustomersWithFilters(@Param("customerType") Customer.CustomerType customerType,
                                          @Param("status") Customer.CustomerStatus status,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);
    
    /**
     * Counts customers by status.
     * 
     * @param status the customer status
     * @return count of customers with the specified status
     */
    @Query("SELECT COUNT(c) FROM Customer c WHERE c.status = :status")
    Long countByStatus(@Param("status") Customer.CustomerStatus status);
    
    /**
     * Finds customers created between two dates.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of customers created in the date range
     */
    @Query("SELECT c FROM Customer c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate")
    List<Customer> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Checks if a customer with the given email exists.
     * Note: Consider using findByEmail() for better performance when you need the customer object.
     * 
     * @param email the email to check
     * @return true if customer with email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Checks if a customer with the given email exists, excluding a specific customer ID.
     * Note: Consider using findByEmail() for better performance when you need the customer object.
     * 
     * @param email the email to check
     * @param id the customer ID to exclude from the check
     * @return true if another customer with email exists
     */
    boolean existsByEmailAndIdNot(String email, Long id);
}