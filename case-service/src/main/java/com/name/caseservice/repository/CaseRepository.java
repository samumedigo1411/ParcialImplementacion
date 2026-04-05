package com.name.caseservice.repository;

import com.name.caseservice.model.CasePriority;
import com.name.caseservice.model.InvestigationCase;
import com.name.caseservice.model.CaseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends JpaRepository<InvestigationCase, Long> {

    // Buscar casos por estado
    List<InvestigationCase> findByStatus(CaseStatus status);

    // Buscar casos por detective asignado
    List<InvestigationCase> findByAssignedDetective(String assignedDetective);

    // Buscar casos por prioridad
    List<InvestigationCase> findByPriority(CasePriority priority);
}