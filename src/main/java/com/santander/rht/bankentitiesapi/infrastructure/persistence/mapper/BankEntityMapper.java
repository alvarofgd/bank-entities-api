package com.santander.rht.bankentitiesapi.infrastructure.persistence.mapper;

import com.santander.rht.bankentitiesapi.domain.model.Bank;
import com.santander.rht.bankentitiesapi.infrastructure.persistence.entity.BankEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct mapper for Bank domain model and JPA entity
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BankEntityMapper {
    
    Bank toDomain(BankEntity entity);
    
    BankEntity toEntity(Bank domain);
    
    void updateEntityFromDomain(Bank domain, @MappingTarget BankEntity entity);
}