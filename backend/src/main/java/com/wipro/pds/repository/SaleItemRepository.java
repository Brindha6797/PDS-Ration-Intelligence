package com.wipro.pds.repository;

import com.wipro.pds.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {
    List<SaleItem> findBySale_SaleId(Long saleId);
}
