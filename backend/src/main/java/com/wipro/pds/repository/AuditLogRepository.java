package com.wipro.pds.repository;

import com.wipro.pds.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityNameAndEntityIdOrderByActionTimestampDesc(String entityName, String entityId);
    Page<AuditLog> findAllByOrderByActionTimestampDesc(Pageable pageable);
}
