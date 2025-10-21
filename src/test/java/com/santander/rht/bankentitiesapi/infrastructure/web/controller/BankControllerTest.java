package com.santander.rht.bankentitiesapi.infrastructure.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.rht.bankentitiesapi.domain.exception.BankNotFoundException;
import com.santander.rht.bankentitiesapi.domain.exception.DuplicateBankException;
import com.santander.rht.bankentitiesapi.domain.model.Bank;
import com.santander.rht.bankentitiesapi.domain.model.BankType;
import com.santander.rht.bankentitiesapi.domain.port.in.BankServicePort;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.BankResponse;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.CreateBankRequest;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.UpdateBankRequest;
import com.santander.rht.bankentitiesapi.infrastructure.web.mapper.BankWebMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankController.class)
class BankControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockitoBean
    private BankServicePort bankServicePort;
    
    @MockitoBean
    private BankWebMapper bankWebMapper;
    
    private Bank validBank;
    private CreateBankRequest createRequest;
    private UpdateBankRequest updateRequest;
    private BankResponse bankResponse;
    
    @BeforeEach
    void setUp() {
        validBank = Bank.builder()
                .id(1L)
                .swiftCode("SANDESMMXXX")
                .name("Banco Santander")
                .address("Paseo de la Castellana 83-85")
                .city("Madrid")
                .country("Spain")
                .countryCode("ES")
                .phoneNumber("+34915123000")
                .email("info@santander.es")
                .website("https://www.santander.es")
                .bankType(BankType.COMMERCIAL)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        createRequest = CreateBankRequest.builder()
                .swiftCode("SANDESMMXXX")
                .name("Banco Santander")
                .address("Paseo de la Castellana 83-85")
                .city("Madrid")
                .country("Spain")
                .countryCode("ES")
                .phoneNumber("+34915123000")
                .email("info@santander.es")
                .website("https://www.santander.es")
                .bankType(BankType.COMMERCIAL)
                .active(true)
                .build();
        
        updateRequest = UpdateBankRequest.builder()
                .swiftCode("SANDESMMXXX")
                .name("Updated Bank Name")
                .address("Updated Address")
                .city("Madrid")
                .country("Spain")
                .countryCode("ES")
                .phoneNumber("+34915123000")
                .email("info@santander.es")
                .website("https://www.santander.es")
                .bankType(BankType.COMMERCIAL)
                .active(true)
                .build();
        
        bankResponse = BankResponse.builder()
                .id(1L)
                .swiftCode("SANDESMMXXX")
                .name("Banco Santander")
                .build();
    }
    
    @Test
    void createBank_ValidRequest_ReturnsCreated() throws Exception {
        // Given  
        when(bankWebMapper.toDomain(any(CreateBankRequest.class))).thenReturn(validBank);
        when(bankServicePort.createBank(any(Bank.class))).thenReturn(validBank);
        when(bankWebMapper.toResponse(any(Bank.class))).thenReturn(bankResponse);
        
        // When & Then
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.swiftCode").value("SANDESMMXXX"));
    }
    
    @Test
    void createBank_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Given - Only provide SWIFT code without required fields to trigger validation error
        String invalidRequestJson = "{\"swiftCode\":\"INVALID\"}";
        
        // When & Then
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void createBank_DuplicateSwiftCode_ReturnsConflict() throws Exception {
        // Given
        when(bankWebMapper.toDomain(any(CreateBankRequest.class))).thenReturn(validBank);
        when(bankServicePort.createBank(any(Bank.class)))
                .thenThrow(DuplicateBankException.bySwiftCode("SANDESMMXXX"));
        
        // When & Then
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_BANK"));
    }
    
    @Test
    void getBankById_ExistingBank_ReturnsOk() throws Exception {
        // Given
        when(bankServicePort.getBankById(1L)).thenReturn(Optional.of(validBank));
        when(bankWebMapper.toResponse(any(Bank.class))).thenReturn(bankResponse);
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks/1"))
                .andExpect(status().isOk());
    }
    
    @Test
    void getBankById_NonExistingBank_ReturnsNotFound() throws Exception {
        // Given
        when(bankServicePort.getBankById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getBankBySwiftCode_ExistingBank_ReturnsOk() throws Exception {
        // Given
        when(bankServicePort.getBankBySwiftCode("SANDESMMXXX")).thenReturn(Optional.of(validBank));
        when(bankWebMapper.toResponse(any(Bank.class))).thenReturn(bankResponse);
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks/swift/SANDESMMXXX"))
                .andExpect(status().isOk());
    }
    
    @Test
    void getAllBanks_ReturnsOk() throws Exception {
        // Given
        when(bankServicePort.getAllBanks()).thenReturn(List.of(validBank));
        when(bankWebMapper.toResponseList(any())).thenReturn(List.of());
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks"))
                .andExpect(status().isOk());
    }
    
    @Test
    void getAllBanks_WithCountryFilter_ReturnsOk() throws Exception {
        // Given
        when(bankServicePort.getBanksByCountry("Spain")).thenReturn(List.of(validBank));
        when(bankWebMapper.toResponseList(any())).thenReturn(List.of());
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks?country=Spain"))
                .andExpect(status().isOk());
    }
    
    @Test
    void updateBank_ValidRequest_ReturnsOk() throws Exception {
        // Given
        when(bankWebMapper.toDomain(any(UpdateBankRequest.class))).thenReturn(validBank);
        when(bankServicePort.updateBank(anyLong(), any(Bank.class))).thenReturn(validBank);
        when(bankWebMapper.toResponse(any(Bank.class))).thenReturn(bankResponse);
        
        // When & Then
        mockMvc.perform(put("/api/v1/banks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());
    }
    
    @Test
    void updateBank_NonExistingBank_ReturnsNotFound() throws Exception {
        // Given
        when(bankWebMapper.toDomain(any(UpdateBankRequest.class))).thenReturn(validBank);
        when(bankServicePort.updateBank(anyLong(), any(Bank.class)))
                .thenThrow(BankNotFoundException.byId(999L));
        
        // When & Then
        mockMvc.perform(put("/api/v1/banks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("BANK_NOT_FOUND"));
    }
    
    @Test
    void deleteBank_ExistingBank_ReturnsNoContent() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/banks/1"))
                .andExpect(status().isNoContent());
    }
    
    @Test
    void deleteBank_NonExistingBank_ReturnsNotFound() throws Exception {
        // Given
        doThrow(BankNotFoundException.byId(999L)).when(bankServicePort).deleteBank(999L);
        
        // When & Then
        mockMvc.perform(delete("/api/v1/banks/999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void selfCallGetBankById_ReturnsOk() throws Exception {
        // Given
        when(bankServicePort.selfCallGetBankById(1L)).thenReturn(Optional.of(validBank));
        when(bankWebMapper.toResponse(any(Bank.class))).thenReturn(bankResponse);
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks/1/self-call"))
                .andExpect(status().isOk());
    }
    
    @Test
    void selfCallGetBankBySwiftCode_ReturnsOk() throws Exception {
        // Given
        when(bankServicePort.selfCallGetBankBySwiftCode("SANDESMMXXX")).thenReturn(Optional.of(validBank));
        when(bankWebMapper.toResponse(any(Bank.class))).thenReturn(bankResponse);
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks/swift/SANDESMMXXX/self-call"))
                .andExpect(status().isOk());
    }
}