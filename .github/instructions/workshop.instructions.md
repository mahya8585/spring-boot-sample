# Workshop Documentation Instructions

## 🎯 Context
This file provides specific instructions for working with the **workshop documentation and educational materials** of the TechBookStore modernization workshop, including Google Codelabs content, multilingual documentation, and learning path management.

## 📚 Documentation Architecture

### Documentation Hierarchy
```
/workspace/
├── README.md                       # Main project overview
├── workshop-docs/                  # Comprehensive workshop materials
│   ├── README.md                  # Workshop navigation index
│   ├── 00-prerequisites/         # Environment setup (planned)
│   ├── 01-setup/                 # Implementation guides
│   ├── 02-hands-on/              # Core workshop chapters
│   ├── 03-exercises/             # Practice exercises & prompts
│   ├── 04-solutions/             # Reference implementations
│   ├── 05-troubleshooting/       # Error resolution guides
│   └── 06-appendix/              # Reference materials
├── codelabs/                      # Google Codelabs format
│   ├── README.md                 # Codelabs overview
│   ├── chapter1-current-state-analysis.md  # Main codelab (2207 lines)
│   └── assets/                   # Supporting resources
└── docs/                         # Additional documentation
    ├── README.md                 # Documentation structure
    └── codelabs/                 # Codelab build artifacts
```

### Learning Path Structure
The workshop follows a systematic 6-chapter progression:

**Phase 1: Analysis & Strategy (120 minutes)**
- Chapter 1: Current State Analysis
- Chapter 2: Issue Identification
- Chapter 3: Strategy Planning

**Phase 2: Implementation (120-180 minutes)**
- Chapter 4: Detailed Implementation
- Chapter 5: Advanced Patterns

**Phase 3: Operations (60 minutes)**
- Chapter 6: Stabilization & Continuous Improvement

## 🎓 Educational Framework

### Target Audiences
1. **Workshop Participants**: Learning modernization with GitHub Copilot
2. **Instructors**: Teaching AI-assisted development
3. **Enterprise Teams**: Reference implementation patterns
4. **Azure Practitioners**: Cloud deployment methodologies

### Learning Objectives by Chapter

#### Chapter 1: Current State Analysis (80 minutes)
```markdown
**Core Skills:**
- GitHub Copilot Chat for legacy code analysis
- Prompt engineering for code discovery
- Systematic architecture documentation
- Dependency analysis automation

**Practical Outcomes:**
- Complete codebase inventory
- Technology stack assessment
- Security vulnerability identification
- Performance bottleneck analysis
```

#### Chapter 4: Implementation Planning (60-90 minutes)
```markdown
**Technical Focus:**
- Spring Boot 2.3 → 3.x migration paths
- React 16 → 18 modernization
- Jakarta EE namespace migration
- Material-UI → MUI 5.x upgrade

**AI Integration:**
- 20+ specialized prompt patterns
- Code generation workflows
- Automated testing strategies
- Documentation generation
```

### Workshop Materials Organization

#### Setup Guides (`workshop-docs/01-setup/`)
```
IMPLEMENTATION-GUIDE-CHAPTER4.md    # 📋 Technical implementation guide
operations-playbook.md              # 🚀 Azure deployment procedures
```

#### Hands-on Materials (`workshop-docs/02-hands-on/`)
```
WORKSHOP-CHAPTER[1-6].md           # 📖 Main workshop curriculum
TUTORIAL-CHAPTER[1-6].md           # 🔧 Step-by-step tutorials
TUTORIAL-CHAPTER5-AZURE-DEPLOYMENT.md  # ☁️ Azure-specific guide
```

#### Exercise Library (`workshop-docs/03-exercises/`)
```
PROMPTS-CHAPTER[1-5].md            # 🤖 GitHub Copilot prompt libraries
VALIDATION-CHECKLIST-CHAPTER4.md   # ✅ Quality assurance framework
```

## 🌐 Multilingual Documentation Strategy

### Primary Language: Japanese
- **Target Audience**: Japanese enterprise developers
- **Content Depth**: Comprehensive technical explanations
- **Cultural Context**: Japanese business practices and terminology

### Secondary Language: English
- **Target Audience**: International workshop participants
- **Content Scope**: Key concepts and summaries
- **Distribution**: GitHub documentation and abstracts

