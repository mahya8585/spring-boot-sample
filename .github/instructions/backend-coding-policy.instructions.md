# Backend Coding Policy Instructions - Spring Boot 2.3 + Java 8

## 🎯 Purpose
This document provides strict coding guidelines and policies for developing the **TechBookStore Backend** application using Spring Boot 2.3.12.RELEASE and Java 8. These guidelines ensure code consistency, maintainability, and alignment with legacy system patterns while preparing for future modernization.

## 📋 Technology Stack Constraints

### Fixed Technology Versions
```xml
<properties>
    <java.version>1.8</java.version>
    <spring-boot.version>2.3.12.RELEASE</spring-boot.version>
    <springfox.version>2.9.2</springfox.version>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
</properties>
```

### Package Structure Requirements
```
com.techbookstore.app/
├── TechBookStoreApplication.java    # Main Spring Boot application
├── config/                          # Configuration classes only
├── controller/                      # REST controllers only
├── dto/                            # Data Transfer Objects only
├── entity/                         # JPA entities only
├── exception/                      # Custom exceptions and handlers
├── repository/                     # Spring Data JPA repositories only
└── service/                        # Business logic services only
```

## 🏗️ Architectural Guidelines

### 1. Controller Layer Standards

#### Required Annotations and Structure
```java
@RestController
@RequestMapping("/api/v1/[resource]")
@CrossOrigin(origins = "http://localhost:3000")  // REQUIRED for React frontend
public class [Resource]Controller {

    @Autowired  // LEGACY PATTERN - Required for consistency
    private [Resource]Repository [resource]Repository;

    // Controller methods here
}
```

#### HTTP Method Mapping Standards
```java
// GET Collection - MUST support pagination
@GetMapping
public ResponseEntity<Page<[Resource]Dto>> getAll[Resources](
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy,
        @RequestParam(defaultValue = "asc") String sortDir,
        @RequestParam(required = false) String keyword) {
    // Implementation
}

// GET Single Resource
@GetMapping("/{id}")
public ResponseEntity<[Resource]Dto> get[Resource]ById(@PathVariable Long id) {
    // MUST return 404 for non-existent resources
}

// POST Create
@PostMapping
public ResponseEntity<[Resource]Dto> create[Resource](@RequestBody [Resource] [resource]) {
    // MUST return 201 Created with location header
}

// PUT Update
@PutMapping("/{id}")
public ResponseEntity<[Resource]Dto> update[Resource](@PathVariable Long id, @RequestBody [Resource] [resource]) {
    // MUST return 404 for non-existent resources
}

// DELETE Remove
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete[Resource](@PathVariable Long id) {
    // MUST return 204 No Content or 404 Not Found
}
```

#### Required Error Handling Pattern
```java
@GetMapping("/{id}")
public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
    Optional<Book> book = bookRepository.findById(id);
    if (book.isPresent()) {
        return ResponseEntity.ok(new BookDto(book.get()));
    } else {
        return ResponseEntity.notFound().build();  // REQUIRED pattern
    }
}
```

### 2. Entity Layer Standards

#### JPA Entity Requirements
```java
@Entity
@Table(name = "[table_name]")  // REQUIRED: explicit table naming
public class [EntityName] {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // REQUIRED strategy
    private Long id;

    // Column definitions MUST be explicit
    @Column(nullable = false, length = 255)  // Always specify constraints
    private String requiredField;

    @Column(name = "custom_name")  // Use snake_case for DB columns
    private String customField;

    // Default constructor REQUIRED
    public [EntityName]() {}

    // Convenience constructor RECOMMENDED
    public [EntityName](String requiredField) {
        this.requiredField = requiredField;
    }

    // Standard getters and setters REQUIRED
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ... other getters/setters
}
```

#### Relationship Mapping Standards
```java
// Many-to-One (REQUIRED pattern)
@ManyToOne
@JoinColumn(name = "foreign_key_id")  // EXPLICIT foreign key naming
private ParentEntity parent;

// One-to-Many (if needed)
@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
private List<ChildEntity> children = new ArrayList<>();

// Enum Mapping (REQUIRED pattern)
@Enumerated(EnumType.STRING)  // ALWAYS use STRING for database compatibility
private StatusEnum status;
```

