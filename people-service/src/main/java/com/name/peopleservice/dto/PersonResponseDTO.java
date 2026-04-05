package com.name.peopleservice.dto;

import com.name.peopleservice.model.PersonRole;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PersonResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private PersonRole role;
    private Long caseId;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}