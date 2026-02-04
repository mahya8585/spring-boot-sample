package com.techbookstore.app.dto;

import com.techbookstore.app.entity.Book;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BookDto {
    private Long id;
    private String isbn13;
    private String title;
    private String titleEn;
    private String publisherName;
    private LocalDate publicationDate;
    private Integer edition;
    private BigDecimal listPrice;
    private BigDecimal sellingPrice;
    private Integer pages;
    private String level;
    private String versionInfo;
    private String sampleCodeUrl;

    // Constructors
    public BookDto() {}

    public BookDto(Book book) {
        this.id = book.getId();
        this.isbn13 = book.getIsbn13();
        this.title = book.getTitle();
        this.titleEn = book.getTitleEn();
        this.publisherName = book.getPublisher() != null ? book.getPublisher().getName() : null;
        this.publicationDate = book.getPublicationDate();
        this.edition = book.getEdition();
        this.listPrice = book.getListPrice();
        this.sellingPrice = book.getSellingPrice();
        this.pages = book.getPages();
        this.level = book.getLevel() != null ? book.getLevel().toString() : null;
        this.versionInfo = book.getVersionInfo();
        this.sampleCodeUrl = book.getSampleCodeUrl();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn13() {
        return isbn13;
    }

    public void setIsbn13(String isbn13) {
        this.isbn13 = isbn13;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public LocalDate getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(LocalDate publicationDate) {
        this.publicationDate = publicationDate;
    }

    public Integer getEdition() {
        return edition;
    }

    public void setEdition(Integer edition) {
        this.edition = edition;
    }

    public BigDecimal getListPrice() {
        return listPrice;
    }

    public void setListPrice(BigDecimal listPrice) {
        this.listPrice = listPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public String getSampleCodeUrl() {
        return sampleCodeUrl;
    }

    public void setSampleCodeUrl(String sampleCodeUrl) {
        this.sampleCodeUrl = sampleCodeUrl;
    }
}