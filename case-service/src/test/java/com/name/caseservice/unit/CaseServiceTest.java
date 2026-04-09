package com.name.caseservice.unit;

import com.name.caseservice.dto.CaseRequestDTO;
import com.name.caseservice.controller.dto.CaseResponseDTO;
import com.name.caseservice.exception.CaseNotFoundException;
import com.name.caseservice.model.CasePriority;
import com.name.caseservice.model.CaseStatus;
import com.name.caseservice.model.InvestigationCase;
import com.name.caseservice.repository.CaseRepository;
import com.name.caseservice.service.CaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CaseServiceTest {

    @Mock
    private CaseRepository caseRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private CaseService caseService;
    private InvestigationCase testCase;
    private CaseRequestDTO testRequest;

    @BeforeEach
    void setUp() {
        testCase = new InvestigationCase();
        testCase.setId(1L);
        testCase.setTitle("Caso Aris");
        testCase.setDescription("Encontrado en su estudio");
        testCase.setPriority(CasePriority.High);
        testCase.setStatus(CaseStatus.Open);
        testCase.setAssignedDetective("Luis R");
        testRequest = new CaseRequestDTO();
        testRequest.setTitle("Caso Aris");
        testRequest.setDescription("Encontrado en su estudio");
        testRequest.setPriority(CasePriority.High);
        testRequest.setAssignedDetective("Luis R");
    }

    // Pruebas exitosas
    @Test
    void createCase_ShouldReturnCaseResponse_WhenValidRequest() {
        when(caseRepository.save(any(InvestigationCase.class))).thenReturn(testCase);
        CaseResponseDTO result = caseService.createCase(testRequest);
        assertNotNull(result);
        assertEquals("Caso Aris", result.getTitle());
        assertEquals(CasePriority.High, result.getPriority());
        verify(caseRepository, times(1)).save(any(InvestigationCase.class));
    }

    @Test
    void getAllCases_ShouldReturnListOfCases() {
        when(caseRepository.findAll()).thenReturn(List.of(testCase));
        List<CaseResponseDTO> result = caseService.getAllCases();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Caso Aris", result.get(0).getTitle());
    }

    @Test
    void getCaseById_ShouldReturnCase_WhenExists() {
        when(caseRepository.findById(1L)).thenReturn(Optional.of(testCase));
        CaseResponseDTO result = caseService.getCaseById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Caso Aris", result.getTitle());
    }

    @Test
    void updateCase_ShouldReturnUpdatedCase_WhenExists() {
        when(caseRepository.findById(1L)).thenReturn(Optional.of(testCase));
        when(caseRepository.save(any(InvestigationCase.class))).thenReturn(testCase);
        CaseResponseDTO result = caseService.updateCase(1L, testRequest);
        assertNotNull(result);
        verify(caseRepository, times(1)).save(any(InvestigationCase.class));
    }

    @Test
    void deleteCase_ShouldDeleteCase_WhenExists() {
        when(caseRepository.existsById(1L)).thenReturn(true);
        doNothing().when(caseRepository).deleteById(1L);
        assertDoesNotThrow(() -> caseService.deleteCase(1L));
        verify(caseRepository, times(1)).deleteById(1L);
    }

    // Pruebas de error

    @Test
    void getCaseById_ShouldThrowException_WhenNotFound() {
        when(caseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CaseNotFoundException.class,
            () -> caseService.getCaseById(99L));
    }

    @Test
    void updateCase_ShouldThrowException_WhenNotFound() {
        when(caseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CaseNotFoundException.class,
            () -> caseService.updateCase(99L, testRequest));
    }

    @Test
    void deleteCase_ShouldThrowException_WhenNotFound() {
        when(caseRepository.existsById(99L)).thenReturn(false);
        assertThrows(CaseNotFoundException.class,
            () -> caseService.deleteCase(99L));
    }

    // Pruebas de kafka

    @Test
    void createCase_ShouldPublishKafkaEvent_WhenCaseCreated() {
        when(caseRepository.save(any(InvestigationCase.class))).thenReturn(testCase);
        caseService.createCase(testRequest);
        verify(kafkaTemplate, times(1)).send(anyString(), anyString(), any());
    }
}