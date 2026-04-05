package com.name.caseservice.dto;

import com.name.caseservice.model.CasePriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CaseRequestDTO {
    @NotBlank(message = "El título es obligatorio")
    private String title;
    private String description;
    @NotNull(message = "La prioridad es obligatoria")
    private CasePriority priority;
    private String assignedDetective;
}