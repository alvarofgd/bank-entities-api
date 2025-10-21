package com.santander.rht.bankentitiesapi.domain.model;

/**
 * Enumeration of different types of banking institutions
 */
public enum BankType {
    COMMERCIAL("Commercial Bank"),
    INVESTMENT("Investment Bank"),
    CENTRAL("Central Bank"),
    COOPERATIVE("Cooperative Bank"),
    SAVINGS("Savings Bank"),
    CREDIT_UNION("Credit Union"),
    ONLINE("Online Bank"),
    PRIVATE("Private Bank"),
    DEVELOPMENT("Development Bank"),
    EXPORT_IMPORT("Export-Import Bank");
    
    private final String displayName;
    
    BankType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}