package com.wipro.pds.repository;

import com.wipro.pds.entity.Entitlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntitlementRepository extends JpaRepository<Entitlement, Long> {
    List<Entitlement> findByCardHolder_CardIdAndAllotmentYearAndAllotmentMonth(Long cardId, Integer year, Integer month);
    Optional<Entitlement> findByCardHolder_CardIdAndCommodity_CommodityIdAndAllotmentYearAndAllotmentMonth(
        Long cardId, Long commodityId, Integer year, Integer month);

    @Query("SELECT e FROM Entitlement e WHERE e.cardHolder.assignedShop.shopId = :shopId " +
           "AND e.allotmentYear = :year AND e.allotmentMonth = :month")
    List<Entitlement> findByShopAndPeriod(@Param("shopId") Long shopId, @Param("year") Integer year, @Param("month") Integer month);
}
