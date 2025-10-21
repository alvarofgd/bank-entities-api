package com.santander.rht.bankentitiesapi.domain.exception;

/**
 * Exception thrown when trying to create a bank with a SWIFT code that already exists
 */
public class DuplicateBankException extends DomainException {
    
    public DuplicateBankException(String message) {
        super(message);
    }
    
    public static DuplicateBankException bySwiftCode(String swiftCode) {
        return new DuplicateBankException("Bank already exists with SWIFT code: " + swiftCode);
    }
}