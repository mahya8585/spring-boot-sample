package com.techbookstore.app.service;

import com.techbookstore.app.entity.Customer;
import com.techbookstore.app.entity.Order;
import com.techbookstore.app.exception.CustomerEmailAlreadyExistsException;
import com.techbookstore.app.exception.CustomerNotFoundException;
import com.techbookstore.app.repository.CustomerRepository;
import com.techbookstore.app.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing customers and customer-related operations.
 */
@Service
@Transactional
public class CustomerService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);
    
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    
    /**
     * Constructor injection for dependencies.
     */
    public CustomerService(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }
    
    /**
     * Creates a new customer.
     * 
     * @param customer the customer to create
     * @return the created customer
     * @throws CustomerEmailAlreadyExistsException if email already exists
     */
    public Customer createCustomer(Customer customer) {
        logger.info("Creating new customer with email: {}", customer.getEmail());
        
        // Check if email already exists (optimized single query)
        Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
        if (existingCustomer.isPresent()) {
            throw new CustomerEmailAlreadyExistsException(customer.getEmail());
        }
        
        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        Customer savedCustomer = customerRepository.save(customer);
        
        logger.info("Created customer with ID: {}", savedCustomer.getId());
        return savedCustomer;
    }
    
    /**
     * Updates an existing customer.
     * 
     * @param id the customer ID
     * @param customerData the updated customer data
     * @return the updated customer
     * @throws CustomerNotFoundException if customer is not found
     * @throws CustomerEmailAlreadyExistsException if email already exists for another customer
     */
    public Customer updateCustomer(Long id, Customer customerData) {
        logger.info("Updating customer with ID: {}", id);
        
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        
        // Check if email already exists for another customer (optimized single query)
        if (!existingCustomer.getEmail().equals(customerData.getEmail())) {
            Optional<Customer> duplicateEmailCustomer = customerRepository.findByEmail(customerData.getEmail());
            if (duplicateEmailCustomer.isPresent() && !duplicateEmailCustomer.get().getId().equals(id)) {
                throw new CustomerEmailAlreadyExistsException(customerData.getEmail());
            }
        }
        
        // Update fields
        existingCustomer.setCustomerType(customerData.getCustomerType());
        existingCustomer.setName(customerData.getName());
        existingCustomer.setNameKana(customerData.getNameKana());
        existingCustomer.setEmail(customerData.getEmail());
        existingCustomer.setPhone(customerData.getPhone());
        existingCustomer.setBirthDate(customerData.getBirthDate());
        existingCustomer.setGender(customerData.getGender());
        existingCustomer.setOccupation(customerData.getOccupation());
        existingCustomer.setCompanyName(customerData.getCompanyName());
        existingCustomer.setDepartment(customerData.getDepartment());
        existingCustomer.setPostalCode(customerData.getPostalCode());
        existingCustomer.setAddress(customerData.getAddress());
        existingCustomer.setNotes(customerData.getNotes());
        existingCustomer.setUpdatedAt(LocalDateTime.now());
        
        Customer updatedCustomer = customerRepository.save(existingCustomer);
        logger.info("Updated customer with ID: {}", updatedCustomer.getId());
        return updatedCustomer;
    }
    
    /**
     * Retrieves a customer by ID.
     * 
     * @param id the customer ID
     * @return the customer
     * @throws CustomerNotFoundException if customer is not found
     */
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
    
    /**
     * Retrieves a customer by email.
     * 
     * @param email the customer email
     * @return the customer
     * @throws CustomerNotFoundException if customer is not found
     */
    @Transactional(readOnly = true)
    public Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new CustomerNotFoundException("email", email));
    }
    
    /**
     * Retrieves all active customers with pagination.
     * 
     * @param pageable the pagination parameters
     * @return page of customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> getAllCustomers(Pageable pageable) {
        return customerRepository.findAllActive(pageable);
    }
    
    /**
     * Searches customers by keyword with pagination.
     * 
     * @param keyword the search keyword
     * @param pageable the pagination parameters
     * @return page of customers matching the keyword
     */
    @Transactional(readOnly = true)
    public Page<Customer> searchCustomers(String keyword, Pageable pageable) {
        return customerRepository.findByKeyword(keyword, pageable);
    }
    
    /**
     * Searches customers with filters.
     * 
     * @param customerType the customer type filter
     * @param status the customer status filter
     * @param startDate the start date filter
     * @param endDate the end date filter
     * @param pageable the pagination parameters
     * @return page of filtered customers
     */
    @Transactional(readOnly = true)
    public Page<Customer> findCustomersWithFilters(Customer.CustomerType customerType,
                                                   Customer.CustomerStatus status,
                                                   LocalDateTime startDate,
                                                   LocalDateTime endDate,
                                                   Pageable pageable) {
        return customerRepository.findCustomersWithFilters(customerType, status, startDate, endDate, pageable);
    }
    
    /**
     * Deletes a customer (logical delete).
     * 
     * @param id the customer ID
     * @throws CustomerNotFoundException if customer is not found
     */
    public void deleteCustomer(Long id) {
        logger.info("Deleting customer with ID: {}", id);
        
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));
        
        customer.setStatus(Customer.CustomerStatus.DELETED);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
        
        logger.info("Deleted customer with ID: {}", id);
    }
    
    /**
     * Retrieves all orders for a specific customer.
     * 
     * @param customerId the customer ID
     * @return list of orders
     */
    @Transactional(readOnly = true)
    public List<Order> getCustomerOrders(Long customerId) {
        // Verify customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        return orderRepository.findByCustomerId(customerId);
    }
    
    /**
     * Gets the count of customers by status.
     * 
     * @param status the customer status
     * @return count of customers
     */
    @Transactional(readOnly = true)
    public Long getCustomerCountByStatus(Customer.CustomerStatus status) {
        return customerRepository.countByStatus(status);
    }
    
    /**
     * Checks if a customer email exists.
     * 
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return customerRepository.existsByEmail(email);
    }
}