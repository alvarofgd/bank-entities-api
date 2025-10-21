package com.santander.rht.bankentitiesapi.domain.port.out;

import com.santander.rht.bankentitiesapi.domain.model.Bank;

import java.util.List;
import java.util.Optional;

/**
 * Output port for Bank repository operations
 * This interface is implemented by the infrastructure layer
 */
public interface BankRepositoryPort {
    
    /**
     * Save a new bank or update an existing one
     */
    Bank save(Bank bank);
    
    /**
     * Find a bank by its ID
     */
    Optional<Bank> findById(Long id);
    
    /**
     * Find a bank by its SWIFT code
     */
    Optional<Bank> findBySwiftCode(String swiftCode);
    
    /**
     * Find all banks
     */
    List<Bank> findAll();
    
    /**
     * Find banks by country
     */
    List<Bank> findByCountry(String country);
    
    /**
     * Find banks by country code
     */
    List<Bank> findByCountryCode(String countryCode);
    
    /**
     * Find banks by name (case-insensitive partial match)
     */
    List<Bank> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find only active banks
     */
    List<Bank> findByActiveTrue();
    
    /**
     * Check if a bank exists by SWIFT code
     */
    boolean existsBySwiftCode(String swiftCode);
    
    /**
     * Delete a bank by its ID
     */
    void deleteById(Long id);
    
    /**
     * Count total number of banks
     */
    long count();
}