package com.name.evidenceservice.dto;

import com.name.evidenceservice.model.EvidenceType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EvidenceResponseDTO {
    private Long id;
    private String title;
    private String description;
    private EvidenceType type;
    private Long caseId;
    private String currentCustodian;
    private String location;
    private LocalDateTime collectedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}