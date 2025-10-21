package com.santander.rht.bankentitiesapi.application.service;

import com.santander.rht.bankentitiesapi.domain.exception.BankNotFoundException;
import com.santander.rht.bankentitiesapi.domain.exception.DuplicateBankException;
import com.santander.rht.bankentitiesapi.domain.exception.InvalidBankDataException;
import com.santander.rht.bankentitiesapi.domain.model.Bank;
import com.santander.rht.bankentitiesapi.domain.model.BankType;
import com.santander.rht.bankentitiesapi.domain.port.out.BankHttpClientPort;
import com.santander.rht.bankentitiesapi.domain.port.out.BankRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {
    
    @Mock
    private BankRepositoryPort bankRepositoryPort;
    
    @Mock
    private BankHttpClientPort bankHttpClientPort;
    
    @InjectMocks
    private BankService bankService;
    
    private Bank validBank;
    
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
    }
    
    @Test
    void createBank_ValidBank_Success() {
        // Given
        Bank newBank = Bank.builder()
                .swiftCode("SANDESMMXXX")
                .name("Banco Santander")
                .active(true)
                .build();
        
        when(bankRepositoryPort.existsBySwiftCode(anyString())).thenReturn(false);
        when(bankRepositoryPort.save(any(Bank.class))).thenReturn(validBank);
        
        // When
        Bank result = bankService.createBank(newBank);
        
        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getSwiftCode()).isEqualTo("SANDESMMXXX");
        verify(bankRepositoryPort).existsBySwiftCode("SANDESMMXXX");
        verify(bankRepositoryPort).save(any(Bank.class));
    }
    
    @Test
    void createBank_DuplicateSwiftCode_ThrowsException() {
        // Given
        Bank newBank = Bank.builder()
                .swiftCode("SANDESMMXXX")
                .name("Banco Santander")
                .build();
        
        when(bankRepositoryPort.existsBySwiftCode(anyString())).thenReturn(true);
        
        // When & Then
        assertThatThrownBy(() -> bankService.createBank(newBank))
                .isInstanceOf(DuplicateBankException.class)
                .hasMessageContaining("SANDESMMXXX");
        
        verify(bankRepositoryPort).existsBySwiftCode("SANDESMMXXX");
        verify(bankRepositoryPort, never()).save(any());
    }
    
    @Test
    void createBank_InvalidSwiftCode_ThrowsException() {
        // Given
        Bank newBank = Bank.builder()
                .swiftCode("INVALID")
                .name("Bank Name")
                .build();
        
        // When & Then
        assertThatThrownBy(() -> bankService.createBank(newBank))
                .isInstanceOf(InvalidBankDataException.class)
                .hasMessageContaining("Invalid SWIFT code format");
        
        verify(bankRepositoryPort, never()).existsBySwiftCode(anyString());
        verify(bankRepositoryPort, never()).save(any());
    }
    
    @Test
    void createBank_MissingName_ThrowsException() {
        // Given
        Bank newBank = Bank.builder()
                .swiftCode("SANDESMMXXX")
                .name(null)
                .build();
        
        // When & Then
        assertThatThrownBy(() -> bankService.createBank(newBank))
                .isInstanceOf(InvalidBankDataException.class)
                .hasMessageContaining("name");
        
        verify(bankRepositoryPort, never()).save(any());
    }
    
    @Test
    void updateBank_ValidBank_Success() {
        // Given
        Long bankId = 1L;
        Bank updateBank = Bank.builder()
                .swiftCode("SANDESMMXXX")
                .name("Updated Bank Name")
                .build();
        
        when(bankRepositoryPort.findById(bankId)).thenReturn(Optional.of(validBank));
        when(bankRepositoryPort.save(any(Bank.class))).thenReturn(validBank);
        
        // When
        Bank result = bankService.updateBank(bankId, updateBank);
        
        // Then
        assertThat(result).isNotNull();
        verify(bankRepositoryPort).findById(bankId);
        verify(bankRepositoryPort).save(any(Bank.class));
    }
    
    @Test
    void updateBank_BankNotFound_ThrowsException() {
        // Given
        Long bankId = 999L;
        Bank updateBank = Bank.builder()
                .swiftCode("SANDESMMXXX")
                .name("Bank Name")
                .build();
        
        when(bankRepositoryPort.findById(bankId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> bankService.updateBank(bankId, updateBank))
                .isInstanceOf(BankNotFoundException.class);
        
        verify(bankRepositoryPort).findById(bankId);
        verify(bankRepositoryPort, never()).save(any());
    }
    
    @Test
    void getBankById_ExistingBank_ReturnsBank() {
        // Given
        Long bankId = 1L;
        when(bankRepositoryPort.findById(bankId)).thenReturn(Optional.of(validBank));
        
        // When
        Optional<Bank> result = bankService.getBankById(bankId);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(bankId);
        verify(bankRepositoryPort).findById(bankId);
    }
    
    @Test
    void getBankById_NonExistingBank_ReturnsEmpty() {
        // Given
        Long bankId = 999L;
        when(bankRepositoryPort.findById(bankId)).thenReturn(Optional.empty());
        
        // When
        Optional<Bank> result = bankService.getBankById(bankId);
        
        // Then
        assertThat(result).isEmpty();
        verify(bankRepositoryPort).findById(bankId);
    }
    
    @Test
    void getBankBySwiftCode_ExistingBank_ReturnsBank() {
        // Given
        String swiftCode = "SANDESMMXXX";
        when(bankRepositoryPort.findBySwiftCode(swiftCode)).thenReturn(Optional.of(validBank));
        
        // When
        Optional<Bank> result = bankService.getBankBySwiftCode(swiftCode);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getSwiftCode()).isEqualTo(swiftCode);
        verify(bankRepositoryPort).findBySwiftCode(swiftCode);
    }
    
    @Test
    void getAllBanks_ReturnsAllBanks() {
        // Given
        List<Bank> banks = List.of(validBank);
        when(bankRepositoryPort.findAll()).thenReturn(banks);
        
        // When
        List<Bank> result = bankService.getAllBanks();
        
        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(validBank);
        verify(bankRepositoryPort).findAll();
    }
    
    @Test
    void deleteBank_ExistingBank_Success() {
        // Given
        Long bankId = 1L;
        when(bankRepositoryPort.findById(bankId)).thenReturn(Optional.of(validBank));
        
        // When
        bankService.deleteBank(bankId);
        
        // Then
        verify(bankRepositoryPort).findById(bankId);
        verify(bankRepositoryPort).deleteById(bankId);
    }
    
    @Test
    void deleteBank_NonExistingBank_ThrowsException() {
        // Given
        Long bankId = 999L;
        when(bankRepositoryPort.findById(bankId)).thenReturn(Optional.empty());
        
        // When & Then
        assertThatThrownBy(() -> bankService.deleteBank(bankId))
                .isInstanceOf(BankNotFoundException.class);
        
        verify(bankRepositoryPort).findById(bankId);
        verify(bankRepositoryPort, never()).deleteById(anyLong());
    }
    
    @Test
    void selfCallGetBankById_Success() {
        // Given
        Long bankId = 1L;
        when(bankHttpClientPort.getBankById(bankId)).thenReturn(Optional.of(validBank));
        
        // When
        Optional<Bank> result = bankService.selfCallGetBankById(bankId);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(validBank);
        verify(bankHttpClientPort).getBankById(bankId);
    }
    
    @Test
    void selfCallGetBankBySwiftCode_Success() {
        // Given
        String swiftCode = "SANDESMMXXX";
        when(bankHttpClientPort.getBankBySwiftCode(swiftCode)).thenReturn(Optional.of(validBank));
        
        // When
        Optional<Bank> result = bankService.selfCallGetBankBySwiftCode(swiftCode);
        
        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(validBank);
        verify(bankHttpClientPort).getBankBySwiftCode(swiftCode);
    }
}