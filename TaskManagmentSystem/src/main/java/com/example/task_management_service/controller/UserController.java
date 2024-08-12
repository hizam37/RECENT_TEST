package com.example.task_management_service.controller;


import com.example.task_management_service.model.Task;
import com.example.task_management_service.model.TaskPriority;
import com.example.task_management_service.model.TaskStatus;
import com.example.task_management_service.model.User;
import com.example.task_management_service.service.TaskService;
import com.example.task_management_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/task")
public class UserController {

    private final TaskService taskService;


    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/create")
    public Task createTask(@RequestBody Task task, HttpServletResponse response)
    {
        return taskService.createTask(task,response);
    }



    @PostMapping("/update_task/{taskId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public Task updateTaskById(@RequestBody Task task,@PathVariable Long taskId)
    {
        return taskService.updateTaskById(task,taskId);
    }


    @GetMapping("/view_tasks")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Task> getTasks()
    {
       return taskService.getTasks();
    }

    @GetMapping("/filterby_status/{taskStatus}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Task> filterByStatus(@PathVariable String taskStatus)
    {
      TaskStatus status = TaskStatus.valueOf(taskStatus.toUpperCase());
      return taskService.filterByStatus(status);
    }


    @GetMapping("/filterby_priority/{taskPriority}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<Task>filterByPriority(@PathVariable String taskPriority)
    {
        TaskPriority priority = TaskPriority.valueOf(taskPriority.toUpperCase());
      return taskService.filterByPriority(priority);
    }


    @PostMapping("delete_task_by_id/{taskId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public void deleteTaskById(@PathVariable Long taskId)
    {
        taskService.deleteTaskById(taskId);
    }

    @PostMapping("view_my_task")
    @PreAuthorize("hasRole('ROLE_PERFORMER')")
    public Task getMyTask(HttpServletRequest request)
    {
        return taskService.getMyTask(request);
    }


    @PostMapping("update_my_task_status_to/{taskStatus}")
    @PreAuthorize("hasRole('ROLE_PERFORMER')")
    public void updateMyStatusTo(@PathVariable TaskStatus taskStatus, HttpServletRequest request)
    {
        taskService.updateMyStatusTo(taskStatus,request);
    }






}
