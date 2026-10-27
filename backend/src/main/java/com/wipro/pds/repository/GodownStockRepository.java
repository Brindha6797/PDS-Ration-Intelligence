package com.wipro.pds.repository;

import com.wipro.pds.entity.GodownStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GodownStockRepository extends JpaRepository<GodownStock, Long> {
    List<GodownStock> findByGodown_GodownId(Long godownId);
    Optional<GodownStock> findByGodown_GodownIdAndCommodity_CommodityId(Long godownId, Long commodityId);
}
