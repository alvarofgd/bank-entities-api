package com.santander.rht.bankentitiesapi.domain.port.out;

import com.santander.rht.bankentitiesapi.domain.model.Bank;

import java.util.Optional;

/**
 * Output port for external HTTP client to call own endpoints
 * This interface is implemented by the infrastructure layer
 */
public interface BankHttpClientPort {
    
    /**
     * Call the GET /banks/{id} endpoint of this same service
     */
    Optional<Bank> getBankById(Long id);
    
    /**
     * Call the GET /banks/swift/{swiftCode} endpoint of this same service
     */
    Optional<Bank> getBankBySwiftCode(String swiftCode);
}