package com.example.task_management_service.repository;

import com.example.task_management_service.model.Task;
import com.example.task_management_service.model.TaskPriority;
import com.example.task_management_service.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task,Long> {
    Task findByPerformerId(Long performerId);


    @Query("SELECT t FROM Task t WHERE t.taskStatus = :status")
    List<Task> filterByStatus(@Param("status") TaskStatus status);


    @Query("SELECT t FROM Task t WHERE t.taskPriority = :priority")
    List<Task> findByTaskPriority(@Param("priority") TaskPriority priority);

}
