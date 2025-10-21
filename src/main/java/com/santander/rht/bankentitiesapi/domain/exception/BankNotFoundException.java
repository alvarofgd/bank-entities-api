package com.santander.rht.bankentitiesapi.domain.exception;

/**
 * Exception thrown when a bank is not found
 */
public class BankNotFoundException extends DomainException {
    
    public BankNotFoundException(String message) {
        super(message);
    }
    
    public static BankNotFoundException byId(Long id) {
        return new BankNotFoundException("Bank not found with id: " + id);
    }
    
    public static BankNotFoundException bySwiftCode(String swiftCode) {
        return new BankNotFoundException("Bank not found with SWIFT code: " + swiftCode);
    }
}