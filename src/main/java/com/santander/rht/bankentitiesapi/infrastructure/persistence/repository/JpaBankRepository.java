package com.santander.rht.bankentitiesapi.infrastructure.persistence.repository;

import com.santander.rht.bankentitiesapi.infrastructure.persistence.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository for Bank entities
 */
@Repository
public interface JpaBankRepository extends JpaRepository<BankEntity, Long> {
    
    Optional<BankEntity> findBySwiftCode(String swiftCode);
    
    List<BankEntity> findByCountry(String country);
    
    List<BankEntity> findByCountryCode(String countryCode);
    
    List<BankEntity> findByNameContainingIgnoreCase(String name);
    
    List<BankEntity> findByActiveTrue();
    
    boolean existsBySwiftCode(String swiftCode);
    
    @Query("SELECT b FROM BankEntity b WHERE b.swiftCode = :swiftCode AND b.id != :id")
    Optional<BankEntity> findBySwiftCodeAndIdNot(@Param("swiftCode") String swiftCode, @Param("id") Long id);
}