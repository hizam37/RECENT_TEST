package com.hizam.task_management_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hizam.task_management_service.exception.TaskException;
import com.hizam.task_management_service.model.*;
import com.hizam.task_management_service.repository.ReferenceTokenRepository;
import com.hizam.task_management_service.repository.TaskRepository;
import com.hizam.task_management_service.service.TaskService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @MockBean
    private ReferenceTokenRepository referenceTokenRepository;

    @MockBean
    private TaskRepository taskRepository;


    @Test
    public void createTaskTest() throws Exception {
        Task task = new Task();
        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskPriority(TaskPriority.HIGH);
        task.setAuthor("Anton");
        task.setDescription("Deploy Project");
        task.setTitle("Java Development");
        task.setPerformerId(1L);
        when(taskService.addTask(task)).thenReturn(task);
        mockMvc.perform(post("/api/v1/auth/login/create")
                        .with(user("dan").roles("ADMIN"))
                        .content(asJsonString(task))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.author").value(task.getAuthor()))
                .andExpect(jsonPath("$.description").value(task.getDescription()))
                .andExpect(jsonPath("$.title").value(task.getTitle()))
                .andExpect(jsonPath("$.taskStatus").value(task.getTaskStatus().name()))
                .andExpect(jsonPath("$.taskPriority").value(task.getTaskPriority().name()))
                .andDo(print());
    }


    @Test
    public void createTaskForAdminTest() throws Exception {
        Task task = new Task();
        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskPriority(TaskPriority.HIGH);
        task.setAuthor("Anton");
        task.setDescription("Deploy Project");
        task.setTitle("Java Development");
        task.setPerformerId(1L);
        doThrow(new TaskException("Task cannot be assigned to an admin")).when(taskService).addTask(task);
        mockMvc.perform(post("/api/v1/auth/login/create")
                        .with(user("dan").roles("ADMIN"))
                        .content(asJsonString(task))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }


    @Test
    public void getTasksTest() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskPriority(TaskPriority.HIGH);
        task.setAuthor("ALI");
        task.setPerformerId(2L);
        task.setComment("has to be solved");
        when(taskService.getTasks(any(TaskPage.class), any(TaskSearchCriteria.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(task), PageRequest.of(0, 10), 1));
        mockMvc.perform(get("/api/v1/auth/login/view_tasks").with(user("dan").roles("ADMIN")))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    public void deleteANonExistingTaskTest() throws Exception {
        doThrow(new TaskException("performer with id " + 1L + " does not exist")).when(taskService).deleteTaskByPerformerId(1L);
        mockMvc.perform(delete("/api/v1/auth/login/delete_task_by_id/{taskId}", 1L).with(user("dan").roles("ADMIN")))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    public void deleteAnExistingTaskTest() throws Exception {
        doNothing().when(taskService).deleteTaskByPerformerId(1L);
        mockMvc.perform(delete("/api/v1/auth/login/delete_task_by_id/{taskId}", 1L).with(user("dan").roles("ADMIN")))
                .andExpect(status().isOk())
                .andDo(print());
    }



    @Test
    public void getMyTaskTest() throws Exception {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskPriority(TaskPriority.HIGH);
        task.setAuthor("ALI");
        task.setPerformerId(2L);
        task.setComment("has to be solved");
        when(taskService.viewMyTask(any(HttpServletRequest.class))).thenReturn(task);
        mockMvc.perform(get("/api/v1/auth/login/view_my_task").with(user("anton").roles("PERFORMER")))
                .andDo(print());
    }

    @Test
    public void updateTaskOfThePerformerStatusTest() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReferenceToken referenceToken = new ReferenceToken();
        referenceToken.setReferenceToken("1231242-3DSAFGWT-GFDGASDFGS-HWGRWER3RRF");
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJVU0VSIl0sImlkIjoiZDc2MWU3YWItMGFiNy00MTM5LTk0ZjktOWJhYzUxZDY0MWFmIiwiZW1haWwiOiJHT09GWUBnbWFpbC5jb20iLCJzdWIiOiJHT09GWUBnbWFpbC5jb20iLCJpYXQiOjE3MjUwMTU5NjMsImV4cCI6MTcyNTE1OTk2M30.Hp0XgHrVTmHBLO9r42uMyHCQ-5mMPfEBvjyHejVpoGQ";
        Cookie cookie1 = new Cookie("accessToken", token);
        Cookie cookie2 = new Cookie("refreshToken", referenceToken.getReferenceToken());
        Cookie[] cookies = new Cookie[]{cookie1,cookie2};
        referenceToken.setId(1L);
        referenceToken.setReferenceToken(token);
        referenceToken.setPerformerId(1L);
        Task task = new Task();
        task.setId(1L);
        task.setTaskStatus(TaskStatus.COMPLETED);
        task.setComment("task solved");
        when(request.getCookies()).thenReturn(cookies);
        when(referenceTokenRepository.findByReferenceToken(cookies[1].getValue())).thenReturn(referenceToken);
        when(taskRepository.findByPerformerId(referenceToken.getPerformerId())).thenReturn(task);
        doNothing().when(taskService).updateMyTask(task, request);
        mockMvc.perform(put("/api/v1/auth/login/update_my_task").with(user("adam").roles("PERFORMER"))
                        .content(asJsonString(task))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    public void updateTaskOfThePerformerTest() throws Exception
    {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskPriority(TaskPriority.MEDIUM);
        task.setAuthor("ALI");
        task.setPerformerId(2L);
        task.setComment("has to be solved");
        doNothing().when(taskService).updateTaskByPerformerId(task,task.getPerformerId());
        mockMvc.perform(put("/api/v1/auth/login/update/{performerId}",2).with(user("dan").roles("ADMIN"))
                .content(asJsonString(task))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }






    public static String asJsonString(final Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
