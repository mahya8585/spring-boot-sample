package com.techbookstore.app.dto;

import com.techbookstore.app.entity.Customer;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerDto {
    
    private Long id;
    private String customerType;
    private String name;
    private String nameKana;
    private String email;
    private String phone;
    private LocalDate birthDate;
    private String gender;
    private String occupation;
    private String companyName;
    private String department;
    private String postalCode;
    private String address;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public CustomerDto() {}
    
    public CustomerDto(Customer customer) {
        this.id = customer.getId();
        this.customerType = customer.getCustomerType() != null ? customer.getCustomerType().toString() : null;
        this.name = customer.getName();
        this.nameKana = customer.getNameKana();
        this.email = customer.getEmail();
        this.phone = customer.getPhone();
        this.birthDate = customer.getBirthDate();
        this.gender = customer.getGender() != null ? customer.getGender().toString() : null;
        this.occupation = customer.getOccupation();
        this.companyName = customer.getCompanyName();
        this.department = customer.getDepartment();
        this.postalCode = customer.getPostalCode();
        this.address = customer.getAddress();
        this.status = customer.getStatus() != null ? customer.getStatus().toString() : null;
        this.notes = customer.getNotes();
        this.createdAt = customer.getCreatedAt();
        this.updatedAt = customer.getUpdatedAt();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getCustomerType() {
        return customerType;
    }
    
    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getNameKana() {
        return nameKana;
    }
    
    public void setNameKana(String nameKana) {
        this.nameKana = nameKana;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}