### I18n Implementation Pattern
```markdown
# Bilingual Heading Pattern
## 📊 現状分析フェーズ / Current State Analysis Phase

### Content Structure
- **Japanese (Primary)**: Detailed explanations and instructions
- **English (Secondary)**: Key concepts and technical summaries

### Code Examples
```java
// Japanese comments for business logic
// 顧客情報を取得する処理
public Customer getCustomer(Long customerId) {
    // English technical comments
    return customerRepository.findById(customerId)
        .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
}
```

### Workshop Prompts (Bilingual)
```
Japanese: "このレガシーコードをGitHub Copilotを使って分析し、モダナイゼーションの課題を特定してください"
English: "Analyze this legacy code using GitHub Copilot and identify modernization challenges"
```

## 📝 Google Codelabs Integration

### Codelabs Format Structure
```markdown
author: shinyay
summary: Step 1: レガシーシステム現状分析 - GitHub Copilot実践ワークショップ
id: chapter1-current-state-analysis
categories: workshop,github-copilot,legacy-modernization,prompt-engineering
environments: web
status: Published
feedback link: https://github.com/shinyay/legacy-spring-boot-23-app-ws-2508/issues
tags: github-copilot,spring-boot,react,legacy-modernization

# Main Workshop Title

## セクション1: 概要
Duration: 15

### 学習目標
- GitHub Copilot Chat を活用したレガシーアプリケーション分析手法
- プロンプトエンジニアリングによる効率的なコードベース調査
```

### Content Quality Standards
- **Duration Tracking**: Each section specifies time requirements
- **Interactive Elements**: Checkpoints and validation steps
- **Code Examples**: Syntax-highlighted, copy-pasteable code blocks
- **Progressive Complexity**: Beginner → Intermediate → Advanced

### Build and Validation
```bash
# Validate Codelabs format
./scripts/validate-codelab.sh

# Generate local preview
./scripts/generate-preview.sh

# Build with claat (requires Go installation)
cd codelabs
claat export chapter1-current-state-analysis.md
open chapter1-current-state-analysis/index.html
```

## 🔧 Documentation Development Guidelines

### Content Creation Patterns

#### Workshop Chapter Template
```markdown
# Chapter X: [Title] - Legacy Spring Boot Modernization Workshop

## 📋 Prerequisites
- [List of requirements]

## 🎯 Learning Objectives
### Core Skills
- [Primary skill 1]
- [Primary skill 2]

### Technical Outcomes
- [Deliverable 1]
- [Deliverable 2]

## 📊 Time Allocation
- **Total Duration**: X hours (X minutes)
- **Setup**: X minutes
- **Core Activity**: X minutes
- **Validation**: X minutes

## 🛠️ Hands-on Exercise
### Step 1: [Action]
```bash
# Command example
git clone repository
```

**Expected Output:**
```
Expected command output
```

### ✅ Checkpoint
Verify that you have completed:
- [ ] Task 1
- [ ] Task 2

## 🎯 GitHub Copilot Prompts
### Analysis Prompt
```
Japanese: "プロンプト内容（日本語）"
English: "Prompt content (English)"
```

### Expected Response Pattern
- AI should identify [specific patterns]
- Generated code should include [requirements]

## 🔍 Troubleshooting
### Common Issues
**Issue**: [Description]
**Solution**: [Step-by-step resolution]

## 📚 Additional Resources
- [Link to related documentation]
- [Reference implementation]
```

#### Prompt Library Template
```markdown
# GitHub Copilot Prompts - Chapter X

## 🎯 Prompt Categories

### Code Analysis Prompts
#### Legacy Pattern Detection
```
Prompt: "このSpring Boot 2.3のコードを分析し、Spring Boot 3.xへの移行で必要な変更点を特定してください"
Context: Legacy controller analysis
Expected: Migration checklist with specific code changes
```

#### Performance Analysis
```
Prompt: "このJPAクエリのパフォーマンス問題を特定し、最適化案を提案してください"
Context: N+1 query problems
Expected: Optimized query with @Query annotations
```

### Code Generation Prompts
#### Modern Component Creation
```
Prompt: "React 16のクラスコンポーネントをReact 18の関数コンポーネントとフックに変換してください"
Context: Component modernization
Expected: Functional component with useState/useEffect
```
```

### Solution Documentation Template
```markdown
# Implementation Solutions - Chapter X

## 🎯 Before/After Comparisons

### Backend Modernization
#### Legacy Pattern (Spring Boot 2.3)
```java
@RestController
public class BookController {
    @Autowired
    private BookService bookService; // Field injection (deprecated)

    @GetMapping("/books")
    public List<Book> getBooks() { // Raw entity exposure
        return bookService.findAll();
    }
}
```

#### Modern Pattern (Spring Boot 3.x)
```java
@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService; // Constructor injection
    private final BookMapper bookMapper;   // DTO mapping

    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getBooks() {
        List<BookDto> books = bookService.findAll()
            .stream()
            .map(bookMapper::toDto)
            .toList(); // Java 17+ feature

        return ResponseEntity.ok(books);
    }
}
```

