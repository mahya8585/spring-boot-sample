# GitHub Copilot Custom Instructions for Legacy Spring Boot Modernization Workshop

## 🎯 Project Context & Purpose

This workspace contains **TechBookStore**, a comprehensive educational framework for teaching legacy system modernization using GitHub Copilot and modern development practices. The project serves as both a hands-on workshop and reference implementation for enterprise-grade legacy application modernization.

### Core Mission
- **Educational Focus**: 6-chapter structured workshop (240-360 minutes) teaching modernization techniques
- **AI-Assisted Development**: Showcase GitHub Copilot integration patterns and prompt engineering
- **Real-World Application**: Complete modernization journey from Spring Boot 2.3/React 16 to Spring Boot 3.x/React 18+
- **Azure Cloud Deployment**: Production-ready infrastructure automation and DevOps practices

## 🏗️ Architecture Overview

### Current Legacy Stack (Intentionally Outdated)
```
Backend:  Spring Boot 2.3.12 + Java 8 + Maven
Frontend: React 16.13.1 + Material-UI 4.x + Redux
Database: H2 (dev) / PostgreSQL (prod)
Cache:    Redis integration
Security: Spring Security (legacy patterns)
```

### Target Modern Stack
```
Backend:  Spring Boot 3.x + Java 17+ + Jakarta EE
Frontend: React 18 + MUI 5.x + Modern hooks
Infrastructure: Azure Container Apps + Bicep IaC
Observability: Application Insights + monitoring
```

## 📚 Key Directory Structure

```
/workspace/
├── backend/                    # Spring Boot 2.3 application (legacy)
│   ├── src/main/java/com/techbookstore/app/
│   │   ├── TechBookStoreApplication.java
│   │   ├── controller/         # REST API endpoints
│   │   ├── service/           # Business logic
│   │   ├── entity/            # JPA entities
│   │   └── repository/        # Data access
│   └── pom.xml               # Maven dependencies (Java 8)
├── frontend/                  # React 16 application (legacy)
│   ├── src/
│   │   ├── App.js            # Main routing component
│   │   ├── components/       # 25+ UI components
│   │   ├── contexts/         # I18n, state management
│   │   └── services/         # API integration
│   └── package.json          # npm dependencies (React 16)
├── workshop-docs/            # Comprehensive learning materials
│   ├── 01-setup/             # Implementation guides
│   ├── 02-hands-on/          # Workshop chapters 1-6
│   ├── 03-exercises/         # Prompt libraries (50+ patterns)
│   ├── 04-solutions/         # Reference implementations
│   └── 05-troubleshooting/   # Error resolution guides
├── codelabs/                 # Google Codelabs format content
├── AZD-PROJECT/              # Azure Developer CLI configuration
├── BICEP-TEMPLATES/          # Infrastructure as Code
└── scripts/                  # Automation and validation tools
```

## 🎓 Workshop Learning Objectives

### Chapter 1-3: Analysis & Strategy (120 minutes)
- GitHub Copilot-assisted legacy code analysis
- Issue identification and prioritization
- Modernization strategy planning

### Chapter 4-5: Implementation (120-180 minutes)
- Backend modernization (Spring Boot 2.3 → 3.x)
- Frontend modernization (React 16 → 18)
- Azure deployment automation

### Chapter 6: Operations (60 minutes)
- Monitoring and maintenance
- Continuous improvement practices

## 🤖 GitHub Copilot Integration Guidelines

### When Assisting with Code Analysis
1. **Use Educational Context**: Frame responses as learning opportunities
2. **Highlight Legacy Patterns**: Point out outdated practices and explain modern alternatives
3. **Provide Migration Paths**: Show step-by-step modernization approaches
4. **Reference Workshop Materials**: Connect to relevant chapters and exercises

### Code Modernization Patterns
```java
// LEGACY (Current): Spring Boot 2.3 + Java 8
@RestController
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public List<Book> getBooks() {
        return bookService.findAll();
    }
}

// MODERN (Target): Spring Boot 3.x + Java 17+
@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getBooks() {
        return ResponseEntity.ok(
            bookService.findAll()
                .stream()
                .map(BookMapper::toDto)
                .toList()
        );
    }
}
```

### Frontend Modernization Patterns
```javascript
// LEGACY (Current): React 16 + Class Components
class BookList extends Component {
    constructor(props) {
        super(props);
        this.state = { books: [] };
    }

    componentDidMount() {
        this.fetchBooks();
    }
}

// MODERN (Target): React 18 + Hooks
const BookList = () => {
    const [books, setBooks] = useState([]);

    useEffect(() => {
        fetchBooks();
    }, []);

    return <div>{/* JSX */}</div>;
};
```

## 🛠️ Development Commands & Scripts

