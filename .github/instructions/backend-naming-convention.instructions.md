# Backend Naming Convention Instructions - Spring Boot 2.3 + Java 8

## 🎯 Purpose
This document establishes **mandatory naming conventions** for the TechBookStore Backend application (Spring Boot 2.3.12 + Java 8). Consistent naming conventions improve code readability, maintainability, and team collaboration while supporting the educational objectives of the modernization workshop.

## 📋 General Naming Principles

### 1. Core Naming Rules (MANDATORY)
```java
// Use PascalCase for classes, interfaces, enums
public class BookController { }
public interface BookRepository { }
public enum TechLevel { }

// Use camelCase for methods, variables, parameters
public ResponseEntity<BookDto> getBookById(Long bookId) { }
private String titleEn;
private BookService bookService;

// Use SCREAMING_SNAKE_CASE for constants
public static final String DEFAULT_SORT_FIELD = "id";
public static final int MAX_PAGE_SIZE = 100;

// Use snake_case for database columns and tables
@Table(name = "books")
@Column(name = "title_en")
@Column(name = "publication_date")
```

### 2. Package Naming Structure (FIXED)
```
com.techbookstore.app/           # Root package (NEVER change)
├── config/                      # Configuration classes
├── controller/                  # REST API controllers
├── dto/                        # Data Transfer Objects
├── entity/                     # JPA entities
├── exception/                  # Custom exceptions
├── repository/                 # Data access repositories
└── service/                    # Business logic services
```

## 🏗️ Layer-Specific Naming Conventions

### 1. Controller Layer Naming

#### Class Naming Pattern
```java
// MANDATORY PATTERN: [Domain]Controller
@RestController
@RequestMapping("/api/v1/books")
public class BookController { }                    // ✅ CORRECT

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController { }                // ✅ CORRECT

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController { }               // ✅ CORRECT

// AVOID THESE PATTERNS
public class BooksController { }                   // ❌ WRONG: Plural
public class BookRestController { }               // ❌ WRONG: Too verbose
public class BookAPI { }                          // ❌ WRONG: Wrong suffix
```

#### Method Naming Pattern
```java
@RestController
public class BookController {

    // GET Collection: get[Domain]s() or getAll[Domain]s()
    @GetMapping
    public ResponseEntity<Page<BookDto>> getAllBooks() { }      // ✅ CORRECT

    @GetMapping
    public ResponseEntity<Page<BookDto>> getBooks() { }         // ✅ ACCEPTABLE

    // GET Single: get[Domain]ById()
    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById() { }            // ✅ CORRECT

    // GET By Field: get[Domain]By[Field]()
    @GetMapping("/isbn/{isbn13}")
    public ResponseEntity<BookDto> getBookByIsbn() { }          // ✅ CORRECT

    // POST Create: create[Domain]()
    @PostMapping
    public ResponseEntity<BookDto> createBook() { }             // ✅ CORRECT

    // PUT Update: update[Domain]()
    @PutMapping("/{id}")
    public ResponseEntity<BookDto> updateBook() { }             // ✅ CORRECT

    // DELETE Remove: delete[Domain]() or remove[Domain]()
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook() { }               // ✅ CORRECT
}
```

#### URL Path Naming
```java
// MANDATORY PATTERN: /api/v1/[resource-plural]
@RequestMapping("/api/v1/books")                   // ✅ CORRECT
@RequestMapping("/api/v1/customers")               // ✅ CORRECT
@RequestMapping("/api/v1/inventory")               // ✅ CORRECT (uncountable)
@RequestMapping("/api/v1/tech-trends")             // ✅ CORRECT (kebab-case for compound)

// AVOID THESE PATTERNS
@RequestMapping("/books")                          // ❌ WRONG: Missing version
@RequestMapping("/api/Book")                       // ❌ WRONG: PascalCase
@RequestMapping("/api/v1/bookItems")              // ❌ WRONG: camelCase
```

