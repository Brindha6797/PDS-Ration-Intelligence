package com.wipro.pds.repository;

import com.wipro.pds.entity.DispatchItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DispatchItemRepository extends JpaRepository<DispatchItem, Long> {
    List<DispatchItem> findByDispatch_DispatchId(Long dispatchId);
}
