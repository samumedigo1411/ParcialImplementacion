package com.name.caseservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaseCreatedEvent {
    private Long caseId;
    private String title;
    private String status;
    private String priority;
    private String assignedDetective;
}