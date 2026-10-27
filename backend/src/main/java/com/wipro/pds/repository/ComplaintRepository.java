package com.wipro.pds.repository;

import com.wipro.pds.entity.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Optional<Complaint> findByComplaintRef(String complaintRef);
    List<Complaint> findByShop_ShopId(Long shopId);
    List<Complaint> findByStatus(String status);
    List<Complaint> findByCategory(String category);
    Page<Complaint> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
