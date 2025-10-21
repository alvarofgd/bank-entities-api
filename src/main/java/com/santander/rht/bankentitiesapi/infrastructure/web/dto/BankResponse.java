package com.santander.rht.bankentitiesapi.infrastructure.web.dto;

import com.santander.rht.bankentitiesapi.domain.model.BankType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for bank response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankResponse {
    
    private Long id;
    private String swiftCode;
    private String name;
    private String address;
    private String city;
    private String country;
    private String countryCode;
    private String phoneNumber;
    private String email;
    private String website;
    private BankType bankType;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}