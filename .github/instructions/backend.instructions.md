# Backend Development Instructions - Spring Boot Modernization

## 🎯 Context
This file provides specific instructions for working with the **TechBookStore backend**, a Spring Boot 2.3.12 + Java 8 application that serves as the foundation for a comprehensive modernization workshop.

## 🏗️ Current Architecture

### Technology Stack (Legacy - Intentional)
```
- Spring Boot: 2.3.12.RELEASE
- Java: 1.8 (OpenJDK 8)
- Build Tool: Maven 3.6.3
- Database: H2 (development), PostgreSQL (production)
- Cache: Redis with Spring Data Redis
- Security: Spring Security 5.3.x
- API Documentation: Swagger 2 (Springfox 2.9.2)
- Testing: JUnit 5, Mockito, Spring Boot Test
```

### Package Structure
```
com.techbookstore.app/
├── TechBookStoreApplication.java    # Main application class
├── config/                          # Configuration classes
│   ├── SecurityConfig.java         # Spring Security configuration
│   ├── SwaggerConfig.java          # Swagger documentation setup
│   └── RedisConfig.java            # Redis cache configuration
├── controller/                      # REST API controllers
│   ├── BookController.java         # Book management endpoints
│   ├── InventoryController.java    # Inventory operations
│   ├── CustomerController.java     # Customer management
│   └── ReportsController.java      # Analytics and reporting
├── dto/                            # Data Transfer Objects
│   ├── BookDto.java               # Book data transfer
│   ├── InventoryDto.java          # Inventory transfer objects
│   └── CustomerDto.java           # Customer data structures
├── entity/                         # JPA Entity classes
│   ├── Book.java                  # Book entity with relationships
│   ├── Inventory.java             # Inventory tracking
│   ├── Customer.java              # Customer information
│   └── Order.java                 # Order management
├── exception/                      # Custom exception handling
│   ├── GlobalExceptionHandler.java # Global error handling
│   └── BusinessException.java     # Business logic exceptions
├── repository/                     # Spring Data JPA repositories
│   ├── BookRepository.java        # Book data access
│   ├── InventoryRepository.java   # Inventory queries
│   └── CustomerRepository.java    # Customer data operations
└── service/                        # Business logic layer
    ├── BookService.java           # Book business operations
    ├── InventoryService.java      # Inventory management logic
    ├── CustomerService.java       # Customer service operations
    └── ReportsService.java        # Analytics and reporting logic
```

## 🎓 Educational Objectives

### Learning Goals for Backend Modernization
1. **Legacy Pattern Recognition**: Identify outdated Spring Boot 2.3 patterns
2. **Migration Planning**: Understand Spring Boot 2.3 → 3.x upgrade path
3. **Java Modernization**: Migrate from Java 8 to Java 17+ features
4. **Security Updates**: Implement modern Spring Security patterns
5. **API Evolution**: Upgrade from Swagger 2 to OpenAPI 3
6. **Performance Optimization**: Apply modern JVM and Spring optimizations

## 🔧 Development Guidelines

### When Adding New Features
```java
// CURRENT PATTERN (Legacy Spring Boot 2.3)
@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }

    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.save(book);
    }
}

// SUGGESTED MODERN PATTERN (Target Spring Boot 3.x)
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Validated
public class BookController {

    private final BookService bookService;
    private final BookMapper bookMapper;

    @GetMapping
    public ResponseEntity<List<BookDto>> getAllBooks() {
        List<BookDto> books = bookService.findAll()
            .stream()
            .map(bookMapper::toDto)
            .toList(); // Java 17+ feature

        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<BookDto> createBook(@Valid @RequestBody CreateBookRequest request) {
        Book book = bookService.save(bookMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookMapper.toDto(book));
    }
}
```

### Configuration Patterns

#### Legacy Security Configuration (Current)
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/api/public/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .httpBasic();
    }
}
```

#### Modern Security Configuration (Target)
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .build();
    }
}
```

### Database Configuration

#### Current H2 Configuration (Development)
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
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
      ddl-auto: create-drop
    show-sql: true
```

#### PostgreSQL Configuration (Production)
```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}
    driver-class-name: org.postgresql.Driver
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
    show-sql: false