#### Parameter Naming
```java
// Path Parameters: use camelCase
@GetMapping("/{bookId}")
public ResponseEntity<BookDto> getBookById(@PathVariable Long bookId) { }

// Query Parameters: use camelCase
@GetMapping
public ResponseEntity<Page<BookDto>> getAllBooks(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "10") int size,
    @RequestParam(defaultValue = "id") String sortBy,        // ✅ CORRECT
    @RequestParam(defaultValue = "asc") String sortDir,      // ✅ CORRECT
    @RequestParam(required = false) String keyword) { }      // ✅ CORRECT
```

### 2. Entity Layer Naming

#### Entity Class Naming
```java
// MANDATORY PATTERN: Singular nouns in PascalCase
@Entity
@Table(name = "books")
public class Book { }                              // ✅ CORRECT

@Entity
@Table(name = "customers")
public class Customer { }                          // ✅ CORRECT

@Entity
@Table(name = "inventory_transactions")
public class InventoryTransaction { }              // ✅ CORRECT

// AVOID THESE PATTERNS
public class Books { }                             // ❌ WRONG: Plural
public class book { }                              // ❌ WRONG: lowercase
public class BookEntity { }                        // ❌ WRONG: Redundant suffix
```

#### Table and Column Naming
```java
@Entity
@Table(name = "books")                             // snake_case, plural
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                               // Simple fields: camelCase

    @Column(name = "isbn_13")                      // Database: snake_case
    private String isbn13;                         // Java: camelCase (without underscore)

    @Column(name = "title_en")                     // Database: snake_case
    private String titleEn;                        // Java: camelCase

    @Column(name = "publication_date")             // Database: snake_case
    private LocalDate publicationDate;             // Java: camelCase

    @Column(name = "list_price")                   // Database: snake_case
    private BigDecimal listPrice;                  // Java: camelCase

    @Column(name = "sample_code_url")              // Database: snake_case
    private String sampleCodeUrl;                  // Java: camelCase
}
```

#### Relationship Naming
```java
@Entity
public class Book {

    // Many-to-One: reference entity name
    @ManyToOne
    @JoinColumn(name = "publisher_id")             // Foreign key: [table]_id
    private Publisher publisher;                   // Field: entity name

    // One-to-Many: plural of related entity
    @OneToMany(mappedBy = "book")
    private List<OrderItem> orderItems;           // Plural of related entity

    // Many-to-Many: plural of related entity
    @ManyToMany
    @JoinTable(
        name = "book_categories",                  // Join table: [entity1]_[entity2]
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<Category> categories;             // Plural of related entity
}
```

#### Enum Naming
```java
// Enum Class: PascalCase with descriptive name
public enum TechLevel {
    BEGINNER,                                      // ✅ CORRECT: SCREAMING_SNAKE_CASE
    INTERMEDIATE,                                  // ✅ CORRECT
    ADVANCED                                       // ✅ CORRECT
}

public enum TransactionType {
    STOCK_IN,                                      // ✅ CORRECT: Descriptive
    STOCK_OUT,                                     // ✅ CORRECT
    ADJUSTMENT,                                    // ✅ CORRECT
    TRANSFER                                       // ✅ CORRECT
}

// Usage in Entity
@Enumerated(EnumType.STRING)                       // ALWAYS use STRING
@Column(name = "tech_level")                       // Database: snake_case
private TechLevel level;                           // Field: descriptive name
```

### 3. Repository Layer Naming

#### Repository Interface Naming
```java
// MANDATORY PATTERN: [Entity]Repository
public interface BookRepository extends JpaRepository<Book, Long> { }              // ✅ CORRECT
public interface CustomerRepository extends JpaRepository<Customer, Long> { }      // ✅ CORRECT
public interface InventoryRepository extends JpaRepository<Inventory, Long> { }    // ✅ CORRECT

// AVOID THESE PATTERNS
public interface BookDao { }                       // ❌ WRONG: DAO pattern
public interface BookDataAccess { }               // ❌ WRONG: Too verbose
public interface IBookRepository { }              // ❌ WRONG: Hungarian notation
```

