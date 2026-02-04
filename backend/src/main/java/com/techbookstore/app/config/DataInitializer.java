package com.techbookstore.app.config;

import com.techbookstore.app.entity.*;
import com.techbookstore.app.entity.Book.TechLevel;
import com.techbookstore.app.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private PublisherRepository publisherRepository;
    
    @Autowired
    private InventoryRepository inventoryRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Only initialize if database is empty (both books and publishers)
        // This ensures compatibility with data.sql
        if (bookRepository.count() > 0 || publisherRepository.count() > 0) {
            System.out.println("Data already exists. Skipping DataInitializer.");
            return;
        }
        
        System.out.println("Initializing data via DataInitializer...");
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Create sample publishers
        Publisher publisher1 = new Publisher("技術評論社");
        Publisher publisher2 = new Publisher("オライリー・ジャパン");
        publisher1 = publisherRepository.save(publisher1);
        publisher2 = publisherRepository.save(publisher2);
        
        // Create sample authors
        Author author1 = new Author("山田太郎");
        Author author2 = new Author("佐藤花子");
        authorRepository.save(author1);
        authorRepository.save(author2);
        
        // Create sample books
        Book book1 = new Book("9784774187123", "Javaプログラミング入門");
        book1.setPublisher(publisher1);
        book1.setPublicationDate(LocalDate.of(2023, 1, 15));
        book1.setEdition(1);
        book1.setListPrice(new BigDecimal("3200"));
        book1.setSellingPrice(new BigDecimal("2880"));
        book1.setPages(420);
        book1.setLevel(TechLevel.BEGINNER);
        book1 = bookRepository.save(book1);
        
        Book book2 = new Book("9784873119465", "Spring Boot実践ガイド");
        book2.setPublisher(publisher2);
        book2.setPublicationDate(LocalDate.of(2023, 3, 10));
        book2.setEdition(2);
        book2.setListPrice(new BigDecimal("4500"));
        book2.setSellingPrice(new BigDecimal("4050"));
        book2.setPages(580);
        book2.setLevel(TechLevel.INTERMEDIATE);
        book2 = bookRepository.save(book2);
        
        Book book3 = new Book("9784798167312", "React開発現場のテクニック");
        book3.setPublisher(publisher1);
        book3.setPublicationDate(LocalDate.of(2023, 2, 20));
        book3.setEdition(1);
        book3.setListPrice(new BigDecimal("3800"));
        book3.setSellingPrice(new BigDecimal("3420"));
        book3.setPages(350);
        book3.setLevel(TechLevel.ADVANCED);
        book3 = bookRepository.save(book3);
        
        // Create sample inventory
        Inventory inventory1 = new Inventory(book1);
        inventory1.setStoreStock(25);
        inventory1.setWarehouseStock(100);
        inventory1.setReorderPoint(5);
        inventory1.setLastReceivedDate(LocalDate.now().minusDays(10));
        inventoryRepository.save(inventory1);
        
        Inventory inventory2 = new Inventory(book2);
        inventory2.setStoreStock(15);
        inventory2.setWarehouseStock(80);
        inventory2.setReorderPoint(3);
        inventory2.setLastReceivedDate(LocalDate.now().minusDays(5));
        inventoryRepository.save(inventory2);
        
        Inventory inventory3 = new Inventory(book3);
        inventory3.setStoreStock(30);
        inventory3.setWarehouseStock(60);
        inventory3.setReorderPoint(8);
        inventory3.setLastReceivedDate(LocalDate.now().minusDays(3));
        inventoryRepository.save(inventory3);
        
        // Create sample orders
        Order order1 = new Order(Order.OrderType.ONLINE, Order.PaymentMethod.CREDIT_CARD);
        order1.setOrderNumber("ORD-20250125-0001");
        order1.setCustomerId(1L);
        order1.setOrderDate(LocalDateTime.now().minusDays(3));
        order1.setStatus(Order.OrderStatus.DELIVERED);
        order1.setConfirmedDate(LocalDateTime.now().minusDays(2));
        order1.setShippedDate(LocalDateTime.now().minusDays(1));
        order1.setDeliveredDate(LocalDateTime.now());
        order1.setNotes("初回購入特典適用");
        
        OrderItem orderItem1 = new OrderItem(order1, book1, 2, book1.getSellingPrice());
        OrderItem orderItem2 = new OrderItem(order1, book2, 1, book2.getSellingPrice());
        
        order1.getOrderItems().add(orderItem1);
        order1.getOrderItems().add(orderItem2);
        order1.setTotalAmount(orderItem1.getTotalPrice().add(orderItem2.getTotalPrice()));
        
        orderRepository.save(order1);
        
        Order order2 = new Order(Order.OrderType.WALK_IN, Order.PaymentMethod.CASH);
        order2.setOrderNumber("ORD-20250125-0002");
        order2.setOrderDate(LocalDateTime.now().minusHours(6));
        order2.setStatus(Order.OrderStatus.PENDING);
        order2.setNotes("店頭での現金購入");
        
        OrderItem orderItem3 = new OrderItem(order2, book3, 1, book3.getSellingPrice());
        order2.getOrderItems().add(orderItem3);
        order2.setTotalAmount(orderItem3.getTotalPrice());
        
        orderRepository.save(order2);
        
        Order order3 = new Order(Order.OrderType.PHONE, Order.PaymentMethod.BANK_TRANSFER);
        order3.setOrderNumber("ORD-20250125-0003");
        order3.setCustomerId(2L);
        order3.setOrderDate(LocalDateTime.now().minusHours(2));
        order3.setStatus(Order.OrderStatus.CONFIRMED);
        order3.setConfirmedDate(LocalDateTime.now().minusHours(1));
        order3.setNotes("法人での一括購入");
        
        OrderItem orderItem4 = new OrderItem(order3, book1, 5, book1.getSellingPrice());
        OrderItem orderItem5 = new OrderItem(order3, book2, 3, book2.getSellingPrice());
        OrderItem orderItem6 = new OrderItem(order3, book3, 2, book3.getSellingPrice());
        
        order3.getOrderItems().add(orderItem4);
        order3.getOrderItems().add(orderItem5);
        order3.getOrderItems().add(orderItem6);
        order3.setTotalAmount(orderItem4.getTotalPrice()
                .add(orderItem5.getTotalPrice())
                .add(orderItem6.getTotalPrice()));
        
        orderRepository.save(order3);
    }
}