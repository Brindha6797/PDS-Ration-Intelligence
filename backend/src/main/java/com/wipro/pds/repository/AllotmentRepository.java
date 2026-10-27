package com.wipro.pds.repository;

import com.wipro.pds.entity.Allotment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AllotmentRepository extends JpaRepository<Allotment, Long> {
    List<Allotment> findByAllotmentYearAndAllotmentMonth(Integer year, Integer month);
    List<Allotment> findByShop_ShopIdAndAllotmentYearAndAllotmentMonth(Long shopId, Integer year, Integer month);
    Optional<Allotment> findByShop_ShopIdAndCommodity_CommodityIdAndAllotmentYearAndAllotmentMonth(
        Long shopId, Long commodityId, Integer year, Integer month);
}
