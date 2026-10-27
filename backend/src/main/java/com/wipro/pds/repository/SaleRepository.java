package com.wipro.pds.repository;

import com.wipro.pds.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    Optional<Sale> findByBillNumber(String billNumber);
    List<Sale> findByShop_ShopIdOrderBySaleDateDesc(Long shopId);
    Page<Sale> findByShop_ShopIdOrderBySaleDateDesc(Long shopId, Pageable pageable);
    List<Sale> findByCardHolder_CardIdOrderBySaleDateDesc(Long cardId);
    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}
