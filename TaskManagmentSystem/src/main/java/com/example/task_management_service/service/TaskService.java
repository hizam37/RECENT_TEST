package com.example.task_management_service.service;

import com.example.task_management_service.exception.AuthorizationDeniedException;
import com.example.task_management_service.exception.RegistrationException;
import com.example.task_management_service.exception.TaskException;
import com.example.task_management_service.model.*;
import com.example.task_management_service.repository.ReferenceTokenRepository;
import com.example.task_management_service.repository.TaskRepository;
import com.example.task_management_service.repository.UserRepository;
import com.example.task_management_service.security.service.referenceTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    private final referenceTokenService referenceTokenService;

    private final ReferenceTokenRepository referenceTokenRepository;

    public Task createTask(Task task, HttpServletResponse response)
    {

        Optional<User> userFound = userRepository.findById(task.getPerformerId());
        if(userFound.isPresent() && userFound.get().getRole().name().equals("ROLE_PERFORMER"))
        {
            task.setPerformerId(userFound.get().getId());
            var refreshToken = referenceTokenService.generateRefreshTokenByPerformerId(userFound.get().getId());
            Cookie cookie = new Cookie("refreshToken",refreshToken.getToken());
            cookie.setHttpOnly(true);
            cookie.setSecure(true);
            response.addCookie(cookie);

        }else {
            throw new TaskException("Исполнитель с установленным идентификатором не существует");
        }
        return taskRepository.save(task);
    }


    public Task updateTaskById(Task task,Long taskId)
    {

        Task existingTask = getTaskById(taskId);

        if(Objects.nonNull(existingTask))
        {
            existingTask.setTaskPriority(task.getTaskPriority());

            existingTask.setTitle(task.getTitle());

            existingTask.setAuthor(task.getAuthor());

            Task performerIdIsExist = taskRepository.findByPerformerId(task.getPerformerId());
            if(performerIdIsExist==null)
            {
                throw new TaskException("Исполнитель с установленным идентификатором не существует");
            }else {
                existingTask.setPerformerId(task.getPerformerId());
            }

            existingTask.setTaskStatus(task.getTaskStatus());

            existingTask.setComment(task.getComment());

            existingTask.setDescription(task.getDescription());


        }


        return taskRepository.save(existingTask);
    }

    public Task getTaskById(Long taskId)
    {
        return taskRepository.findById(taskId).orElseThrow(null);
    }




    public List<Task> getTasks()
    {
        List<Task> taskList = taskRepository.findAll();
        return Collections.synchronizedList(taskList);
    }

    public Task getMyTask(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            Optional<ReferenceToken> refreshToken  = referenceTokenRepository.findByToken(cookie.getValue());
            if(refreshToken.isPresent())
            {
              return taskRepository.findByPerformerId(refreshToken.get().getPerformerId());
            }
        }

        return null;
    }

    public List<Task> filterByStatus(TaskStatus taskStatus) {

        return taskRepository.filterByStatus(taskStatus);
    }

    public List<Task> filterByPriority(TaskPriority taskPriority) {

        return taskRepository.findByTaskPriority(taskPriority);
    }


    public void deleteTaskById(Long taskId)
    {
        taskRepository.deleteById(taskId);
    }


    public void updateMyStatusTo(TaskStatus taskStatus,HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            Optional<ReferenceToken> referenceToken = referenceTokenRepository.findByToken(cookie.getValue());
            if(referenceToken.isPresent())
            {
                Task task = taskRepository.findByPerformerId(referenceToken.get().getPerformerId());
                task.setTaskStatus(TaskStatus.valueOf(taskStatus.name()));
                taskRepository.save(task);
            }
        }

    }




}
