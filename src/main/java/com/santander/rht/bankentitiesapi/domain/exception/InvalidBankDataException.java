package com.santander.rht.bankentitiesapi.domain.exception;

/**
 * Exception thrown when bank validation fails
 */
public class InvalidBankDataException extends DomainException {
    
    public InvalidBankDataException(String message) {
        super(message);
    }
    
    public static InvalidBankDataException invalidSwiftCode(String swiftCode) {
        return new InvalidBankDataException("Invalid SWIFT code format: " + swiftCode);
    }
    
    public static InvalidBankDataException missingRequiredField(String fieldName) {
        return new InvalidBankDataException("Required field is missing or empty: " + fieldName);
    }
}