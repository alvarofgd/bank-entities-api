# Security Policy

## 🔒 Supported Versions

We actively support and provide security updates for the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.x.x   | ✅ Yes             |
| < 1.0   | ❌ No              |

## 🚨 Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security vulnerability, please follow these steps:

### 📧 Private Disclosure

**DO NOT** open a public GitHub issue for security vulnerabilities.

Instead, please report security vulnerabilities through one of these methods:

1. **GitHub Security Advisories** (Preferred)
   - Go to the [Security tab](https://github.com/alvarofgd/bank-entities-api/security)
   - Click "Report a vulnerability"
   - Fill out the form with details

2. **Email**
   - Send an email to: `security@your-domain.com`
   - Include "SECURITY" in the subject line
   - Provide detailed information about the vulnerability

### 📋 Information to Include

Please include the following information in your report:

- **Type of vulnerability** (e.g., SQL injection, XSS, authentication bypass)
- **Location** of the vulnerable code (file path, line number if possible)
- **Description** of the vulnerability and its potential impact
- **Steps to reproduce** the vulnerability
- **Proof of concept** or exploit code (if applicable)
- **Suggested fix** (if you have one)

### 🕐 Response Timeline

- **Initial Response**: Within 48 hours
- **Triage**: Within 1 week
- **Fix Development**: Depends on severity
  - Critical: Within 1 week
  - High: Within 2 weeks
  - Medium: Within 1 month
  - Low: Next scheduled release

### 🏆 Recognition

We appreciate security researchers who help improve our security. Upon successful resolution of a reported vulnerability:

- We will acknowledge your contribution (unless you prefer to remain anonymous)
- We may include you in our security hall of fame
- For significant vulnerabilities, we may offer recognition in our release notes

## 🛡️ Security Best Practices

### For Users

1. **Keep Updated**: Always use the latest stable version
2. **Secure Configuration**: Follow our security configuration guide
3. **Environment Variables**: Never commit sensitive data to version control
4. **Network Security**: Use HTTPS in production
5. **Database Security**: Use strong passwords and restrict access

### For Developers

1. **Input Validation**: Validate all user inputs
2. **SQL Injection**: Use parameterized queries
3. **Authentication**: Implement proper authentication and authorization
4. **Session Management**: Use secure session handling
5. **Error Handling**: Don't expose sensitive information in error messages

## 🔐 Security Features

### Application Security

- **Input Validation**: All API inputs are validated
- **SQL Injection Protection**: Uses JPA/Hibernate with parameterized queries
- **CORS Configuration**: Properly configured CORS headers
- **Security Headers**: Implements security-related HTTP headers
- **Error Handling**: Sanitized error responses

### Infrastructure Security

- **Database Security**: PostgreSQL with authentication
- **Container Security**: Regular base image updates
- **Network Security**: Internal Docker networking
- **Monitoring**: Comprehensive logging and monitoring

### Dependencies

- **Dependency Scanning**: Regular vulnerability scans
- **Updates**: Prompt security updates for dependencies
- **Audit**: Regular security audits of third-party components

## 📊 Security Monitoring

We use various tools and practices to maintain security:

- **Static Code Analysis**: SonarQube integration
- **Dependency Scanning**: Maven dependency-check plugin
- **Container Scanning**: Trivy vulnerability scanner
- **Automated Testing**: Security-focused unit and integration tests

## 📚 Security Resources

### Documentation

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- [Docker Security Best Practices](https://docs.docker.com/engine/security/)

### Tools Used

- **SAST**: SonarQube
- **Dependency Check**: OWASP Dependency Check
- **Container Scanning**: Trivy
- **Security Headers**: Spring Security

## 🔄 Security Updates

Security updates will be:

1. **Tested** thoroughly before release
2. **Documented** with clear upgrade instructions
3. **Announced** through GitHub releases
4. **Backported** to supported versions when necessary

## 📞 Contact

For non-security related issues, please use:
- GitHub Issues for bugs and feature requests
- GitHub Discussions for questions and general discussion

For security matters only:
- GitHub Security Advisories (preferred)
- Email: `security@your-domain.com`

---

**Note**: This security policy is subject to change. Please check back regularly for updates.
