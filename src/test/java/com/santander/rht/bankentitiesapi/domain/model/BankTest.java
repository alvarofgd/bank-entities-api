package com.santander.rht.bankentitiesapi.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankTest {
    
    @Test
    void isValidSwiftCode_ValidEightCharacterCode_ReturnsTrue() {
        // Given
        Bank bank = Bank.builder()
                .swiftCode("SANDESM1")
                .build();
        
        // When
        boolean result = bank.isValidSwiftCode();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    void isValidSwiftCode_ValidElevenCharacterCode_ReturnsTrue() {
        // Given
        Bank bank = Bank.builder()
                .swiftCode("SANDESMMXXX")
                .build();
        
        // When
        boolean result = bank.isValidSwiftCode();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    void isValidSwiftCode_NullCode_ReturnsFalse() {
        // Given
        Bank bank = Bank.builder()
                .swiftCode(null)
                .build();
        
        // When
        boolean result = bank.isValidSwiftCode();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void isValidSwiftCode_EmptyCode_ReturnsFalse() {
        // Given
        Bank bank = Bank.builder()
                .swiftCode("")
                .build();
        
        // When
        boolean result = bank.isValidSwiftCode();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void isValidSwiftCode_InvalidLength_ReturnsFalse() {
        // Given
        Bank bank = Bank.builder()
                .swiftCode("SAND")
                .build();
        
        // When
        boolean result = bank.isValidSwiftCode();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void isValidSwiftCode_InvalidCharacters_ReturnsFalse() {
        // Given
        Bank bank = Bank.builder()
                .swiftCode("SAND@SM1")
                .build();
        
        // When
        boolean result = bank.isValidSwiftCode();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void isOperational_ActiveTrue_ReturnsTrue() {
        // Given
        Bank bank = Bank.builder()
                .active(true)
                .build();
        
        // When
        boolean result = bank.isOperational();
        
        // Then
        assertThat(result).isTrue();
    }
    
    @Test
    void isOperational_ActiveFalse_ReturnsFalse() {
        // Given
        Bank bank = Bank.builder()
                .active(false)
                .build();
        
        // When
        boolean result = bank.isOperational();
        
        // Then
        assertThat(result).isFalse();
    }
    
    @Test
    void isOperational_ActiveNull_ReturnsFalse() {
        // Given
        Bank bank = Bank.builder()
                .active(null)
                .build();
        
        // When
        boolean result = bank.isOperational();
        
        // Then
        assertThat(result).isFalse();
    }
}