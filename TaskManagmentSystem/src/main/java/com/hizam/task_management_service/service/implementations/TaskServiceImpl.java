package com.hizam.task_management_service.service.implementations;

import com.hizam.task_management_service.dto.TaskDto;
import com.hizam.task_management_service.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface TaskServiceImpl {


    Page<TaskDto> getTasks(TaskPage taskPage, TaskSearchCriteria taskSearchCriteria);


    TaskDto addTask(TaskDto task);



    void updateTaskByPerformerId(Task task,Long performerId);

    void updateMyTask(Task task, HttpServletRequest request);

    TaskDto viewMyTask(HttpServletRequest request);

    void deleteTaskByPerformerId(Long performerId);

}
