# Testing and Quality Assurance Instructions

## 🎯 Context
This file provides specific instructions for **testing strategies, quality assurance frameworks, and validation procedures** for the TechBookStore modernization workshop, ensuring both educational effectiveness and technical reliability.

## 🧪 Testing Architecture Overview

### Multi-Layer Testing Strategy
```
┌─────────────────────────────────────────────────────────────┐
│                    Workshop Quality Framework                │
├─────────────────────────────────────────────────────────────┤
│  Educational Content Testing    │    Technical Content Testing │
│  ├─ Learning Path Validation   │    ├─ Code Example Verification │
│  ├─ Exercise Completeness      │    ├─ Deployment Testing        │
│  ├─ Time Allocation Accuracy   │    ├─ Cross-Platform Validation │
│  └─ Multilingual Consistency   │    └─ Security Assessment       │
├─────────────────────────────────────────────────────────────┤
│                    Application Testing                       │
│  Backend (Spring Boot)         │    Frontend (React)           │
│  ├─ Unit Tests (JUnit 5)      │    ├─ Unit Tests (Jest)        │
│  ├─ Integration Tests          │    ├─ Component Tests (RTL)    │
│  ├─ API Contract Tests         │    ├─ E2E Tests (Cypress)      │
│  └─ Performance Tests          │    └─ Accessibility Tests      │
├─────────────────────────────────────────────────────────────┤
│                Infrastructure Testing                        │
│  ├─ Bicep Template Validation  │    ├─ Security Scan           │
│  ├─ Deployment Verification    │    ├─ Cost Optimization       │
│  ├─ Environment Consistency    │    └─ Monitoring Validation   │
└─────────────────────────────────────────────────────────────┘
```

### Testing Technologies & Frameworks
```
Backend Testing:
├─ JUnit 5.8.x          # Unit testing framework
├─ Mockito 4.x          # Mocking framework
├─ Spring Boot Test     # Integration testing
├─ TestContainers       # Database integration tests
├─ JaCoCo              # Code coverage analysis
└─ WireMock            # API mocking

Frontend Testing:
├─ Jest 26.x           # JavaScript testing framework
├─ React Testing Library # Component testing utilities
├─ MSW                 # Mock Service Worker for API mocking
├─ Cypress (planned)   # End-to-end testing
└─ axe-core            # Accessibility testing

Infrastructure Testing:
├─ Azure CLI           # Resource validation
├─ Bicep Linter       # Template validation
├─ Checkov            # Security scanning
└─ Azure Monitor      # Health monitoring
```

## 🔧 Backend Testing Guidelines

### Unit Testing Patterns

#### Service Layer Testing
```java
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private BookService bookService;

    @Test
    @DisplayName("Should return all books when repository contains data")
    void shouldReturnAllBooksWhenRepositoryContainsData() {
        // Given
        List<Book> expectedBooks = Arrays.asList(
            createTestBook("Spring Boot in Action", "Craig Walls"),
            createTestBook("Java Concurrency in Practice", "Brian Goetz")
        );
        when(bookRepository.findAll()).thenReturn(expectedBooks);

        // When
        List<Book> actualBooks = bookService.findAll();

        // Then
        assertThat(actualBooks)
            .hasSize(2)
            .extracting(Book::getTitle)
            .containsExactly("Spring Boot in Action", "Java Concurrency in Practice");

        verify(bookRepository).findAll();
    }

    @Test
    @DisplayName("Should throw exception when book not found")
    void shouldThrowExceptionWhenBookNotFound() {
        // Given
        Long nonExistentBookId = 999L;
        when(bookRepository.findById(nonExistentBookId))
            .thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookService.findById(nonExistentBookId))
            .isInstanceOf(BookNotFoundException.class)
            .hasMessage("Book not found with id: 999");
    }

    private Book createTestBook(String title, String author) {
        return Book.builder()
            .title(title)
            .author(author)
            .isbn("978-1234567890")
            .price(BigDecimal.valueOf(3500))
            .category("Technical")
            .build();
    }
}
```

