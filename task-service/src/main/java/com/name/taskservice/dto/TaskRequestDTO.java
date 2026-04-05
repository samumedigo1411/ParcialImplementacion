package com.name.taskservice.dto;

import com.name.taskservice.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TaskRequestDTO {
    @NotBlank(message = "El título es obligatorio")
    private String title;
    private String description;
    @NotNull(message = "El caso es obligatorio")
    private Long caseId;
    private String assignedTo;
    private TaskPriority priority;
    private LocalDateTime dueDate;
}