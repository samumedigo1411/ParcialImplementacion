package com.name.evidenceservice.repository;

import com.name.evidenceservice.model.Evidence;
import com.name.evidenceservice.model.EvidenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvidenceRepository extends JpaRepository<Evidence, Long> {
    List<Evidence> findByCaseId(Long caseId);
    List<Evidence> findByCaseIdAndType(Long caseId, EvidenceType type);
    List<Evidence> findByCurrentCustodian(String custodian);
}