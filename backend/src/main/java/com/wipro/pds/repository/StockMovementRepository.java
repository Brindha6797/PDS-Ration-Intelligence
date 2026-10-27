package com.wipro.pds.repository;

import com.wipro.pds.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByReferenceDocTypeAndReferenceDocId(String docType, String docId);
    Page<StockMovement> findAllByOrderByMovementTimestampDesc(Pageable pageable);
    List<StockMovement> findByCommodity_CommodityIdAndMovementTimestampBetween(Long commodityId, LocalDateTime start, LocalDateTime end);
}