### Migration Steps
1. **Dependency Injection**: Replace @Autowired with constructor injection
2. **Response Wrapping**: Use ResponseEntity for proper HTTP responses
3. **DTO Pattern**: Implement data transfer objects
4. **Modern APIs**: Utilize Java 17+ language features
```

## 🎯 Workshop Facilitation Guidelines

### Instructor Notes
```markdown
### Teaching Points for Chapter X
- **Key Concept**: Emphasize the importance of [concept]
- **Common Mistakes**: Students often struggle with [issue]
- **Time Management**: Allow extra time for [complex topic]
- **Interactive Elements**: Use [specific exercise] to reinforce learning

### GitHub Copilot Demonstration
1. **Setup**: Ensure all students have Copilot enabled
2. **Prompt Strategy**: Start with simple prompts, build complexity
3. **Error Handling**: Show how to refine prompts when output is incorrect
4. **Best Practices**: Demonstrate prompt engineering techniques
```

### Student Progress Tracking
```markdown
### Validation Checkpoints
#### Chapter 1 Completion Criteria
- [ ] Generated complete architecture analysis
- [ ] Identified 5+ modernization opportunities
- [ ] Created dependency graph
- [ ] Documented security concerns

#### Chapter 4 Completion Criteria
- [ ] Migrated 3+ backend controllers
- [ ] Updated 5+ React components
- [ ] Implemented modern testing patterns
- [ ] Validated deployment pipeline
```

## 🛠️ Content Management Workflows

### Documentation Updates
```bash
# Update workshop content
git checkout -b update-chapter-4
# Edit relevant files
git add workshop-docs/02-hands-on/WORKSHOP-CHAPTER4.md
git commit -m "Update Chapter 4 with new Spring Boot 3.x patterns"
git push origin update-chapter-4
```

### Content Validation
```bash
# Validate all documentation
./scripts/validate-i18n.sh
./scripts/validate-codelab.sh

# Check for broken links
find workshop-docs -name "*.md" -exec markdown-link-check {} \;

# Verify code examples
grep -r "```java" workshop-docs/ | wc -l  # Count Java examples
grep -r "```javascript" workshop-docs/ | wc -l  # Count JS examples
```

### Preview Generation
```bash
# Generate local previews
./scripts/generate-preview.sh

# Serve documentation locally
cd docs && python -m http.server 8000
```

## 🎯 GitHub Copilot Prompts for Documentation

### Content Creation
```
"Generate a comprehensive workshop chapter on React modernization including hands-on exercises, code examples, and validation checkpoints"

"Create a bilingual prompt library for Spring Boot migration with both Japanese and English versions"

"Develop troubleshooting guide for common Azure deployment issues in educational environments"
```

### Content Review
```
"Review this workshop chapter for technical accuracy and educational effectiveness"

"Suggest improvements to this GitHub Copilot prompt library for better learning outcomes"

"Validate this Azure deployment guide for workshop participants with limited cloud experience"
```

### Localization
```
"Translate this technical workshop content from Japanese to English while maintaining educational clarity"

"Adapt this enterprise-focused content for academic workshop settings"

"Create executive summary of this technical workshop chapter for management stakeholders"
```

## 📊 Content Metrics and Analytics

### Workshop Effectiveness Tracking
```markdown
### Learning Outcome Metrics
- **Completion Rate**: % of participants finishing each chapter
- **Time Allocation**: Actual vs. planned duration per section
- **Difficulty Assessment**: Participant feedback on complexity levels
- **GitHub Copilot Usage**: Effectiveness of prompt patterns

### Content Quality Indicators
- **Code Example Coverage**: Number of working code samples per chapter
- **Multilingual Completeness**: Japanese/English content parity
- **Interactive Elements**: Checkpoints and validation steps per chapter
- **External Dependencies**: Links, tools, and resource availability
```

### Continuous Improvement Process
```markdown
### Monthly Content Review
1. **Participant Feedback Analysis**: Review workshop surveys and GitHub issues
2. **Technology Updates**: Update for new Spring Boot/React/Azure features
3. **Prompt Optimization**: Refine GitHub Copilot prompts based on effectiveness
4. **Content Accuracy**: Verify all code examples and deployment procedures

### Quarterly Major Updates
1. **Framework Migrations**: Update for major version changes
2. **New Learning Patterns**: Incorporate emerging best practices
3. **Platform Updates**: Adapt for Azure service changes
4. **Accessibility Improvements**: Enhance multilingual and accessibility features
```

This documentation framework ensures consistent, high-quality educational content that effectively teaches modern development practices through hands-on experience with AI-assisted development tools.