#### Repository Testing with TestContainers
```java
@SpringBootTest
@Testcontainers
class BookRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @Transactional
    void shouldFindBooksByCategory() {
        // Given
        Book frontendBook = createTestBook("React Design Patterns", "Frontend");
        Book backendBook = createTestBook("Spring Boot in Action", "Backend");

        entityManager.persistAndFlush(frontendBook);
        entityManager.persistAndFlush(backendBook);

        // When
        List<Book> frontendBooks = bookRepository.findByCategory("Frontend");

        // Then
        assertThat(frontendBooks)
            .hasSize(1)
            .extracting(Book::getTitle)
            .containsExactly("React Design Patterns");
    }

    @Test
    void shouldFindBooksWithInventoryDetails() {
        // Given
        Book book = createTestBookWithInventory();
        entityManager.persistAndFlush(book);

        // When
        List<Book> booksWithInventory = bookRepository.findBooksWithInventory();

        // Then
        assertThat(booksWithInventory)
            .hasSize(1)
            .extracting(Book::getInventory)
            .isNotNull();
    }
}
```

#### Controller Integration Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BookControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void shouldReturnAllBooks() {
        // Given
        Book book1 = createTestBook("Book 1");
        Book book2 = createTestBook("Book 2");
        bookRepository.saveAll(Arrays.asList(book1, book2));

        // When
        ResponseEntity<Book[]> response = restTemplate.getForEntity("/api/books", Book[].class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void shouldCreateNewBook() {
        // Given
        CreateBookRequest request = CreateBookRequest.builder()
            .title("New Book")
            .author("Test Author")
            .isbn("978-1234567890")
            .price(BigDecimal.valueOf(2500))
            .category("Technical")
            .build();

        // When
        ResponseEntity<Book> response = restTemplate.postForEntity(
            "/api/books", request, Book.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getTitle()).isEqualTo("New Book");

        // Verify in database
        List<Book> books = bookRepository.findAll();
        assertThat(books).hasSize(1);
    }

    @Test
    void shouldReturnValidationErrorForInvalidBook() {
        // Given
        CreateBookRequest invalidRequest = CreateBookRequest.builder()
            .title("") // Empty title should cause validation error
            .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/api/books", invalidRequest, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
```

### API Contract Testing
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookApiContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldMatchOpenApiSpecification() throws Exception {
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk())
            .andExpected(content().contentType(MediaType.APPLICATION_JSON))
            .andExpected(jsonPath("$").isArray())
            .andExpected(jsonPath("$[*].id").exists())
            .andExpected(jsonPath("$[*].title").exists())
            .andExpected(jsonPath("$[*].author").exists());
    }
}
```

## 🎨 Frontend Testing Guidelines

### Component Unit Testing
```javascript
// __tests__/BookList.test.js
import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import BookList from '../BookList';
import { I18nProvider } from '../contexts/I18nContext';
import { ThemeProvider } from '@material-ui/core/styles';
import theme from '../theme';

// Mock server setup
const server = setupServer(
  rest.get('/api/books', (req, res, ctx) => {
    return res(
      ctx.json([
        {
          id: 1,
          title: 'Spring Boot in Action',
          author: 'Craig Walls',
          category: 'Backend',
          price: 3500
        },
        {
          id: 2,
          title: 'React Design Patterns',
          author: 'John Doe',
          category: 'Frontend',
          price: 2800
        },
      ])
    );
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

// Test wrapper with providers
const renderWithProviders = (component) => {
  return render(
    <ThemeProvider theme={theme}>
      <I18nProvider>
        {component}
      </I18nProvider>
    </ThemeProvider>
  );
};

describe('BookList Component', () => {
  test('renders loading state initially', () => {
    renderWithProviders(<BookList />);

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });

  test('displays books after loading', async () => {
    renderWithProviders(<BookList />);

    await waitFor(() => {
      expect(screen.getByText('Spring Boot in Action')).toBeInTheDocument();
      expect(screen.getByText('React Design Patterns')).toBeInTheDocument();
    });

    // Check for proper formatting
    expect(screen.getByText('¥3,500')).toBeInTheDocument();
    expect(screen.getByText('¥2,800')).toBeInTheDocument();
  });

  test('filters books by search term', async () => {
    const user = userEvent.setup();
    renderWithProviders(<BookList />);

    // Wait for books to load
    await waitFor(() => {
      expect(screen.getByText('Spring Boot in Action')).toBeInTheDocument();
    });

    // Search for "Spring"
    const searchInput = screen.getByRole('textbox', { name: /search/i });
    await user.type(searchInput, 'Spring');

    expect(screen.getByText('Spring Boot in Action')).toBeInTheDocument();
    expect(screen.queryByText('React Design Patterns')).not.toBeInTheDocument();
  });

  test('handles API error gracefully', async () => {
    server.use(
      rest.get('/api/books', (req, res, ctx) => {
        return res(ctx.status(500), ctx.json({ message: 'Server error' }));
      })
    );

    renderWithProviders(<BookList />);

    await waitFor(() => {
      expect(screen.getByText(/error loading books/i)).toBeInTheDocument();
    });
  });

  test('supports accessibility requirements', async () => {
    renderWithProviders(<BookList />);

    await waitFor(() => {
      expect(screen.getByText('Spring Boot in Action')).toBeInTheDocument();
    });

    // Check ARIA labels
    expect(screen.getByRole('textbox', { name: /search/i })).toHaveAttribute('aria-label');
    expect(screen.getByRole('list')).toHaveAttribute('aria-label', 'Books list');

    // Check keyboard navigation
    const firstBook = screen.getByRole('listitem', { name: /spring boot/i });
    expect(firstBook).toHaveAttribute('tabindex', '0');
  });
});
```

### Custom Hook Testing
```javascript
// __tests__/useApi.test.js
import { renderHook, waitFor } from '@testing-library/react';
import { rest } from 'msw';
import { setupServer } from 'msw/node';
import { useApi } from '../hooks/useApi';

const server = setupServer(
  rest.get('/api/test', (req, res, ctx) => {
    return res(ctx.json({ data: 'test data' }));
  })
);

beforeAll(() => server.listen());
afterEach(() => server.resetHandlers());
afterAll(() => server.close());

describe('useApi Hook', () => {
  test('returns loading state initially', () => {
    const { result } = renderHook(() => useApi('/api/test'));

    expect(result.current.loading).toBe(true);
    expect(result.current.data).toBe(null);
    expect(result.current.error).toBe(null);
  });

  test('returns data on successful API call', async () => {
    const { result } = renderHook(() => useApi('/api/test'));

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.data).toEqual({ data: 'test data' });
    expect(result.current.error).toBe(null);
  });

  test('handles API errors', async () => {
    server.use(
      rest.get('/api/test', (req, res, ctx) => {
        return res(ctx.status(404), ctx.json({ message: 'Not found' }));
      })
    );

    const { result } = renderHook(() => useApi('/api/test'));

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.data).toBe(null);
    expect(result.current.error).toBeTruthy();
  });
});
```

### Integration Testing with Cypress (Planned)
```javascript
// cypress/integration/book-management.spec.js
describe('Book Management Flow', () => {
  beforeEach(() => {
    cy.visit('/books');
    cy.intercept('GET', '/api/books', { fixture: 'books.json' }).as('getBooks');
  });

  it('should allow user to view and search books', () => {
    cy.wait('@getBooks');

    // Check initial state
    cy.get('[data-testid="book-list"]').should('be.visible');
    cy.get('[data-testid="book-item"]').should('have.length.gte', 1);

    // Test search functionality
    cy.get('[data-testid="search-input"]').type('Spring Boot');
    cy.get('[data-testid="book-item"]').should('contain', 'Spring Boot');

    // Test book details navigation
    cy.get('[data-testid="book-item"]').first().click();
    cy.url().should('include', '/books/');
    cy.get('[data-testid="book-title"]').should('be.visible');
  });

  it('should handle error states gracefully', () => {
    cy.intercept('GET', '/api/books', { statusCode: 500 }).as('getBooksError');
    cy.visit('/books');

    cy.wait('@getBooksError');
    cy.get('[data-testid="error-message"]').should('be.visible');
    cy.get('[data-testid="retry-button"]').should('be.visible');
  });
});
```

## ☁️ Infrastructure Testing

### Bicep Template Validation
```bash
#!/bin/bash
# scripts/test-infrastructure.sh

echo "🔍 Validating Bicep templates..."

# Validate main template
az bicep build --file BICEP-TEMPLATES/main.bicep
if [ $? -ne 0 ]; then
    echo "❌ Main template validation failed"
    exit 1
fi

# Validate all modules
for module in BICEP-TEMPLATES/modules/*.bicep; do
    echo "Validating $module..."
    az bicep build --file "$module"
    if [ $? -ne 0 ]; then
        echo "❌ Module validation failed: $module"
        exit 1
    fi
done

echo "✅ All Bicep templates validated successfully"

# Test deployment in validation mode
echo "🧪 Testing deployment validation..."
az deployment group validate \
    --resource-group "rg-test" \
    --template-file BICEP-TEMPLATES/main.bicep \
    --parameters environment=dev \
                appName=testapp \
                dbAdminPassword="TestPassword123!"

if [ $? -eq 0 ]; then
    echo "✅ Deployment validation passed"
else
    echo "❌ Deployment validation failed"
    exit 1
fi
```

### Azure Resource Testing
```bash
#!/bin/bash
# scripts/test-azure-deployment.sh

RESOURCE_GROUP="rg-techbookstore-test"
LOCATION="japaneast"

echo "🚀 Testing Azure deployment..."

# Create test resource group
az group create --name $RESOURCE_GROUP --location $LOCATION

# Deploy infrastructure
azd provision --environment test

# Test application endpoints
FRONTEND_URL=$(azd env get-values | grep FRONTEND_URL | cut -d'=' -f2 | tr -d '"')
BACKEND_URL=$(azd env get-values | grep BACKEND_URL | cut -d'=' -f2 | tr -d '"')

echo "Testing frontend at: $FRONTEND_URL"
curl -f "$FRONTEND_URL" || echo "❌ Frontend not accessible"

echo "Testing backend health at: $BACKEND_URL/actuator/health"
curl -f "$BACKEND_URL/actuator/health" || echo "❌ Backend health check failed"

# Test database connectivity
echo "Testing database connection..."
# Add database connection test

# Cleanup
echo "🧹 Cleaning up test resources..."
azd down --environment test --purge
```

## 📋 Workshop Content Testing

### Content Validation Scripts
```bash
#!/bin/bash
# scripts/test-workshop-content.sh

echo "📚 Testing workshop content..."

# Test all markdown files for broken links
echo "Checking for broken links..."
find workshop-docs -name "*.md" -exec markdown-link-check {} \;

# Validate code examples
echo "Validating code examples..."
./scripts/validate-code-examples.sh

# Test multilingual consistency
echo "Checking multilingual consistency..."
./scripts/validate-i18n.sh

# Validate Codelabs format
echo "Validating Codelabs format..."
./scripts/validate-codelab.sh

# Test time allocations
echo "Checking time allocations..."
TOTAL_TIME=$(grep -r "Duration:" workshop-docs/02-hands-on/ | \
             awk '{sum += $2} END {print sum}')
echo "Total workshop time: $TOTAL_TIME minutes"

if [ $TOTAL_TIME -lt 240 ] || [ $TOTAL_TIME -gt 360 ]; then
    echo "⚠️ Total time outside target range (240-360 minutes)"
else
    echo "✅ Total time within target range"
fi

echo "✅ Workshop content validation completed"
```

### Code Example Validation
```bash
#!/bin/bash
# scripts/validate-code-examples.sh

echo "🔍 Validating code examples in documentation..."

# Extract Java code blocks and validate syntax
find workshop-docs -name "*.md" -exec grep -l "```java" {} \; | while read file; do
    echo "Validating Java examples in $file..."

    # Extract Java code blocks
    awk '/```java/,/```/' "$file" | grep -v '```' > /tmp/java_code.java

    # Basic syntax check (requires Java compiler)
    if [ -s /tmp/java_code.java ]; then
        javac -cp ".:backend/target/classes" /tmp/java_code.java 2>/dev/null
        if [ $? -ne 0 ]; then
            echo "⚠️ Java syntax issues in $file"
        fi
    fi
done

# Extract JavaScript/React code blocks
find workshop-docs -name "*.md" -exec grep -l "```javascript\|```jsx" {} \; | while read file; do
    echo "Validating JavaScript examples in $file..."

    # Extract JavaScript code blocks
    awk '/```javascript/,/```/' "$file" | grep -v '```' > /tmp/js_code.js
    awk '/```jsx/,/```/' "$file" | grep -v '```' >> /tmp/js_code.js

    # Basic syntax check (requires Node.js)
    if [ -s /tmp/js_code.js ]; then
        node -c /tmp/js_code.js 2>/dev/null
        if [ $? -ne 0 ]; then
            echo "⚠️ JavaScript syntax issues in $file"
        fi
    fi
done

# Cleanup
rm -f /tmp/java_code.java /tmp/js_code.js

echo "✅ Code example validation completed"
```

## 🎯 Quality Assurance Framework

### Test Automation Pipeline
```yaml
# .github/workflows/qa-pipeline.yml
name: Quality Assurance Pipeline

on:
  pull_request:
    paths:
      - 'backend/**'
      - 'frontend/**'
      - 'workshop-docs/**'
      - 'BICEP-TEMPLATES/**'

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '8'
          distribution: 'temurin'

      - name: Run backend tests
        run: |
          cd backend
          ./mvnw clean test jacoco:report

      - name: Upload coverage reports
        uses: codecov/codecov-action@v3
        with:
          file: backend/target/site/jacoco/jacoco.xml

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '12'

      - name: Install dependencies
        run: |
          cd frontend
          npm ci

      - name: Run frontend tests
        run: |
          cd frontend
          npm test -- --coverage --watchAll=false

      - name: Upload coverage reports
        uses: codecov/codecov-action@v3
        with:
          file: frontend/coverage/lcov.info

  infrastructure-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: azure/login@v1
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}

      - name: Validate Bicep templates
        run: ./scripts/test-infrastructure.sh

  content-validation:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Validate workshop content
        run: ./scripts/test-workshop-content.sh

      - name: Check documentation links
        uses: gaurav-nelson/github-action-markdown-link-check@v1
        with:
          use-quiet-mode: 'yes'
          use-verbose-mode: 'yes'
          config-file: '.github/markdown-link-check.json'
```

### Performance Testing
```java
// Performance test example
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookControllerPerformanceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    void shouldRespondWithin2Seconds() {
        ResponseEntity<Book[]> response = restTemplate.getForEntity("/api/books", Book[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void shouldHandleConcurrentRequests() throws InterruptedException {
        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    ResponseEntity<Book[]> response = restTemplate.getForEntity("/api/books", Book[].class);
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
```

This comprehensive testing framework ensures both the technical reliability and educational effectiveness of the TechBookStore modernization workshop, providing confidence in code quality, infrastructure stability, and learning outcomes.
