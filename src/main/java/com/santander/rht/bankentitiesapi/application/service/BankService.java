package com.santander.rht.bankentitiesapi.application.service;

import com.santander.rht.bankentitiesapi.domain.exception.BankNotFoundException;
import com.santander.rht.bankentitiesapi.domain.exception.DuplicateBankException;
import com.santander.rht.bankentitiesapi.domain.exception.InvalidBankDataException;
import com.santander.rht.bankentitiesapi.domain.model.Bank;
import com.santander.rht.bankentitiesapi.domain.port.in.BankServicePort;
import com.santander.rht.bankentitiesapi.domain.port.out.BankHttpClientPort;
import com.santander.rht.bankentitiesapi.domain.port.out.BankRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Application service implementing bank business logic
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BankService implements BankServicePort {
    
    private final BankRepositoryPort bankRepositoryPort;
    private final BankHttpClientPort bankHttpClientPort;
    
    @Override
    public Bank createBank(Bank bank) {
        log.info("Creating new bank with SWIFT code: {}", bank.getSwiftCode());
        
        // Validate bank data
        validateBankData(bank);
        
        // Check for duplicates
        if (bankRepositoryPort.existsBySwiftCode(bank.getSwiftCode())) {
            throw DuplicateBankException.bySwiftCode(bank.getSwiftCode());
        }
        
        // Set creation timestamp and ensure active status
        bank.setCreatedAt(LocalDateTime.now());
        bank.setUpdatedAt(LocalDateTime.now());
        if (bank.getActive() == null) {
            bank.setActive(true);
        }
        
        Bank savedBank = bankRepositoryPort.save(bank);
        log.info("Bank created successfully with ID: {}", savedBank.getId());
        return savedBank;
    }
    
    @Override
    public Bank updateBank(Long id, Bank bank) {
        log.info("Updating bank with ID: {}", id);
        
        // Check if bank exists
        Bank existingBank = bankRepositoryPort.findById(id)
                .orElseThrow(() -> BankNotFoundException.byId(id));
        
        // Validate bank data
        validateBankData(bank);
        
        // Check for SWIFT code duplicates (excluding current bank)
        if (!existingBank.getSwiftCode().equals(bank.getSwiftCode()) && 
            bankRepositoryPort.existsBySwiftCode(bank.getSwiftCode())) {
            throw DuplicateBankException.bySwiftCode(bank.getSwiftCode());
        }
        
        // Update fields
        bank.setId(id);
        bank.setCreatedAt(existingBank.getCreatedAt());
        bank.setUpdatedAt(LocalDateTime.now());
        
        Bank updatedBank = bankRepositoryPort.save(bank);
        log.info("Bank updated successfully with ID: {}", id);
        return updatedBank;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Bank> getBankById(Long id) {
        log.debug("Getting bank by ID: {}", id);
        return bankRepositoryPort.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Bank> getBankBySwiftCode(String swiftCode) {
        log.debug("Getting bank by SWIFT code: {}", swiftCode);
        return bankRepositoryPort.findBySwiftCode(swiftCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Bank> getAllBanks() {
        log.debug("Getting all banks");
        return bankRepositoryPort.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Bank> getBanksByCountry(String country) {
        log.debug("Getting banks by country: {}", country);
        return bankRepositoryPort.findByCountry(country);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Bank> getBanksByCountryCode(String countryCode) {
        log.debug("Getting banks by country code: {}", countryCode);
        return bankRepositoryPort.findByCountryCode(countryCode);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Bank> searchBanksByName(String name) {
        log.debug("Searching banks by name: {}", name);
        return bankRepositoryPort.findByNameContainingIgnoreCase(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Bank> getActiveBanks() {
        log.debug("Getting active banks");
        return bankRepositoryPort.findByActiveTrue();
    }
    
    @Override
    public void deleteBank(Long id) {
        log.info("Deleting bank with ID: {}", id);
        
        // Check if bank exists
        if (!bankRepositoryPort.findById(id).isPresent()) {
            throw BankNotFoundException.byId(id);
        }
        
        bankRepositoryPort.deleteById(id);
        log.info("Bank deleted successfully with ID: {}", id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Bank> selfCallGetBankById(Long id) {
        log.info("Self-calling to get bank by ID: {}", id);
        return bankHttpClientPort.getBankById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Bank> selfCallGetBankBySwiftCode(String swiftCode) {
        log.info("Self-calling to get bank by SWIFT code: {}", swiftCode);
        return bankHttpClientPort.getBankBySwiftCode(swiftCode);
    }
    
    private void validateBankData(Bank bank) {
        if (bank.getSwiftCode() == null || bank.getSwiftCode().trim().isEmpty()) {
            throw InvalidBankDataException.missingRequiredField("swiftCode");
        }
        
        if (bank.getName() == null || bank.getName().trim().isEmpty()) {
            throw InvalidBankDataException.missingRequiredField("name");
        }
        
        // Validate SWIFT code format
        if (!bank.isValidSwiftCode()) {
            throw InvalidBankDataException.invalidSwiftCode(bank.getSwiftCode());
        }
        
        // Normalize SWIFT code to uppercase
        bank.setSwiftCode(bank.getSwiftCode().trim().toUpperCase());
    }
}