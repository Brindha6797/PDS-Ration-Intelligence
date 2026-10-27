package com.wipro.pds.repository;

import com.wipro.pds.entity.ShopStock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopStockRepository extends JpaRepository<ShopStock, Long> {
    List<ShopStock> findByShop_ShopId(Long shopId);
    Optional<ShopStock> findByShop_ShopIdAndCommodity_CommodityId(Long shopId, Long commodityId);

    // Row-level lock for safe concurrency (SELECT ... FOR UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ShopStock s WHERE s.shop.shopId = :shopId AND s.commodity.commodityId = :commodityId")
    Optional<ShopStock> findForUpdate(@Param("shopId") Long shopId, @Param("commodityId") Long commodityId);
}
