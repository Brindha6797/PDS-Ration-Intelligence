package com.wipro.pds.repository;

import com.wipro.pds.entity.ComplaintHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintHistoryRepository extends JpaRepository<ComplaintHistory, Long> {
    List<ComplaintHistory> findByComplaint_ComplaintIdOrderByActionTimestampAsc(Long complaintId);
}
