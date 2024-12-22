package com.hizam.task_management_service.service;

import com.hizam.task_management_service.dto.TaskDto;
import com.hizam.task_management_service.exception.TaskException;
import com.hizam.task_management_service.mapper.TaskMapper;
import com.hizam.task_management_service.model.*;
import com.hizam.task_management_service.repository.ReferenceTokenRepository;
import com.hizam.task_management_service.repository.TaskCriteriaRepository;
import com.hizam.task_management_service.repository.TaskRepository;
import com.hizam.task_management_service.repository.UserRepository;
import com.hizam.task_management_service.service.implementations.TaskServiceImpl;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class TaskService implements TaskServiceImpl {

    private final TaskRepository taskRepository;

    private final TaskCriteriaRepository taskCriteriaRepository;

    private final ReferenceTokenRepository referenceTokenRepository;

    private final UserRepository userRepository;

    private final TaskMapper taskMapper;


    @Override
    public Page<TaskDto> getTasks(TaskPage taskPage, TaskSearchCriteria taskSearchCriteria) {
        Page<Task> tasks = taskCriteriaRepository.findAllWithFilters(taskPage, taskSearchCriteria);
        return taskMapper.taskToTaskDtoPage(tasks);
    }

    @Override
    public TaskDto addTask(TaskDto taskDto) {
        Task task = taskMapper.taskDtoToTaskEntity(taskDto);
        if (userRepository.findById(task.getPerformerId()).isEmpty()) {
            throw new TaskException("Performer with id + " + task.getPerformerId() + " does not exist");
        }
        else if(userRepository.findById(task.getPerformerId()).get().getRole().name().equals("ROLE_ADMIN"))
        {
            throw new TaskException("Task cannot be assigned to an admin");
        }
        taskRepository.save(task);

        return taskMapper.taskToTaskDto(task);
    }

    @Override
    public void updateTaskByPerformerId(Task task, Long performerId) {
        taskCriteriaRepository.updateTaskByPerformerId(task, performerId);
    }



    @Override
    @Transactional
    public void updateMyTask(Task task, HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        Arrays.stream(cookies).map(cookie -> referenceTokenRepository
                        .findByReferenceToken(cookie.getValue()))
                .filter(Objects::nonNull)
                .map(referenceToken -> taskRepository.
                        findById(referenceToken.getPerformerId()))
                .filter(Optional::isPresent)
                .forEach(taskFound -> {
                    taskFound.get().setTaskStatus(task.getTaskStatus());
                    taskFound.get().setComment(task.getComment());
                    taskRepository.save(taskFound.get());

                });

    }

    @Override
    public TaskDto viewMyTask(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {

            ReferenceToken referenceToken = referenceTokenRepository.findByReferenceToken(cookie.getValue());

          Task taskFound = taskRepository.findByPerformerId(referenceToken.getPerformerId());
          return taskMapper.taskToTaskDto(taskFound);

        }

        throw new TaskException("No task found");

    }

    @Override
    public void deleteTaskByPerformerId(Long performerId) {
        Task task = taskRepository.findByPerformerId(performerId);

        if (task != null) {
            taskRepository.delete(task);
        } else {
            throw new TaskException("performer with id " + performerId + " does not exist");
        }
    }

}