### 3. Repository Layer Standards

#### Required Repository Pattern
```java
@Repository  // REQUIRED annotation
public interface [Resource]Repository extends JpaRepository<[Entity], Long> {

    // Custom query methods MUST follow Spring Data naming conventions
    Optional<[Entity]> findBy[Field]([FieldType] [field]);

    List<[Entity]> findBy[Field]([FieldType] [field]);

    Page<[Entity]> findBy[Field]([FieldType] [field], Pageable pageable);

    // Complex queries MUST use @Query annotation
    @Query("SELECT e FROM [Entity] e WHERE e.field LIKE %:keyword%")
    Page<[Entity]> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Native queries ONLY when JPQL is insufficient
    @Query(value = "SELECT * FROM table_name WHERE condition", nativeQuery = true)
    List<[Entity]> findByNativeQuery();
}
```

### 4. Service Layer Standards

#### Service Class Structure (REQUIRED)
```java
@Service  // REQUIRED annotation
public class [Resource]Service {

    @Autowired  // LEGACY PATTERN - maintain consistency
    private [Resource]Repository [resource]Repository;

    // Business methods MUST follow this pattern
    public List<[Entity]> findAll() {
        return [resource]Repository.findAll();
    }

    public Optional<[Entity]> findById(Long id) {
        return [resource]Repository.findById(id);
    }

    public [Entity] save([Entity] [entity]) {
        // VALIDATION LOGIC HERE if needed
        return [resource]Repository.save([entity]);
    }

    public void deleteById(Long id) {
        [resource]Repository.deleteById(id);
    }

    // Complex business logic methods
    public [ReturnType] performBusinessOperation([Parameters]) {
        // BUSINESS LOGIC IMPLEMENTATION
    }
}
```

### 5. DTO Pattern Standards

#### DTO Class Requirements
```java
public class [Resource]Dto {

    // ALL fields MUST be private
    private Long id;
    private String field1;
    private String field2;

    // Default constructor REQUIRED
    public [Resource]Dto() {}

    // Entity conversion constructor REQUIRED
    public [Resource]Dto([Entity] entity) {
        this.id = entity.getId();
        this.field1 = entity.getField1();
        this.field2 = entity.getField2();
        // Map all necessary fields
    }

    // Standard getters and setters REQUIRED
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // ... other getters/setters
}
```

## 🔧 Configuration Standards

### 1. Application Configuration

#### Required application.yml Structure
```yaml
# application.yml (REQUIRED base configuration)
spring:
  profiles:
    active: dev  # Default to development

# Common configuration across all profiles
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

# Swagger configuration
springfox:
  documentation:
    swagger-ui:
      base-url: /api-docs
```

#### Profile-Specific Configuration
```yaml
# application-dev.yml (REQUIRED)
spring:
  datasource:
    url: jdbc:h2:mem:techbookstore
    driver-class-name: org.h2.Driver
    username: sa
    password:

  h2:
    console:
      enabled: true
      path: /h2-console

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop  # ONLY for development
    show-sql: true

# application-prod.yml (REQUIRED)
spring:
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate  # NEVER create-drop in production
    show-sql: false
```

### 2. Security Configuration

