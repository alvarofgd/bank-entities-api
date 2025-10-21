package com.santander.rht.bankentitiesapi.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.CreateBankRequest;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.UpdateBankRequest;
import com.santander.rht.bankentitiesapi.domain.model.BankType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@Transactional
class BankIntegrationTest {
    
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void createBank_ValidRequest_Success() throws Exception {
        // Given
        CreateBankRequest request = CreateBankRequest.builder()
                .swiftCode("TESTBNK1")
                .name("Test Bank")
                .address("Test Address")
                .city("Test City")
                .country("Test Country")
                .countryCode("TC")
                .phoneNumber("+1234567890")
                .email("test@testbank.com")
                .website("https://testbank.com")
                .bankType(BankType.COMMERCIAL)
                .active(true)
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.swiftCode").value("TESTBNK1"))
                .andExpect(jsonPath("$.name").value("Test Bank"))
                .andExpect(jsonPath("$.id").exists());
    }    @Test
    void createBank_DuplicateSwiftCode_ReturnsConflict() throws Exception {
        // Given
        CreateBankRequest request1 = CreateBankRequest.builder()
                .swiftCode("DUPLICAT")
                .name("Bank One")
                .build();

        CreateBankRequest request2 = CreateBankRequest.builder()
                .swiftCode("DUPLICAT")
                .name("Bank Two")
                .build();

        // When - Create first bank
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Then - Try to create duplicate
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("DUPLICATE_BANK"));
    }
    
    @Test
    void getBankById_ExistingBank_ReturnsBank() throws Exception {
        // Given - Create a bank first
        CreateBankRequest createRequest = CreateBankRequest.builder()
                .swiftCode("GETTEST1")
                .name("Get Test Bank")
                .build();
        
        String createResponse = mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Parse the response to get the bank ID
        Long bankId = objectMapper.readTree(createResponse).get("id").asLong();
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks/" + bankId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.swiftCode").value("GETTEST1"))
                .andExpect(jsonPath("$.name").value("Get Test Bank"));
    }
    
    @Test
    void getBankById_NonExistingBank_ReturnsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/banks/999999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void updateBank_ExistingBank_Success() throws Exception {
        // Given - Create a bank first
        CreateBankRequest createRequest = CreateBankRequest.builder()
                .swiftCode("UPDATEST")
                .name("Original Bank Name")
                .build();
        
        String createResponse = mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Parse the response to get the bank ID
        Long bankId = objectMapper.readTree(createResponse).get("id").asLong();
        
        UpdateBankRequest updateRequest = UpdateBankRequest.builder()
                .swiftCode("UPDATEST")
                .name("Updated Bank Name")
                .address("Updated Address")
                .build();
        
        // When & Then
        mockMvc.perform(put("/api/v1/banks/" + bankId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Bank Name"))
                .andExpect(jsonPath("$.address").value("Updated Address"));
    }
    
    @Test
    void deleteBank_ExistingBank_Success() throws Exception {
        // Given - Create a bank first
        CreateBankRequest createRequest = CreateBankRequest.builder()
                .swiftCode("DELETEST")
                .name("Bank to Delete")
                .build();
        
        String createResponse = mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Parse the response to get the bank ID
        Long bankId = objectMapper.readTree(createResponse).get("id").asLong();
        
        // When - Delete the bank
        mockMvc.perform(delete("/api/v1/banks/" + bankId))
                .andExpect(status().isNoContent());
        
        // Then - Verify it's deleted
        mockMvc.perform(get("/api/v1/banks/" + bankId))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void getAllBanks_ReturnsAllBanks() throws Exception {
        // Given - Create multiple banks
        CreateBankRequest request1 = CreateBankRequest.builder()
                .swiftCode("GETALL01")
                .name("Bank One")
                .build();
        
        CreateBankRequest request2 = CreateBankRequest.builder()
                .swiftCode("GETALL02")
                .name("Bank Two")
                .build();
        
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }
    
    @Test
    void searchBanksByName_ReturnsMatchingBanks() throws Exception {
        // Given - Create banks with searchable names
        CreateBankRequest request1 = CreateBankRequest.builder()
                .swiftCode("SEARCH01")
                .name("Santander Bank")
                .build();
        
        CreateBankRequest request2 = CreateBankRequest.builder()
                .swiftCode("SEARCH02")
                .name("BBVA Bank")
                .build();
        
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());
        
        mockMvc.perform(post("/api/v1/banks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated());
        
        // When & Then
        mockMvc.perform(get("/api/v1/banks?name=Santander"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Santander Bank"));
    }
}