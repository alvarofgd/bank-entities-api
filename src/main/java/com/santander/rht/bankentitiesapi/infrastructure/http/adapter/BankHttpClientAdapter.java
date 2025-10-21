package com.santander.rht.bankentitiesapi.infrastructure.http.adapter;

import com.santander.rht.bankentitiesapi.domain.model.Bank;
import com.santander.rht.bankentitiesapi.domain.port.out.BankHttpClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Optional;

/**
 * Adapter that implements the BankHttpClientPort using WebClient
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BankHttpClientAdapter implements BankHttpClientPort {
    
    private final WebClient.Builder webClientBuilder;
    
    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;
    
    private WebClient getWebClient() {
        return webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }
    
    @Override
    public Optional<Bank> getBankById(Long id) {
        log.debug("Self-calling GET /api/v1/banks/{} endpoint", id);
        
        try {
            Bank bank = getWebClient()
                    .get()
                    .uri("/api/v1/banks/{id}", id)
                    .retrieve()
                    .bodyToMono(Bank.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            log.debug("Self-call successful for bank ID: {}", id);
            return Optional.ofNullable(bank);
            
        } catch (Exception e) {
            log.error("Error during self-call for bank ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<Bank> getBankBySwiftCode(String swiftCode) {
        log.debug("Self-calling GET /api/v1/banks/swift/{} endpoint", swiftCode);
        
        try {
            Bank bank = getWebClient()
                    .get()
                    .uri("/api/v1/banks/swift/{swiftCode}", swiftCode)
                    .retrieve()
                    .bodyToMono(Bank.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
            
            log.debug("Self-call successful for SWIFT code: {}", swiftCode);
            return Optional.ofNullable(bank);
            
        } catch (Exception e) {
            log.error("Error during self-call for SWIFT code {}: {}", swiftCode, e.getMessage());
            return Optional.empty();
        }
    }
}