#### Query Method Naming
```java
public interface BookRepository extends JpaRepository<Book, Long> {

    // SPRING DATA NAMING CONVENTIONS (MANDATORY)

    // findBy[Property]
    Optional<Book> findByIsbn13(String isbn13);                     // ✅ CORRECT
    List<Book> findByTitle(String title);                           // ✅ CORRECT
    Page<Book> findByLevel(TechLevel level, Pageable pageable);     // ✅ CORRECT

    // findBy[Property1]And[Property2]
    List<Book> findByTitleAndLevel(String title, TechLevel level);  // ✅ CORRECT

    // findBy[Property]Or[Property2]
    List<Book> findByTitleOrTitleEn(String title, String titleEn);  // ✅ CORRECT

    // findBy[Property]Containing (for LIKE queries)
    List<Book> findByTitleContaining(String titlePart);             // ✅ CORRECT
    List<Book> findByTitleContainingIgnoreCase(String titlePart);   // ✅ CORRECT

    // findBy[Property]GreaterThan/LessThan
    List<Book> findByPublicationDateAfter(LocalDate date);          // ✅ CORRECT
    List<Book> findByListPriceGreaterThan(BigDecimal price);        // ✅ CORRECT

    // Custom @Query methods: descriptive names
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Book> findByKeyword(@Param("keyword") String keyword, Pageable pageable);  // ✅ CORRECT

    @Query("SELECT b FROM Book b WHERE b.publisher.id = :publisherId")
    Page<Book> findByPublisherId(@Param("publisherId") Long publisherId, Pageable pageable);  // ✅ CORRECT
}
```

### 4. Service Layer Naming

#### Service Class Naming
```java
// MANDATORY PATTERN: [Domain]Service
@Service
public class BookService { }                       // ✅ CORRECT

@Service
public class CustomerService { }                   // ✅ CORRECT

@Service
public class InventoryService { }                  // ✅ CORRECT

// Complex business services: descriptive names
@Service
public class DemandForecastService { }             // ✅ CORRECT

@Service
public class TechTrendAnalysisService { }          // ✅ CORRECT

@Service
public class OptimalStockCalculatorService { }     // ✅ CORRECT

// AVOID THESE PATTERNS
public class BookBusinessLogic { }                 // ❌ WRONG: Too verbose
public class BookServiceImpl { }                  // ❌ WRONG: Impl suffix unnecessary
public class BookManager { }                      // ❌ WRONG: Manager suffix
```

#### Service Method Naming
```java
@Service
public class BookService {

    // CRUD Operations: Standard naming
    public List<Book> findAllBooks() { }                           // ✅ CORRECT
    public Optional<Book> findBookById(Long id) { }                // ✅ CORRECT
    public Book saveBook(Book book) { }                            // ✅ CORRECT
    public Book updateBook(Book book) { }                          // ✅ CORRECT
    public void deleteBookById(Long id) { }                        // ✅ CORRECT

    // Business operations: Descriptive verbs
    public List<Book> searchBooksByKeyword(String keyword) { }     // ✅ CORRECT
    public Page<Book> findBooksByCategory(Long categoryId, Pageable pageable) { }  // ✅ CORRECT
    public List<Book> getRecommendedBooks(Long customerId) { }     // ✅ CORRECT
    public boolean isBookAvailable(Long bookId) { }                // ✅ CORRECT
    public void reserveBook(Long bookId, Long customerId) { }      // ✅ CORRECT
    public BigDecimal calculateDiscountPrice(Long bookId, String discountCode) { }  // ✅ CORRECT

    // Boolean methods: is/has/can prefix
    public boolean isBookInStock(Long bookId) { }                  // ✅ CORRECT
    public boolean hasValidIsbn(String isbn13) { }                 // ✅ CORRECT
    public boolean canOrderBook(Long bookId, int quantity) { }     // ✅ CORRECT
}
```

### 5. DTO Layer Naming

