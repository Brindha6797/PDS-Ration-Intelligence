package com.wipro.pds.repository;

import com.wipro.pds.entity.StockReconciliation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockReconciliationRepository extends JpaRepository<StockReconciliation, Long> {
    List<StockReconciliation> findByShop_ShopIdOrderByReconDateDesc(Long shopId);
    List<StockReconciliation> findByInvestigationStatus(String status);
}
