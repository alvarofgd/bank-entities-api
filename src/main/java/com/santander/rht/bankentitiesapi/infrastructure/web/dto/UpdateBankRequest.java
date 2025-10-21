package com.santander.rht.bankentitiesapi.infrastructure.web.dto;

import com.santander.rht.bankentitiesapi.domain.model.BankType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating an existing bank
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBankRequest {
    
    @NotBlank(message = "SWIFT code is required")
    @Pattern(regexp = "^[A-Z0-9]{8}([A-Z0-9]{3})?$", message = "SWIFT code must be 8 or 11 alphanumeric characters")
    private String swiftCode;
    
    @NotBlank(message = "Bank name is required")
    @Size(max = 255, message = "Bank name must not exceed 255 characters")
    private String name;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;
    
    @Pattern(regexp = "^[A-Z]{2}$", message = "Country code must be 2 uppercase letters")
    private String countryCode;
    
    @Pattern(regexp = "^\\+?[0-9\\s\\-\\(\\)]{0,20}$", message = "Invalid phone number format")
    private String phoneNumber;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;
    
    @Size(max = 255, message = "Website must not exceed 255 characters")
    private String website;
    
    private BankType bankType;
    
    private Boolean active;
}