package com.santander.rht.bankentitiesapi.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Bank Entities API documentation.
 * Provides Swagger UI with comprehensive API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bankEntitiesApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bank Entities API")
                        .description("""
                                A comprehensive RESTful microservice for Bank CRUD operations built with hexagonal architecture.
                                
                                ## Features
                                - Complete CRUD operations for Bank entities
                                - SWIFT code validation and duplicate prevention
                                - Self-calling endpoints for demonstration
                                - Advanced search capabilities
                                - Comprehensive observability and monitoring
                                
                                ## Architecture
                                This API follows hexagonal architecture principles with:
                                - Domain-driven design
                                - Clear separation of concerns
                                - Dependency inversion
                                - Comprehensive validation and error handling
                                """)
                        .version("1.0.0-SNAPSHOT")
                        .contact(new Contact()
                                .name("Santander RHT Team")
                                .email("rht-team@santander.com")
                                .url("https://github.com/santander-rht"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.bank-entities.santander.com")
                                .description("Production Server")
                ));
    }
}