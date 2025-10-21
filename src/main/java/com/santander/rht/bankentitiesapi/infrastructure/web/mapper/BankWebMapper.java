package com.santander.rht.bankentitiesapi.infrastructure.web.mapper;

import com.santander.rht.bankentitiesapi.domain.model.Bank;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.BankResponse;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.CreateBankRequest;
import com.santander.rht.bankentitiesapi.infrastructure.web.dto.UpdateBankRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * MapStruct mapper for Bank domain model and web DTOs
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankWebMapper {
    
    Bank toDomain(CreateBankRequest request);
    
    Bank toDomain(UpdateBankRequest request);
    
    BankResponse toResponse(Bank domain);
    
    List<BankResponse> toResponseList(List<Bank> domains);
}