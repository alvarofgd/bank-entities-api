package com.santander.rht.bankentitiesapi.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Bank domain entity representing a banking institution.
 * Uses SWIFT code as unique identifier to prevent duplicates.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bank {
    
    private Long id;
    
    /**
     * SWIFT (Society for Worldwide Interbank Financial Telecommunication) code
     * Unique identifier for banks worldwide (8 or 11 characters)
     */
    private String swiftCode;
    
    /**
     * Official name of the bank
     */
    private String name;
    
    /**
     * Full address of the bank's headquarters
     */
    private String address;
    
    /**
     * City where the bank is located
     */
    private String city;
    
    /**
     * Country where the bank is located
     */
    private String country;
    
    /**
     * ISO country code (2 letters)
     */
    private String countryCode;
    
    /**
     * Phone number of the bank
     */
    private String phoneNumber;
    
    /**
     * Email address of the bank
     */
    private String email;
    
    /**
     * Official website URL
     */
    private String website;
    
    /**
     * Type of banking institution (Commercial, Investment, Central, etc.)
     */
    private BankType bankType;
    
    /**
     * Whether the bank is currently active
     */
    private Boolean active;
    
    /**
     * Timestamp when the bank was created in the system
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the bank was last updated
     */
    private LocalDateTime updatedAt;
    
    /**
     * Validates if the SWIFT code format is correct
     */
    public boolean isValidSwiftCode() {
        if (swiftCode == null || swiftCode.trim().isEmpty()) {
            return false;
        }
        // SWIFT code should be 8 or 11 characters, alphanumeric
        String cleanSwift = swiftCode.trim().toUpperCase();
        return cleanSwift.matches("^[A-Z0-9]{8}([A-Z0-9]{3})?$");
    }
    
    /**
     * Checks if the bank is active and operational
     */
    public boolean isOperational() {
        return active != null && active;
    }
}