#### DTO Class Naming
```java
// MANDATORY PATTERN: [Domain]Dto
public class BookDto { }                           // ✅ CORRECT
public class CustomerDto { }                       // ✅ CORRECT
public class OrderItemDto { }                      // ✅ CORRECT

// Request/Response DTOs: Descriptive names
public class CreateBookRequest { }                 // ✅ CORRECT
public class UpdateCustomerRequest { }             // ✅ CORRECT
public class BookSearchResponse { }                // ✅ CORRECT
public class InventoryReportDto { }                // ✅ CORRECT

// Complex analysis DTOs
public class DemandForecastResult { }              // ✅ CORRECT
public class TechTrendAlertDto { }                 // ✅ CORRECT
public class ABCXYZAnalysisResult { }              // ✅ CORRECT

// AVOID THESE PATTERNS
public class BookTransferObject { }               // ❌ WRONG: Too verbose
public class BookVO { }                           // ❌ WRONG: Abbreviation
public class BookData { }                         // ❌ WRONG: Generic name
```

#### DTO Field Naming
```java
public class BookDto {

    // Simple fields: camelCase (same as entity)
    private Long id;                               // ✅ CORRECT
    private String isbn13;                         // ✅ CORRECT
    private String title;                          // ✅ CORRECT
    private String titleEn;                        // ✅ CORRECT
    private BigDecimal listPrice;                  // ✅ CORRECT
    private LocalDate publicationDate;             // ✅ CORRECT

    // Related entities: use descriptive names
    private String publisherName;                  // ✅ CORRECT (flattened)
    private List<String> categoryNames;            // ✅ CORRECT (flattened)
    private AuthorDto primaryAuthor;               // ✅ CORRECT (nested DTO)

    // Computed fields: descriptive names
    private boolean inStock;                       // ✅ CORRECT
    private int availableQuantity;                 // ✅ CORRECT
    private BigDecimal discountedPrice;            // ✅ CORRECT
}
```

### 6. Exception Layer Naming

#### Exception Class Naming
```java
// MANDATORY PATTERN: [Description]Exception
public class ResourceNotFoundException extends RuntimeException { }     // ✅ CORRECT
public class ValidationException extends RuntimeException { }           // ✅ CORRECT
public class BusinessException extends RuntimeException { }             // ✅ CORRECT

// Domain-specific exceptions
public class BookNotFoundException extends ResourceNotFoundException { } // ✅ CORRECT
public class InvalidIsbnException extends ValidationException { }       // ✅ CORRECT
public class InsufficientStockException extends BusinessException { }   // ✅ CORRECT

// AVOID THESE PATTERNS
public class BookNotFound { }                      // ❌ WRONG: Missing Exception suffix
public class BookException { }                     // ❌ WRONG: Too generic
public class BookError { }                         // ❌ WRONG: Wrong suffix
```

### 7. Configuration Layer Naming

#### Configuration Class Naming
```java
// MANDATORY PATTERN: [Purpose]Config
@Configuration
public class SecurityConfig { }                    // ✅ CORRECT

@Configuration
public class SwaggerConfig { }                     // ✅ CORRECT

@Configuration
public class RedisConfig { }                       // ✅ CORRECT

@Configuration
public class DatabaseConfig { }                    // ✅ CORRECT

// AVOID THESE PATTERNS
public class SecurityConfiguration { }             // ❌ WRONG: Too verbose
public class ConfigSecurity { }                   // ❌ WRONG: Wrong order
public class Security { }                         // ❌ WRONG: Missing suffix
```

## 🗂️ File and Package Naming

### 1. File Naming Rules
```bash
# Java files: PascalCase.java
BookController.java                               # ✅ CORRECT
CustomerService.java                             # ✅ CORRECT
InventoryRepository.java                         # ✅ CORRECT

# Configuration files: kebab-case
application.yml                                  # ✅ CORRECT
application-dev.yml                              # ✅ CORRECT
application-prod.yml                             # ✅ CORRECT

# AVOID THESE PATTERNS
bookController.java                              # ❌ WRONG: camelCase
book_controller.java                             # ❌ WRONG: snake_case
BookController.JAVA                              # ❌ WRONG: Uppercase extension
```

### 2. Test Class Naming
```java
// Unit Tests: [ClassUnderTest]Test
class BookServiceTest { }                         // ✅ CORRECT
class CustomerRepositoryTest { }                  // ✅ CORRECT
class InventoryControllerTest { }                 // ✅ CORRECT

// Integration Tests: [ClassUnderTest]IntegrationTest
@SpringBootTest
class BookControllerIntegrationTest { }           // ✅ CORRECT

@DataJpaTest
class BookRepositoryIntegrationTest { }           // ✅ CORRECT

// AVOID THESE PATTERNS
class TestBookService { }                        // ❌ WRONG: Test prefix
class BookServiceTestCase { }                    // ❌ WRONG: TestCase suffix
class BookServiceTests { }                       // ❌ WRONG: Tests plural
```

