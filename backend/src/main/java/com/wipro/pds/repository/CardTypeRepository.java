package com.wipro.pds.repository;

import com.wipro.pds.entity.CardType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CardTypeRepository extends JpaRepository<CardType, Long> {
    Optional<CardType> findByTypeCode(String typeCode);
}
