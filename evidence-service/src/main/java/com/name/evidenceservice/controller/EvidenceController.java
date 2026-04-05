package com.name.evidenceservice.controller;

import com.name.evidenceservice.dto.EvidenceRequestDTO;
import com.name.evidenceservice.dto.EvidenceResponseDTO;
import com.name.evidenceservice.service.EvidenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evidences")
@RequiredArgsConstructor
public class EvidenceController {
    private final EvidenceService evidenceService;
    @PostMapping
    public ResponseEntity<EvidenceResponseDTO> createEvidence(
            @Valid @RequestBody EvidenceRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(evidenceService.createEvidence(request));
    }
    @GetMapping
    public ResponseEntity<List<EvidenceResponseDTO>> getAllEvidences() {
        return ResponseEntity.ok(evidenceService.getAllEvidences());
    }
    @GetMapping("/{id}")
    public ResponseEntity<EvidenceResponseDTO> getEvidenceById(@PathVariable Long id) {
        return ResponseEntity.ok(evidenceService.getEvidenceById(id));
    }
    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<EvidenceResponseDTO>> getEvidencesByCaseId(
            @PathVariable Long caseId) {
        return ResponseEntity.ok(evidenceService.getEvidencesByCaseId(caseId));
    }
    @PutMapping("/{id}")
    public ResponseEntity<EvidenceResponseDTO> updateEvidence(
            @PathVariable Long id,
            @Valid @RequestBody EvidenceRequestDTO request) {
        return ResponseEntity.ok(evidenceService.updateEvidence(id, request));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvidence(@PathVariable Long id) {
        evidenceService.deleteEvidence(id);
        return ResponseEntity.noContent().build();
    }
}