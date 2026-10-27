package com.wipro.pds.repository;

import com.wipro.pds.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    Optional<Shop> findByShopCode(String shopCode);
    List<Shop> findByShopStatus(String shopStatus);
    List<Shop> findByTalukName(String talukName);
}
