package com.wipro.pds.repository;

import com.wipro.pds.entity.EntitlementRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface EntitlementRuleRepository extends JpaRepository<EntitlementRule, Long> {
    List<EntitlementRule> findByIsActive(String isActive);

    @Query("SELECT r FROM EntitlementRule r WHERE r.cardType.cardTypeId = :cardTypeId " +
           "AND r.commodity.commodityId = :commodityId AND r.isActive = 'Y' " +
           "AND r.effectiveStartDate <= :asOfDate " +
           "AND (r.effectiveEndDate IS NULL OR r.effectiveEndDate >= :asOfDate) " +
           "ORDER BY r.effectiveStartDate DESC")
    List<EntitlementRule> findActiveRules(@Param("cardTypeId") Long cardTypeId,
                                          @Param("commodityId") Long commodityId,
                                          @Param("asOfDate") LocalDate asOfDate);
}
