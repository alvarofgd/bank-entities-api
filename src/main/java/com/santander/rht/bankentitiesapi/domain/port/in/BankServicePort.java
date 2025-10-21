package com.santander.rht.bankentitiesapi.domain.port.in;

import com.santander.rht.bankentitiesapi.domain.model.Bank;

import java.util.List;
import java.util.Optional;

/**
 * Input port for Bank use cases
 * This interface is implemented by the application service layer
 */
public interface BankServicePort {
    
    /**
     * Create a new bank
     */
    Bank createBank(Bank bank);
    
    /**
     * Update an existing bank
     */
    Bank updateBank(Long id, Bank bank);
    
    /**
     * Get a bank by its ID
     */
    Optional<Bank> getBankById(Long id);
    
    /**
     * Get a bank by its SWIFT code
     */
    Optional<Bank> getBankBySwiftCode(String swiftCode);
    
    /**
     * Get all banks
     */
    List<Bank> getAllBanks();
    
    /**
     * Get banks by country
     */
    List<Bank> getBanksByCountry(String country);
    
    /**
     * Get banks by country code
     */
    List<Bank> getBanksByCountryCode(String countryCode);
    
    /**
     * Search banks by name
     */
    List<Bank> searchBanksByName(String name);
    
    /**
     * Get only active banks
     */
    List<Bank> getActiveBanks();
    
    /**
     * Delete a bank by its ID
     */
    void deleteBank(Long id);
    
    /**
     * Self-call to get bank by ID using HTTP client
     */
    Optional<Bank> selfCallGetBankById(Long id);
    
    /**
     * Self-call to get bank by SWIFT code using HTTP client
     */
    Optional<Bank> selfCallGetBankBySwiftCode(String swiftCode);
}