### Application Management
```bash
# Start full application (backend + frontend)
./start-app.sh

# Check application status
./status-app.sh

# Stop all services
./stop-app.sh
```

### Azure Deployment
```bash
# Deploy to Azure (complete infrastructure + apps)
azd up

# Provision infrastructure only
azd provision

# Deploy applications only
azd deploy
```

### Workshop Content Validation
```bash
# Validate Google Codelabs format
./scripts/validate-codelab.sh

# Generate local preview
./scripts/generate-preview.sh

# Validate internationalization
./validate-i18n.sh
```

## 🌐 Multilingual Support

### Primary Language: Japanese
- Workshop documentation is primarily in Japanese
- UI components support Japanese localization
- Error messages and validation in Japanese

### Secondary Language: English
- Bilingual prompt engineering examples
- English abstracts for international users
- Code comments in English

### I18n Implementation
```javascript
// Usage in React components
const { t } = useI18n();
return <h1>{t('app.title', 'TechBookStore - 技術専門書店在庫管理システム')}</h1>;
```

## 🎯 AI Assistant Guidelines

### When Working on Backend (Spring Boot)
1. **Recognize Legacy Patterns**: Identify Spring Boot 2.3/Java 8 patterns
2. **Suggest Modern Alternatives**: Propose Spring Boot 3.x/Java 17+ improvements
3. **Security Considerations**: Highlight security modernization opportunities
4. **Performance Improvements**: Suggest optimization patterns

### When Working on Frontend (React)
1. **Component Modernization**: Convert class components to functional components
2. **Hook Patterns**: Suggest modern React hooks usage
3. **Material-UI Migration**: Propose MUI 5.x upgrade paths
4. **State Management**: Modernize Redux patterns

### When Working on Infrastructure
1. **Azure Best Practices**: Follow Azure Well-Architected Framework
2. **Bicep Patterns**: Use modular, reusable Bicep templates
3. **Security by Default**: Implement secure infrastructure patterns
4. **Cost Optimization**: Suggest cost-effective resource configurations

### When Working on Documentation
1. **Educational Focus**: Maintain workshop learning objectives
2. **Step-by-Step Guidance**: Provide clear, actionable instructions
3. **Bilingual Support**: Consider both Japanese and English audiences
4. **Workshop Integration**: Connect to existing chapter content

## 🔍 Code Quality Standards

### Backend Standards
- **Java Version**: Currently Java 8 (legacy), target Java 17+
- **Spring Boot**: Currently 2.3.12, target 3.x
- **Testing**: JUnit 5 + Mockito, JaCoCo coverage
- **Documentation**: Swagger 2 (legacy) → OpenAPI 3

### Frontend Standards
- **React Version**: Currently 16.13.1, target 18+
- **UI Library**: Material-UI 4.x → MUI 5.x
- **Testing**: Jest + React Testing Library
- **Build**: Create React App with custom configurations

### Infrastructure Standards
- **IaC**: Bicep templates with modular design
- **Security**: Azure Key Vault, private endpoints
- **Monitoring**: Application Insights integration
- **Scalability**: Container Apps with auto-scaling

## 🚨 Important Considerations

### Legacy System Awareness
- **Intentional Legacy Stack**: Current codebase uses outdated technologies for educational purposes
- **Migration Journey**: Focus on gradual, systematic modernization
- **Breaking Changes**: Be aware of Spring Boot 2.3 → 3.x breaking changes
- **Compatibility Issues**: Consider Java 8 → 17+ migration challenges

### Workshop Context
- **Learning-First Approach**: Prioritize educational value over production optimization
- **Step-by-Step Methodology**: Follow workshop chapter progression
- **Prompt Engineering**: Demonstrate effective GitHub Copilot usage patterns
- **Real-World Relevance**: Maintain enterprise applicability

### Azure Integration
- **Cost Consciousness**: Use cost-effective Azure services for workshops
- **Security Focus**: Implement enterprise-grade security patterns
- **Automation**: Leverage azd for streamlined deployment
- **Monitoring**: Comprehensive observability setup

## 🎪 Unique Project Features

### TechBookStore Business Domain
- **Inventory Management**: Technical bookstore with specialized categories
- **Customer Management**: B2B and individual customer support
- **Analytics Dashboard**: Sales trends and inventory insights
- **Internationalization**: Japanese/English book catalog support

### Educational Innovation
- **GitHub Copilot Integration**: 50+ documented prompt patterns
- **Interactive Learning**: Google Codelabs format integration
- **Progressive Complexity**: Beginner to advanced learning tracks
- **Real-World Scenarios**: Enterprise-grade modernization challenges

This workspace represents a sophisticated educational framework designed to teach modern software development practices through hands-on experience with legacy system modernization, AI-assisted development, and cloud-native deployment patterns.
