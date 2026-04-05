package com.name.peopleservice.dto;

import com.name.peopleservice.model.PersonRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PersonRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;
    @NotBlank(message = "El apellido es obligatorio")
    private String lastName;
    private String email;
    private String phone;
    @NotNull(message = "El rol es obligatorio")
    private PersonRole role;
    @NotNull(message = "El caso es obligatorio")
    private Long caseId;
    private String description;
}