```

## 🧪 Testing Guidelines

### Unit Testing Patterns
```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldReturnAllBooks() {
        // Given
        List<Book> expectedBooks = Arrays.asList(
            createTestBook("Spring Boot in Action"),
            createTestBook("Java Concurrency in Practice")
        );
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        // When
        List<Book> actualBooks = bookService.findAll();

        // Then
        assertThat(actualBooks).hasSize(2);
        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }
}
```

### Integration Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BookControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnBooksList() {
        // When
        ResponseEntity<Book[]> response = restTemplate.getForEntity("/api/books", Book[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
```

## 🚀 Performance Considerations

### Current Performance Patterns
```java
// Basic JPA query (may cause N+1 problem)
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategory(String category);
}

// Optimized query with fetch joins
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.categories LEFT JOIN FETCH b.inventory WHERE b.category = :category")
    List<Book> findByCategoryWithDetails(@Param("category") String category);
}
```

### Caching Integration
```java
@Service
@CacheConfig(cacheNames = "books")
public class BookService {

    @Cacheable
    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    @CacheEvict(allEntries = true)
    public Book save(Book book) {
        return bookRepository.save(book);
    }
}
```

## 📊 Monitoring & Observability

### Actuator Endpoints
```yaml
# Current actuator configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

### Custom Health Indicators
```java
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    @Override
    public Health health() {
        try {
            long count = bookRepository.count();
            return Health.up()
                .withDetail("books.count", count)
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}
```

## 🛠️ Build & Deployment

### Maven Configuration Highlights
```xml
<properties>
    <java.version>1.8</java.version>  <!-- Legacy: Target Java 17+ -->
    <spring-boot.version>2.3.12.RELEASE</spring-boot.version>  <!-- Target: 3.x -->
    <springfox.version>2.9.2</springfox.version>  <!-- Target: OpenAPI 3 -->
</properties>
```

### Development Commands
```bash
# Start application in development mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests with coverage
./mvnw clean test jacoco:report

# Package for production
./mvnw clean package -Pprod -DskipTests

# Run with specific profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=staging
```

## 🔧 Common Development Tasks

### Adding New Entity
1. Create entity class in `entity/` package
2. Create corresponding repository in `repository/` package
3. Create service class in `service/` package
4. Create DTO classes in `dto/` package
5. Create controller with REST endpoints
6. Add comprehensive unit and integration tests

### Database Migration
1. Update entity classes with new fields/relationships
2. Configure Hibernate DDL for development (`create-drop`)
3. Create Flyway migrations for production
4. Test migrations with different profiles

### API Documentation
```java
@RestController
@Api(tags = "Book Management", description = "Operations related to book inventory")
public class BookController {

    @ApiOperation(value = "Get all books", notes = "Retrieves all books in the inventory")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Books retrieved successfully"),
        @ApiResponse(code = 500, message = "Internal server error")
    })
    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookService.findAll();
    }
}
```

## 🎯 Workshop Integration

### GitHub Copilot Prompts for Backend
```
"Convert this Spring Boot 2.3 controller to modern Spring Boot 3.x patterns with proper error handling and validation"

"Suggest performance optimizations for this JPA query that might cause N+1 problems"

"Create a comprehensive test suite for this service class following modern testing patterns"

"Help me migrate this Spring Security configuration from WebSecurityConfigurerAdapter to the new SecurityFilterChain approach"
```

### Code Analysis Patterns
- Identify legacy `@Autowired` field injection → Constructor injection
- Find deprecated Spring Boot patterns → Modern alternatives
- Locate potential security vulnerabilities → Secure implementations
- Discover performance bottlenecks → Optimization opportunities

## 🚨 Important Notes

### Legacy Considerations
- **Java 8 Limitations**: Cannot use modern language features (var, records, pattern matching)
- **Spring Boot 2.3**: Missing newer features like native compilation, improved metrics
- **Swagger 2**: Limited OpenAPI 3 features, deprecated annotations

### Migration Roadmap
1. **Phase 1**: Update to Java 11 (intermediate step)
2. **Phase 2**: Migrate to Spring Boot 3.x
3. **Phase 3**: Upgrade to Java 17+
4. **Phase 4**: Implement modern patterns and optimizations

This backend serves as an excellent foundation for learning modern Spring Boot development while understanding the evolution from legacy patterns to contemporary best practices.
