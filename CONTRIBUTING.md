# Contributing to Bank Entities API

Thank you for considering contributing to the Bank Entities API! This document provides guidelines and information for contributors.

## 🚀 Getting Started

### Prerequisites
- **Java 23** or higher
- **Maven 3.8+** (or use the included wrapper `./mvnw`)
- **Docker** and **Docker Compose**
- **Git**

### Development Setup

1. **Fork the repository**
   ```bash
   # Fork on GitHub, then clone your fork
   git clone https://github.com/YOUR_USERNAME/bank-entities-api.git
   cd bank-entities-api
   ```

2. **Set up the development environment**
   ```bash
   # Start the infrastructure services
   ./start-app.sh
   
   # Or start manually
   docker-compose up -d postgres elasticsearch
   ```

3. **Run tests to verify setup**
   ```bash
   ./mvnw clean test
   ```

## 📋 Development Workflow

### Branch Naming Convention
- `feature/feature-name` - New features
- `bugfix/bug-description` - Bug fixes
- `hotfix/critical-fix` - Critical production fixes
- `docs/documentation-update` - Documentation only
- `refactor/component-name` - Code refactoring

### Commit Message Guidelines
Follow the [Conventional Commits](https://conventionalcommits.org/) specification:

```
<type>[optional scope]: <description>

[optional body]

[optional footer(s)]
```

**Types:**
- `feat:` - New feature
- `fix:` - Bug fix
- `docs:` - Documentation only changes
- `style:` - Code style changes (formatting, etc.)
- `refactor:` - Code refactoring
- `test:` - Adding or updating tests
- `chore:` - Maintenance tasks

**Examples:**
```bash
feat(api): add SWIFT code validation endpoint
fix(database): resolve connection pool timeout issue
docs(readme): update API documentation
test(integration): add bank creation tests
```

## 🧪 Testing Guidelines

### Running Tests
```bash
# Unit tests only
./mvnw test

# All tests including integration
./mvnw verify

# Specific test class
./mvnw test -Dtest=BankServiceTest

# Integration tests with Testcontainers
./mvnw test -Dtest=BankIntegrationTest
```

### Test Coverage
- Aim for **80%+ code coverage**
- Write unit tests for business logic
- Write integration tests for API endpoints
- Use meaningful test names that describe behavior

### Test Structure
```java
@Test
@DisplayName("Should create bank when valid data is provided")
void shouldCreateBank_WhenValidDataProvided() {
    // Given
    CreateBankRequest request = createValidBankRequest();
    
    // When
    BankResponse response = bankService.createBank(request);
    
    // Then
    assertThat(response.getName()).isEqualTo(request.getName());
    assertThat(response.getSwiftCode()).isEqualTo(request.getSwiftCode());
}
```

## 🏗️ Code Standards

### Java Code Style
- Follow **Google Java Style Guide**
- Use **4 spaces** for indentation
- Maximum line length: **120 characters**
- Use meaningful variable and method names

### Architecture Guidelines
This project follows **Hexagonal Architecture**:

```
domain/          # Core business logic (no dependencies)
├── model/       # Domain entities
├── port/in/     # Input ports (use cases)
├── port/out/    # Output ports (SPI)
└── exception/   # Domain exceptions

application/     # Application services
└── service/     # Use case implementations

infrastructure/  # External adapters
├── persistence/ # Database adapters
├── web/        # REST controllers & DTOs
└── http/       # HTTP client adapters
```

### Code Quality Checks
```bash
# Run all quality checks
./mvnw clean verify

# Check for dependency vulnerabilities
./mvnw dependency-check:check

# Format code (if configured)
./mvnw spotless:apply
```

## 🔄 Pull Request Process

1. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Write code following the style guidelines
   - Add/update tests as needed
   - Update documentation if necessary

3. **Test your changes**
   ```bash
   ./mvnw clean verify
   ```

4. **Commit your changes**
   ```bash
   git add .
   git commit -m "feat: add new feature description"
   ```

5. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**
   - Use the PR template
   - Include a clear description
   - Reference related issues
   - Ensure CI passes

### Pull Request Template
```markdown
## 📋 Description
Brief description of changes

## 🔗 Related Issues
Closes #123

## 🧪 Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing completed

## 📝 Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Tests pass locally
- [ ] Documentation updated
```

## 🐛 Reporting Issues

### Bug Reports
Use the bug report template and include:
- Clear description of the problem
- Steps to reproduce
- Expected vs actual behavior
- Environment details
- Screenshots if applicable

### Feature Requests
Use the feature request template and include:
- Clear description of the feature
- Use case and motivation
- Proposed solution
- Technical considerations

## 📚 Documentation

### API Documentation
- Use OpenAPI/Swagger annotations
- Include examples in documentation
- Keep API docs up to date

### Code Documentation
- Document public APIs with Javadoc
- Include code examples where helpful
- Document complex business logic

## 🤝 Community Guidelines

### Code of Conduct
- Be respectful and inclusive
- Provide constructive feedback
- Help others learn and grow
- Follow the project's goals

### Getting Help
- Check existing issues and documentation
- Ask questions in discussions
- Be specific about your problem
- Provide context and examples

## 🏷️ Release Process

1. **Version Bump**: Update version in `pom.xml`
2. **Changelog**: Update CHANGELOG.md
3. **Tag Release**: Create Git tag
4. **GitHub Release**: Create GitHub release with notes

## 📞 Contact

- **Issues**: Use GitHub Issues
- **Discussions**: Use GitHub Discussions
- **Email**: [Your contact email if applicable]

Thank you for contributing! 🎉
