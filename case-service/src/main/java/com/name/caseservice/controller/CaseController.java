package com.name.caseservice.controller;

import com.name.caseservice.dto.CaseRequestDTO;
import com.name.caseservice.controller.dto.CaseResponseDTO;
import com.name.caseservice.model.CaseStatus;
import com.name.caseservice.service.CaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cases")
@RequiredArgsConstructor
public class CaseController {

    private final CaseService caseService;

    // POST /cases
    @PostMapping
    public ResponseEntity<CaseResponseDTO> createCase(
            @Valid @RequestBody CaseRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(caseService.createCase(request));
    }

    // GET /cases
    @GetMapping
    public ResponseEntity<List<CaseResponseDTO>> getAllCases() {
        return ResponseEntity.ok(caseService.getAllCases());
    }

    // GET /cases/{id}
    @GetMapping("/{id}")
    public ResponseEntity<CaseResponseDTO> getCaseById(@PathVariable Long id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    // PUT /cases/{id}
    @PutMapping("/{id}")
    public ResponseEntity<CaseResponseDTO> updateCase(
            @PathVariable Long id,
            @Valid @RequestBody CaseRequestDTO request) {
        return ResponseEntity.ok(caseService.updateCase(id, request));
    }

    // DELETE /cases/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCase(@PathVariable Long id) {
        caseService.deleteCase(id);
        return ResponseEntity.noContent().build();
    }

    // GET /cases/status/{status}
    @GetMapping("/status/{status}")
    public ResponseEntity<List<CaseResponseDTO>> getCasesByStatus(
            @PathVariable CaseStatus status) {
        return ResponseEntity.ok(caseService.getCasesByStatus(status));
    }
}