package com.name.caseservice.dto;

import com.name.caseservice.model.CasePriority;
import com.name.caseservice.model.CaseStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CaseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private CaseStatus status;
    private CasePriority priority;
    private String assignedDetective;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}