### 3. Test Method Naming
```java
class BookServiceTest {

    // PATTERN: should[ExpectedBehavior]_when[StateUnderTest]
    @Test
    void shouldReturnBook_whenValidIdProvided() { }               // ✅ CORRECT

    @Test
    void shouldThrowException_whenBookNotFound() { }              // ✅ CORRECT

    @Test
    void shouldReturnEmptyList_whenNoBooksExist() { }             // ✅ CORRECT

    // ALTERNATIVE PATTERN: [methodName]_should[ExpectedBehavior]_when[StateUnderTest]
    @Test
    void findById_shouldReturnBook_whenValidIdProvided() { }      // ✅ ACCEPTABLE

    // AVOID THESE PATTERNS
    @Test
    void testFindById() { }                                       // ❌ WRONG: Test prefix

    @Test
    void findByIdTest() { }                                       // ❌ WRONG: Test suffix
}
```

## 🔧 Variable and Constant Naming

### 1. Local Variables
```java
public class BookService {

    public List<BookDto> searchBooks(String keyword) {
        // Local variables: camelCase, descriptive
        List<Book> foundBooks = bookRepository.findByKeyword(keyword);  // ✅ CORRECT
        List<BookDto> bookDtos = new ArrayList<>();                     // ✅ CORRECT

        // Loop variables: descriptive names
        for (Book book : foundBooks) {                                  // ✅ CORRECT
            BookDto bookDto = new BookDto(book);                        // ✅ CORRECT
            bookDtos.add(bookDto);
        }

        return bookDtos;
    }
}
```

### 2. Instance Variables (Fields)
```java
@Service
public class BookService {

    // Dependency injection: camelCase
    @Autowired
    private BookRepository bookRepository;                              // ✅ CORRECT

    @Autowired
    private InventoryService inventoryService;                          // ✅ CORRECT

    // Configuration properties: camelCase
    @Value("${app.book.max-title-length}")
    private int maxTitleLength;                                         // ✅ CORRECT
}
```

### 3. Constants
```java
public class BookController {

    // Class constants: SCREAMING_SNAKE_CASE
    private static final String DEFAULT_SORT_FIELD = "id";             // ✅ CORRECT
    private static final String DEFAULT_SORT_DIRECTION = "asc";         // ✅ CORRECT
    private static final int DEFAULT_PAGE_SIZE = 10;                    // ✅ CORRECT
    private static final int MAX_PAGE_SIZE = 100;                       // ✅ CORRECT

    // Public constants: SCREAMING_SNAKE_CASE
    public static final String API_VERSION = "v1";                     // ✅ CORRECT
    public static final String BASE_PATH = "/api/" + API_VERSION;       // ✅ CORRECT
}
```

## 📊 Database Naming Conventions

### 1. Table Names
```sql
-- MANDATORY PATTERN: snake_case, plural nouns
books                                              -- ✅ CORRECT
customers                                          -- ✅ CORRECT
inventory_transactions                             -- ✅ CORRECT
order_items                                        -- ✅ CORRECT
tech_trend_analyses                                -- ✅ CORRECT

-- Junction tables: [table1]_[table2] (alphabetical order)
book_categories                                    -- ✅ CORRECT
author_books                                       -- ✅ CORRECT (alphabetical)

-- AVOID THESE PATTERNS
Books                                              -- ❌ WRONG: PascalCase
book                                               -- ❌ WRONG: Singular
bookCategories                                     -- ❌ WRONG: camelCase
```