#### Required Security Pattern
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {  // LEGACY PATTERN

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/public/**", "/h2-console/**", "/swagger-ui/**", "/v2/api-docs/**").permitAll()
                .anyRequest().authenticated()
                .and()
            .httpBasic()
                .and()
            .csrf().disable()  // REQUIRED for REST API
            .headers().frameOptions().disable();  // REQUIRED for H2 console
    }
}
```

### 3. Swagger Configuration

#### Required Swagger Setup
```java
@Configuration
@EnableSwagger2  // LEGACY Swagger 2 pattern
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.techbookstore.app.controller"))
                .paths(PathSelectors.regex("/api/.*"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("TechBookStore API")
                .description("技術専門書店在庫管理システム API")
                .version("1.0.0")
                .build();
    }
}
```

## 🧪 Testing Standards

### 1. Unit Testing Requirements

#### Test Class Structure (REQUIRED)
```java
@ExtendWith(MockitoExtension.class)  // JUnit 5 pattern
class [Class]Test {

    @Mock
    private [Dependency] [dependency];

    @InjectMocks
    private [ClassUnderTest] [classUnderTest];

    @Test
    void should[ExpectedBehavior]_when[Condition]() {
        // Given (Arrange)
        // When (Act)
        // Then (Assert)
    }
}
```

#### Repository Testing Pattern
```java
@DataJpaTest
class [Repository]Test {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private [Repository] [repository];

    @Test
    void shouldFind[Entity]By[Field]() {
        // Given
        [Entity] entity = new [Entity]();
        // Set up entity
        entityManager.persistAndFlush(entity);

        // When
        Optional<[Entity]> result = [repository].findBy[Field](value);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().get[Field]()).isEqualTo(expectedValue);
    }
}
```

#### Integration Testing Pattern
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class [Controller]IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturn[ExpectedResult]_when[Condition]() {
        // Given
        String url = "/api/v1/[resource]";

        // When
        ResponseEntity<[Type]> response = restTemplate.getForEntity(url, [Type].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
```

## 🚨 Code Quality Requirements

### 1. Naming Conventions (MANDATORY)

#### Class Naming
```java
// Controllers MUST end with "Controller"
public class BookController { }

// Services MUST end with "Service"
public class BookService { }

// Repositories MUST end with "Repository"
public interface BookRepository { }

// DTOs MUST end with "Dto"
public class BookDto { }

// Entities MUST be singular nouns
public class Book { }  // NOT Books
```

#### Method Naming
```java
// Repository methods MUST follow Spring Data conventions
findBy[Property]([Type] [property])
findBy[Property1]And[Property2]([Type] [prop1], [Type] [prop2])
findBy[Property]ContainingIgnoreCase(String [property])

// Service methods MUST be descriptive
public List<Book> findAllBooks() { }
public Optional<Book> findBookById(Long id) { }
public Book saveBook(Book book) { }
public void deleteBookById(Long id) { }

// Controller methods MUST match HTTP verbs
public ResponseEntity<List<BookDto>> getAllBooks() { }  // GET
public ResponseEntity<BookDto> getBookById() { }        // GET /{id}
public ResponseEntity<BookDto> createBook() { }         // POST
public ResponseEntity<BookDto> updateBook() { }         // PUT /{id}
public ResponseEntity<Void> deleteBook() { }            // DELETE /{id}
```

### 2. Error Handling Requirements

#### Global Exception Handler (REQUIRED)
```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("RESOURCE_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(ValidationException ex) {
        ErrorResponse error = new ErrorResponse("VALIDATION_ERROR", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        ErrorResponse error = new ErrorResponse("INTERNAL_ERROR", "An unexpected error occurred");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

### 3. Validation Standards

#### Entity Validation (REQUIRED)
```java
@Entity
public class Book {

    @NotNull(message = "ISBN13 is required")
    @Size(min = 13, max = 13, message = "ISBN13 must be exactly 13 characters")
    @Column(unique = true, nullable = false, length = 13)
    private String isbn13;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    @Column(nullable = false)
    private String title;

    @DecimalMin(value = "0.0", message = "Price must be positive")
    @Column(name = "list_price", precision = 10, scale = 2)
    private BigDecimal listPrice;
}
```

#### Controller Validation (REQUIRED)
```java
@PostMapping
public ResponseEntity<BookDto> createBook(@Valid @RequestBody Book book) {
    // @Valid triggers validation annotations
    Book savedBook = bookRepository.save(book);
    return ResponseEntity.status(HttpStatus.CREATED).body(new BookDto(savedBook));
}
```

## 📊 Performance Guidelines

### 1. Database Query Optimization

#### Required Query Patterns
```java
// GOOD: Use pagination for collections
@GetMapping
public ResponseEntity<Page<BookDto>> getAllBooks(Pageable pageable) {
    Page<Book> books = bookRepository.findAll(pageable);
    Page<BookDto> bookDtos = books.map(BookDto::new);
    return ResponseEntity.ok(bookDtos);
}

// GOOD: Use specific queries instead of loading all data
@Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.isbn13 = :keyword")
Page<Book> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

// AVOID: Loading all entities without pagination
public List<Book> getAllBooks() {
    return bookRepository.findAll(); // BAD for large datasets
}
```

### 2. Caching Strategy

#### Service-Level Caching (RECOMMENDED)
```java
@Service
@CacheConfig(cacheNames = "books")
public class BookService {

    @Cacheable(key = "#id")
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @CacheEvict(key = "#book.id")
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @CacheEvict(allEntries = true)
    public void clearCache() {
        // Manually clear cache when needed
    }
}
```

## 🔍 Code Review Checklist

### Before Submitting Code (MANDATORY CHECKS)

#### ✅ Controller Layer
- [ ] Uses `@RestController` and proper mapping annotations
- [ ] Includes `@CrossOrigin` for React frontend compatibility
- [ ] Returns appropriate HTTP status codes
- [ ] Uses `ResponseEntity<T>` for all responses
- [ ] Implements proper error handling (404, 400, 500)
- [ ] Supports pagination for collection endpoints

#### ✅ Entity Layer
- [ ] Uses `@Entity` and `@Table` with explicit table name
- [ ] Has `@Id` with `GenerationType.IDENTITY`
- [ ] All columns have explicit `@Column` annotations
- [ ] Includes default constructor
- [ ] Uses snake_case for database column names
- [ ] Enums use `@Enumerated(EnumType.STRING)`

#### ✅ Repository Layer
- [ ] Extends `JpaRepository<Entity, Long>`
- [ ] Custom queries use proper `@Query` annotations
- [ ] Method names follow Spring Data conventions
- [ ] Complex queries are well-documented

#### ✅ Service Layer
- [ ] Uses `@Service` annotation
- [ ] Implements proper business logic separation
- [ ] Handles exceptions appropriately
- [ ] Uses dependency injection correctly

#### ✅ Testing
- [ ] Unit tests for all service methods
- [ ] Integration tests for API endpoints
- [ ] Test coverage above 80%
- [ ] Uses proper test naming conventions

#### ✅ General Code Quality
- [ ] Follows naming conventions consistently
- [ ] No hardcoded values (use configuration)
- [ ] Proper exception handling
- [ ] Code is well-documented
- [ ] No deprecated API usage

## 🚀 Build and Deployment Standards

### Required Maven Commands
```bash
# Development build
./mvnw clean compile -Pdev

# Run tests with coverage
./mvnw clean test jacoco:report

# Package for production
./mvnw clean package -Pprod -DskipTests

# Run application locally
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Required Environment Variables (Production)
```bash
DATABASE_URL=jdbc:postgresql://host:port/database
DB_USERNAME=username
DB_PASSWORD=password
REDIS_URL=redis://host:port
```

## ⚠️ Legacy System Constraints

### Java 8 Limitations (CANNOT USE)
```java
// CANNOT USE: Java 9+ features
var variable = "value";                    // var keyword
List.of("item1", "item2");                 // Collection factory methods
stream.toList();                           // toList() method
String.isBlank();                          // String methods added after Java 8
Optional.isEmpty();                        // Optional methods added after Java 8

// MUST USE: Java 8 compatible alternatives
String variable = "value";                 // Explicit type declaration
Arrays.asList("item1", "item2");          // Use Arrays.asList()
stream.collect(Collectors.toList());       // Use Collectors.toList()
str.trim().isEmpty();                      // Use trim().isEmpty()
!optional.isPresent();                     // Use !isPresent()
```

### Spring Boot 2.3 Constraints (CANNOT USE)
```java
// CANNOT USE: Spring Boot 3.x features
@RequestMapping("/api/books")              // Use @RequestMapping
@GetMapping("/books")                      // @GetMapping is available
public ResponseEntity<List<BookDto>> books(@RequestParam String filter) {
    // Cannot use Spring Boot 3.x specific features
}

// MUST USE: Spring Boot 2.3 compatible patterns
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {  // LEGACY PATTERN
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Configure security using legacy methods
    }
}
```

This coding policy ensures consistency across the legacy Spring Boot 2.3 + Java 8 application while maintaining educational value for the modernization workshop.
