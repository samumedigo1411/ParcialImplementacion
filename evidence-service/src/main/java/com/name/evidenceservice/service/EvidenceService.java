package com.name.evidenceservice.service;

import com.name.evidenceservice.config.KafkaConfig;
import com.name.evidenceservice.dto.EvidenceRequestDTO;
import com.name.evidenceservice.dto.EvidenceResponseDTO;
import com.name.evidenceservice.exception.EvidenceNotFoundException;
import com.name.evidenceservice.model.Evidence;
import com.name.evidenceservice.model.EvidenceType;
import com.name.evidenceservice.repository.EvidenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceService {

    private final EvidenceRepository evidenceRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EvidenceResponseDTO createEvidence(EvidenceRequestDTO request) {
        Evidence evidence = new Evidence();
        evidence.setTitle(request.getTitle());
        evidence.setDescription(request.getDescription());
        evidence.setType(request.getType());
        evidence.setCaseId(request.getCaseId());
        evidence.setCurrentCustodian(request.getCurrentCustodian());
        evidence.setLocation(request.getLocation());

        Evidence saved = evidenceRepository.save(evidence);

        String eventJson = String.format(
                "{\"event\":\"EvidenceAdded\",\"evidenceId\":%d,\"caseId\":%d,\"type\":\"%s\"}",
                saved.getId(), saved.getCaseId(), saved.getType()
        );
        kafkaTemplate.send(KafkaConfig.EVIDENCE_ADDED_TOPIC,
                String.valueOf(saved.getId()), eventJson);
        log.info("Evento EvidenceAdded publicado: {}", eventJson);

        return toResponse(saved);
    }

    public List<EvidenceResponseDTO> getAllEvidences() {
        return evidenceRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EvidenceResponseDTO getEvidenceById(Long id) {
        Evidence found = evidenceRepository.findById(id)
                .orElseThrow(() -> new EvidenceNotFoundException(
                        "Evidencia no encontrada con id: " + id));
        return toResponse(found);
    }

    public List<EvidenceResponseDTO> getEvidencesByCaseId(Long caseId) {
        return evidenceRepository.findByCaseId(caseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public EvidenceResponseDTO updateEvidence(Long id, EvidenceRequestDTO request) {
        Evidence found = evidenceRepository.findById(id)
                .orElseThrow(() -> new EvidenceNotFoundException(
                        "Evidencia no encontrada con id: " + id));

        found.setTitle(request.getTitle());
        found.setDescription(request.getDescription());
        found.setType(request.getType());
        found.setCaseId(request.getCaseId());
        found.setCurrentCustodian(request.getCurrentCustodian());
        found.setLocation(request.getLocation());

        Evidence updated = evidenceRepository.save(found);
        return toResponse(updated);
    }

    public void deleteEvidence(Long id) {
        if (!evidenceRepository.existsById(id)) {
            throw new EvidenceNotFoundException("Evidencia no encontrada con id: " + id);
        }
        evidenceRepository.deleteById(id);
    }

    private EvidenceResponseDTO toResponse(Evidence e) {
        EvidenceResponseDTO response = new EvidenceResponseDTO();
        response.setId(e.getId());
        response.setTitle(e.getTitle());
        response.setDescription(e.getDescription());
        response.setType(e.getType());
        response.setCaseId(e.getCaseId());
        response.setCurrentCustodian(e.getCurrentCustodian());
        response.setLocation(e.getLocation());
        response.setCollectedAt(e.getCollectedAt());
        response.setCreatedAt(e.getCreatedAt());
        response.setUpdatedAt(e.getUpdatedAt());
        return response;
    }
}