### 2. Column Names
```sql
-- MANDATORY PATTERN: snake_case
id                                                 -- ✅ CORRECT (primary key)
isbn_13                                            -- ✅ CORRECT
title_en                                           -- ✅ CORRECT
publication_date                                   -- ✅ CORRECT
list_price                                         -- ✅ CORRECT
sample_code_url                                    -- ✅ CORRECT

-- Foreign keys: [referenced_table]_id
publisher_id                                       -- ✅ CORRECT
category_id                                        -- ✅ CORRECT
customer_id                                        -- ✅ CORRECT

-- Boolean columns: is_[adjective] or has_[noun]
is_active                                          -- ✅ CORRECT
is_available                                       -- ✅ CORRECT
has_sample_code                                    -- ✅ CORRECT

-- AVOID THESE PATTERNS
publisherId                                        -- ❌ WRONG: camelCase
ISBN13                                             -- ❌ WRONG: SCREAMING_SNAKE_CASE
titleEN                                            -- ❌ WRONG: Mixed case
```

## 📝 JSON API Naming

### 1. JSON Property Names
```json
{
  "id": 1,
  "isbn13": "9784798142470",
  "title": "Spring Boot実践入門",
  "titleEn": "Spring Boot Practical Guide",
  "publicationDate": "2022-03-15",
  "listPrice": 3200.00,
  "sellingPrice": 2880.00,
  "publisherName": "技術評論社",
  "techLevel": "INTERMEDIATE",
  "sampleCodeUrl": "https://github.com/example/sample-code",
  "inStock": true,
  "availableQuantity": 15
}
```

### 2. API Error Response Naming
```json
{
  "errorCode": "BOOK_NOT_FOUND",
  "errorMessage": "Book with ID 999 not found",
  "timestamp": "2025-08-29T10:30:00Z",
  "path": "/api/v1/books/999"
}
```

## ✅ Naming Convention Checklist

### Before Code Commit (MANDATORY REVIEW)

#### ✅ Class Names
- [ ] Controllers end with `Controller`
- [ ] Services end with `Service`
- [ ] Repositories end with `Repository`
- [ ] DTOs end with `Dto` or descriptive suffix (`Request`, `Response`, `Result`)
- [ ] Entities are singular nouns
- [ ] Exceptions end with `Exception`
- [ ] Configuration classes end with `Config`

#### ✅ Method Names
- [ ] Use camelCase
- [ ] Start with verb (get, find, create, update, delete, calculate, etc.)
- [ ] Boolean methods start with is/has/can
- [ ] Repository methods follow Spring Data conventions

#### ✅ Variable Names
- [ ] Use camelCase for fields and local variables
- [ ] Use SCREAMING_SNAKE_CASE for constants
- [ ] Use descriptive names (avoid abbreviations)

#### ✅ Database Names
- [ ] Table names are snake_case and plural
- [ ] Column names are snake_case
- [ ] Foreign keys follow [table]_id pattern
- [ ] Boolean columns start with is_ or has_

#### ✅ File and Package Names
- [ ] Java files use PascalCase
- [ ] Packages use lowercase
- [ ] Configuration files use kebab-case
- [ ] Test classes end with Test or IntegrationTest

## 🚨 Common Naming Mistakes to Avoid

### 1. Inconsistent Casing
```java
// ❌ WRONG: Inconsistent casing
public class bookController { }                    // Should be BookController
private String ISBN13;                             // Should be isbn13
private static final String baseUrl = "...";      // Should be BASE_URL

// ✅ CORRECT: Consistent casing
public class BookController { }
private String isbn13;
private static final String BASE_URL = "...";
```

### 2. Abbreviations and Acronyms
```java
// ❌ WRONG: Unclear abbreviations
private String desc;                               // Use description
private int qty;                                   // Use quantity
private CustomerRepo custRepo;                     // Use customerRepository

// ✅ CORRECT: Full descriptive names
private String description;
private int quantity;
private CustomerRepository customerRepository;
```

### 3. Hungarian Notation
```java
// ❌ WRONG: Hungarian notation (don't use)
private String strTitle;
private int intQuantity;
private boolean bActive;

// ✅ CORRECT: Clean naming
private String title;
private int quantity;
private boolean active;
```

These naming conventions ensure consistency across the TechBookStore backend application and support the educational objectives of demonstrating professional Java development practices in a Spring Boot 2.3 + Java 8 environment.
