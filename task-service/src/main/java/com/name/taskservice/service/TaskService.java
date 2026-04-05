package com.name.taskservice.service;

import com.name.taskservice.config.KafkaConfig;
import com.name.taskservice.dto.TaskRequestDTO;
import com.name.taskservice.dto.TaskResponseDTO;
import com.name.taskservice.exception.TaskNotFoundException;
import com.name.taskservice.model.Task;
import com.name.taskservice.model.TaskStatus;
import com.name.taskservice.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TaskResponseDTO createTask(TaskRequestDTO request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCaseId(request.getCaseId());
        task.setAssignedTo(request.getAssignedTo());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());

        Task saved = taskRepository.save(task);

        String eventJson = String.format(
                "{\"event\":\"TaskCreated\",\"taskId\":%d,\"caseId\":%d,\"assignedTo\":\"%s\"}",
                saved.getId(), saved.getCaseId(), saved.getAssignedTo()
        );
        kafkaTemplate.send(KafkaConfig.TASK_CREATED_TOPIC,
                String.valueOf(saved.getId()), eventJson);
        log.info("Evento TaskCreated publicado: {}", eventJson);

        return toResponse(saved);
    }

    public List<TaskResponseDTO> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO getTaskById(Long id) {
        Task found = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Tarea no encontrada con id: " + id));
        return toResponse(found);
    }

    public List<TaskResponseDTO> getTasksByCaseId(Long caseId) {
        return taskRepository.findByCaseId(caseId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {
        Task found = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Tarea no encontrada con id: " + id));

        found.setTitle(request.getTitle());
        found.setDescription(request.getDescription());
        found.setCaseId(request.getCaseId());
        found.setAssignedTo(request.getAssignedTo());
        found.setPriority(request.getPriority());
        found.setDueDate(request.getDueDate());

        Task updated = taskRepository.save(found);

        String eventJson = String.format(
                "{\"event\":\"TaskAssigned\",\"taskId\":%d,\"assignedTo\":\"%s\"}",
                updated.getId(), updated.getAssignedTo()
        );
        kafkaTemplate.send(KafkaConfig.TASK_ASSIGNED_TOPIC,
                String.valueOf(updated.getId()), eventJson);

        return toResponse(updated);
    }

    public TaskResponseDTO completeTask(Long id) {
        Task found = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(
                        "Tarea no encontrada con id: " + id));

        found.setStatus(TaskStatus.Completed);
        Task updated = taskRepository.save(found);

        String eventJson = String.format(
                "{\"event\":\"TaskCompleted\",\"taskId\":%d,\"caseId\":%d}",
                updated.getId(), updated.getCaseId()
        );
        kafkaTemplate.send(KafkaConfig.TASK_COMPLETED_TOPIC,
                String.valueOf(updated.getId()), eventJson);
        log.info("Evento TaskCompleted publicado: {}", eventJson);

        return toResponse(updated);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException("Tarea no encontrada con id: " + id);
        }
        taskRepository.deleteById(id);
    }

    private TaskResponseDTO toResponse(Task t) {
        TaskResponseDTO response = new TaskResponseDTO();
        response.setId(t.getId());
        response.setTitle(t.getTitle());
        response.setDescription(t.getDescription());
        response.setStatus(t.getStatus());
        response.setPriority(t.getPriority());
        response.setCaseId(t.getCaseId());
        response.setAssignedTo(t.getAssignedTo());
        response.setDueDate(t.getDueDate());
        response.setCreatedAt(t.getCreatedAt());
        response.setUpdatedAt(t.getUpdatedAt());
        return response;
    }
}