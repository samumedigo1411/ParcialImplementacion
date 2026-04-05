package com.name.caseservice.service;

import com.name.caseservice.config.KafkaConfig;
import com.name.caseservice.dto.CaseRequestDTO;
import com.name.caseservice.dto.CaseResponseDTO;
import com.name.caseservice.events.CaseCreatedEvent;
import com.name.caseservice.exception.CaseNotFoundException;
import com.name.caseservice.model.CaseStatus;
import com.name.caseservice.model.InvestigationCase;
import com.name.caseservice.repository.CaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaseService {

    private final CaseRepository caseRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CaseResponseDTO createCase(CaseRequestDTO request) {
        InvestigationCase newCase = new InvestigationCase();
        newCase.setTitle(request.getTitle());
        newCase.setDescription(request.getDescription());
        newCase.setPriority(request.getPriority());
        newCase.setAssignedDetective(request.getAssignedDetective());

        InvestigationCase saved = caseRepository.save(newCase);

        CaseCreatedEvent event = CaseCreatedEvent.builder()
                .caseId(saved.getId())
                .title(saved.getTitle())
                .status(saved.getStatus().name())
                .priority(saved.getPriority().name())
                .assignedDetective(saved.getAssignedDetective())
                .build();

        String eventJson = String.format(
                "{\"caseId\":%d,\"title\":\"%s\",\"status\":\"%s\",\"priority\":\"%s\"}",
                saved.getId(), saved.getTitle(), saved.getStatus(), saved.getPriority()
        );
        kafkaTemplate.send(KafkaConfig.CASE_CREATED_TOPIC,
                String.valueOf(saved.getId()), eventJson);
        log.info("Evento CaseCreated publicado: {}", eventJson);

        return toResponse(saved);
    }

    public List<CaseResponseDTO> getAllCases() {
        return caseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CaseResponseDTO getCaseById(Long id) {
        InvestigationCase found = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(
                        "Caso no encontrado con id: " + id));
        return toResponse(found);
    }

    public CaseResponseDTO updateCase(Long id, CaseRequestDTO request) {
        InvestigationCase found = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(
                        "Caso no encontrado con id: " + id));

        found.setTitle(request.getTitle());
        found.setDescription(request.getDescription());
        found.setPriority(request.getPriority());
        found.setAssignedDetective(request.getAssignedDetective());

        InvestigationCase updated = caseRepository.save(found);
        return toResponse(updated);
    }

    public void deleteCase(Long id) {
        if (!caseRepository.existsById(id)) {
            throw new CaseNotFoundException("Caso no encontrado con id: " + id);
        }
        caseRepository.deleteById(id);
    }

    public List<CaseResponseDTO> getCasesByStatus(CaseStatus status) {
        return caseRepository.findByStatus(status)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private CaseResponseDTO toResponse(InvestigationCase c) {
        CaseResponseDTO response = new CaseResponseDTO();
        response.setId(c.getId());
        response.setTitle(c.getTitle());
        response.setDescription(c.getDescription());
        response.setStatus(c.getStatus());
        response.setPriority(c.getPriority());
        response.setAssignedDetective(c.getAssignedDetective());
        response.setCreatedAt(c.getCreatedAt());
        response.setUpdatedAt(c.getUpdatedAt());
        return response;
    }
}