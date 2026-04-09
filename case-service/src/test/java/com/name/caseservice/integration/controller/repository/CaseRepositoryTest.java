package com.name.caseservice.integration.controller.repository;

import com.name.caseservice.model.CasePriority;
import com.name.caseservice.model.CaseStatus;
import com.name.caseservice.model.InvestigationCase;
import com.name.caseservice.repository.CaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CaseRepositoryTest {
    @Autowired
    private CaseRepository caseRepository;
    private InvestigationCase testCase;
    @BeforeEach
    void setUp() {
        caseRepository.deleteAll();
        testCase = new InvestigationCase();
        testCase.setTitle("Caso Gonzaga");
        testCase.setDescription("Descubierta en su lujoso baño");
        testCase.setPriority(CasePriority.Critical);
        testCase.setStatus(CaseStatus.Open);
        testCase.setAssignedDetective("Luis R");
    }

    @Test
    void save_ShouldPersistCase() {
        InvestigationCase saved = caseRepository.save(testCase);
        assertNotNull(saved.getId());
        assertEquals("Caso Gonzaga", saved.getTitle());
    }

    @Test
    void findById_ShouldReturnCase_WhenExists() {
        InvestigationCase saved = caseRepository.save(testCase);
        Optional<InvestigationCase> found = caseRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Caso Gonzaga", found.get().getTitle());
    }

    @Test
    void findAll_ShouldReturnAllCases() {
        caseRepository.save(testCase);
        InvestigationCase another = new InvestigationCase();
        another.setTitle("Caso Bonet");
        another.setPriority(CasePriority.High);
        another.setStatus(CaseStatus.In_progress);
        caseRepository.save(another);
        List<InvestigationCase> cases = caseRepository.findAll();
        assertEquals(2, cases.size());
    }

    @Test
    void findByStatus_ShouldReturnMatchingCases() {
        caseRepository.save(testCase);
        List<InvestigationCase> openCases = caseRepository.findByStatus(CaseStatus.Open);
        assertFalse(openCases.isEmpty());
        assertTrue(openCases.stream().allMatch(c -> c.getStatus() == CaseStatus.Open));
    }

    @Test
    void findByAssignedDetective_ShouldReturnMatchingCases() {
        caseRepository.save(testCase);
        List<InvestigationCase> cases = caseRepository.findByAssignedDetective("Luis R");
        assertFalse(cases.isEmpty());
        assertEquals("Luis R", cases.get(0).getAssignedDetective());
    }

    @Test
    void deleteById_ShouldRemoveCase() {
        InvestigationCase saved = caseRepository.save(testCase);
        caseRepository.deleteById(saved.getId());
        Optional<InvestigationCase> found = caseRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void existsById_ShouldReturnTrue_WhenExists() {
        InvestigationCase saved = caseRepository.save(testCase);
        assertTrue(caseRepository.existsById(saved.getId()));
    }

    @Test
    void existsById_ShouldReturnFalse_WhenNotExists() {
        assertFalse(caseRepository.existsById(999L));
    }
}