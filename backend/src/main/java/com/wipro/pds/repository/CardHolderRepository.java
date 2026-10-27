package com.wipro.pds.repository;

import com.wipro.pds.entity.CardHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CardHolderRepository extends JpaRepository<CardHolder, Long> {
    Optional<CardHolder> findByCardNumber(String cardNumber);
    List<CardHolder> findByAssignedShop_ShopId(Long shopId);
    Page<CardHolder> findByAssignedShop_ShopId(Long shopId, Pageable pageable);
    
    @Query("SELECT c FROM CardHolder c WHERE (:shopId IS NULL OR c.assignedShop.shopId = :shopId) " +
           "AND (:search IS NULL OR LOWER(c.cardNumber) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.headOfFamilyName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<CardHolder> searchCards(@Param("shopId") Long shopId, @Param("search") String search, Pageable pageable);

    // Cards with no recorded sale for 3 completed consecutive months
    @Query("SELECT ch FROM CardHolder ch WHERE ch.cardStatus = 'ACTIVE' " +
           "AND NOT EXISTS (SELECT s FROM Sale s WHERE s.cardHolder.cardId = ch.cardId " +
           "AND s.saleDate >= :startDate AND s.saleDate <= :endDate AND s.saleStatus = 'COMPLETED')")
    List<CardHolder> findCardsWithNoDrawalsInWindow(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
