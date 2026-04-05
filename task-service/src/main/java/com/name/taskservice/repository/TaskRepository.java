package com.name.taskservice.repository;

import com.name.taskservice.model.Task;
import com.name.taskservice.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByCaseId(Long caseId);
    List<Task> findByAssignedTo(String assignedTo);
    List<Task> findByStatus(TaskStatus status);
    List<Task> findByCaseIdAndStatus(Long caseId, TaskStatus status);
}