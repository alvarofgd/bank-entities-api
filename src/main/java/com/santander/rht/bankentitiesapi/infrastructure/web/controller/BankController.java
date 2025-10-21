package com.santander.rht.bankentitiesapi.infrastructure.web.controller;

import com.santander.rht.bankentitiesapi.domain.model.Bank;
import com.santander.rht.bankentitiesapi.domain.port.in.BankServicePort;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.BankResponse;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.CreateBankRequest;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.ErrorResponse;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.UpdateBankRequest;
import com.santander.rht.bankentitiesapi.infrastructure.web.mapper.BankWebMapper;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Bank operations
 */
@RestController
@RequestMapping("/api/v1/banks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bank Management", description = "Operations for managing bank entities with SWIFT code validation")
public class BankController {
    
    private final BankServicePort bankServicePort;
    private final BankWebMapper bankWebMapper;
    
    @PostMapping
    @Timed(value = "bank.create", description = "Time taken to create a bank")
    @Operation(
        summary = "Create a new bank",
        description = "Creates a new bank entity with SWIFT code validation and duplicate prevention"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Bank created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input or duplicate SWIFT code",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "422",
            description = "Validation errors",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public ResponseEntity<BankResponse> createBank(
        @Parameter(description = "Bank creation request with all required fields", required = true)
        @Valid @RequestBody CreateBankRequest request) {
        log.info("POST /api/v1/banks - Creating bank with SWIFT code: {}", request.getSwiftCode());
        
        Bank bank = bankWebMapper.toDomain(request);
        Bank createdBank = bankServicePort.createBank(bank);
        BankResponse response = bankWebMapper.toResponse(createdBank);
        
        log.info("Bank created successfully with ID: {}", response.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    @Timed(value = "bank.update", description = "Time taken to update a bank")
    public ResponseEntity<BankResponse> updateBank(@PathVariable Long id, 
                                                   @Valid @RequestBody UpdateBankRequest request) {
        log.info("PUT /api/v1/banks/{} - Updating bank", id);
        
        Bank bank = bankWebMapper.toDomain(request);
        Bank updatedBank = bankServicePort.updateBank(id, bank);
        BankResponse response = bankWebMapper.toResponse(updatedBank);
        
        log.info("Bank updated successfully with ID: {}", id);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    @Timed(value = "bank.getById", description = "Time taken to get a bank by ID")
    @Operation(
        summary = "Get bank by ID",
        description = "Retrieves a specific bank by its unique identifier"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Bank found and retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Bank not found"
        )
    })
    public ResponseEntity<BankResponse> getBankById(
        @Parameter(description = "Unique identifier of the bank", required = true) @PathVariable Long id) {
        log.debug("GET /api/v1/banks/{} - Getting bank by ID", id);
        
        Optional<Bank> bank = bankServicePort.getBankById(id);
        return bank.map(b -> {
            BankResponse response = bankWebMapper.toResponse(b);
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/swift/{swiftCode}")
    @Timed(value = "bank.getBySwiftCode", description = "Time taken to get a bank by SWIFT code")
    public ResponseEntity<BankResponse> getBankBySwiftCode(@PathVariable String swiftCode) {
        log.debug("GET /api/v1/banks/swift/{} - Getting bank by SWIFT code", swiftCode);
        
        Optional<Bank> bank = bankServicePort.getBankBySwiftCode(swiftCode);
        return bank.map(b -> {
            BankResponse response = bankWebMapper.toResponse(b);
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Timed(value = "bank.getAll", description = "Time taken to get all banks")
    @Operation(
        summary = "Get all banks",
        description = "Retrieves all banks with optional filtering by country, country code, name, or active status"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Banks retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = BankResponse.class))
        )
    })
    public ResponseEntity<List<BankResponse>> getAllBanks(
        @Parameter(description = "Filter by country name") @RequestParam(required = false) String country,
        @Parameter(description = "Filter by ISO country code") @RequestParam(required = false) String countryCode,
        @Parameter(description = "Filter by bank name") @RequestParam(required = false) String name,
        @Parameter(description = "Filter only active banks") @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        log.debug("GET /api/v1/banks - Getting banks with filters: country={}, countryCode={}, name={}, activeOnly={}", 
                  country, countryCode, name, activeOnly);
        
        List<Bank> banks;
        
        if (activeOnly) {
            banks = bankServicePort.getActiveBanks();
        } else if (country != null && !country.trim().isEmpty()) {
            banks = bankServicePort.getBanksByCountry(country);
        } else if (countryCode != null && !countryCode.trim().isEmpty()) {
            banks = bankServicePort.getBanksByCountryCode(countryCode);
        } else if (name != null && !name.trim().isEmpty()) {
            banks = bankServicePort.searchBanksByName(name);
        } else {
            banks = bankServicePort.getAllBanks();
        }
        
        List<BankResponse> responses = bankWebMapper.toResponseList(banks);
        return ResponseEntity.ok(responses);
    }
    
    @DeleteMapping("/{id}")
    @Timed(value = "bank.delete", description = "Time taken to delete a bank")
    public ResponseEntity<Void> deleteBank(@PathVariable Long id) {
        log.info("DELETE /api/v1/banks/{} - Deleting bank", id);
        
        bankServicePort.deleteBank(id);
        
        log.info("Bank deleted successfully with ID: {}", id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/self-call")
    @Timed(value = "bank.selfCall", description = "Time taken for self-call to get bank")
    public ResponseEntity<BankResponse> selfCallGetBankById(@PathVariable Long id) {
        log.info("GET /api/v1/banks/{}/self-call - Self-calling to get bank by ID", id);
        
        Optional<Bank> bank = bankServicePort.selfCallGetBankById(id);
        return bank.map(b -> {
            BankResponse response = bankWebMapper.toResponse(b);
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/swift/{swiftCode}/self-call")
    @Timed(value = "bank.selfCallBySwift", description = "Time taken for self-call to get bank by SWIFT")
    public ResponseEntity<BankResponse> selfCallGetBankBySwiftCode(@PathVariable String swiftCode) {
        log.info("GET /api/v1/banks/swift/{}/self-call - Self-calling to get bank by SWIFT code", swiftCode);
        
        Optional<Bank> bank = bankServicePort.selfCallGetBankBySwiftCode(swiftCode);
        return bank.map(b -> {
            BankResponse response = bankWebMapper.toResponse(b);
            return ResponseEntity.ok(response);
        }).orElse(ResponseEntity.notFound().build());
    }
}