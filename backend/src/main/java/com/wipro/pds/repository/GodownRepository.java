package com.wipro.pds.repository;

import com.wipro.pds.entity.Godown;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GodownRepository extends JpaRepository<Godown, Long> {
    Optional<Godown> findByGodownCode(String godownCode);
}
