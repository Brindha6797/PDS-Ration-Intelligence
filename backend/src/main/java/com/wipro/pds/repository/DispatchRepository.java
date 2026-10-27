package com.wipro.pds.repository;

import com.wipro.pds.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {
    Optional<Dispatch> findByDispatchNumber(String dispatchNumber);
    List<Dispatch> findByShop_ShopIdOrderByDispatchedAtDesc(Long shopId);
    List<Dispatch> findByGodown_GodownIdOrderByDispatchedAtDesc(Long godownId);
    List<Dispatch> findByDispatchStatus(String dispatchStatus);
}
