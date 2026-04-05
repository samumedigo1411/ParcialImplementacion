package com.name.evidenceservice.dto;

import com.name.evidenceservice.model.EvidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EvidenceRequestDTO {
    @NotBlank(message = "El título es obligatorio")
    private String title;
    private String description;
    @NotNull(message = "El tipo es obligatorio")
    private EvidenceType type;
    @NotNull(message = "El caso es obligatorio")
    private Long caseId;
    private String currentCustodian;
    private String location;
}