package com.wipro.pds.repository;

import com.wipro.pds.entity.FamilyMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {
    List<FamilyMember> findByCardHolder_CardId(Long cardId);
}
