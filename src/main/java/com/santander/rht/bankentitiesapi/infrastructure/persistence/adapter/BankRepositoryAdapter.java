package com.santander.rht.bankentitiesapi.infrastructure.persistence.adapter;

import com.santander.rht.bankentitiesapi.domain.model.Bank;
import com.santander.rht.bankentitiesapi.domain.port.out.BankRepositoryPort;
import com.santander.rht.bankentitiesapi.infrastructure.persistence.entity.BankEntity;
import com.santander.rht.bankentitiesapi.infrastructure.persistence.mapper.BankEntityMapper;
import com.santander.rht.bankentitiesapi.infrastructure.persistence.repository.JpaBankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements the BankRepositoryPort using JPA
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BankRepositoryAdapter implements BankRepositoryPort {
    
    private final JpaBankRepository jpaBankRepository;
    private final BankEntityMapper bankEntityMapper;
    
    @Override
    public Bank save(Bank bank) {
        log.debug("Saving bank with SWIFT code: {}", bank.getSwiftCode());
        BankEntity entity = bankEntityMapper.toEntity(bank);
        BankEntity savedEntity = jpaBankRepository.save(entity);
        Bank savedBank = bankEntityMapper.toDomain(savedEntity);
        log.debug("Bank saved with ID: {}", savedBank.getId());
        return savedBank;
    }
    
    @Override
    public Optional<Bank> findById(Long id) {
        log.debug("Finding bank by ID: {}", id);
        return jpaBankRepository.findById(id)
                .map(bankEntityMapper::toDomain);
    }
    
    @Override
    public Optional<Bank> findBySwiftCode(String swiftCode) {
        log.debug("Finding bank by SWIFT code: {}", swiftCode);
        return jpaBankRepository.findBySwiftCode(swiftCode)
                .map(bankEntityMapper::toDomain);
    }
    
    @Override
    public List<Bank> findAll() {
        log.debug("Finding all banks");
        return jpaBankRepository.findAll()
                .stream()
                .map(bankEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Bank> findByCountry(String country) {
        log.debug("Finding banks by country: {}", country);
        return jpaBankRepository.findByCountry(country)
                .stream()
                .map(bankEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Bank> findByCountryCode(String countryCode) {
        log.debug("Finding banks by country code: {}", countryCode);
        return jpaBankRepository.findByCountryCode(countryCode)
                .stream()
                .map(bankEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Bank> findByNameContainingIgnoreCase(String name) {
        log.debug("Finding banks by name containing: {}", name);
        return jpaBankRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(bankEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public List<Bank> findByActiveTrue() {
        log.debug("Finding active banks");
        return jpaBankRepository.findByActiveTrue()
                .stream()
                .map(bankEntityMapper::toDomain)
                .toList();
    }
    
    @Override
    public boolean existsBySwiftCode(String swiftCode) {
        log.debug("Checking if bank exists by SWIFT code: {}", swiftCode);
        return jpaBankRepository.existsBySwiftCode(swiftCode);
    }
    
    @Override
    public void deleteById(Long id) {
        log.debug("Deleting bank by ID: {}", id);
        jpaBankRepository.deleteById(id);
        log.debug("Bank deleted with ID: {}", id);
    }
    
    @Override
    public long count() {
        log.debug("Counting total banks");
        return jpaBankRepository.count();
    }
}