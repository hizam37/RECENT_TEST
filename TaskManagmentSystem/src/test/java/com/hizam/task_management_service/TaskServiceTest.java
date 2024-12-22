package com.hizam.task_management_service;

import com.hizam.task_management_service.dto.TaskDto;
import com.hizam.task_management_service.exception.TaskException;
import com.hizam.task_management_service.mapper.TaskMapper;
import com.hizam.task_management_service.model.*;
import com.hizam.task_management_service.repository.ReferenceTokenRepository;
import com.hizam.task_management_service.repository.TaskCriteriaRepository;
import com.hizam.task_management_service.repository.TaskRepository;
import com.hizam.task_management_service.repository.UserRepository;
import com.hizam.task_management_service.security.config.SecurityConfiguration;
import com.hizam.task_management_service.service.TaskService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import({SecurityConfiguration.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@Transactional
public class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private TaskMapper taskMapper;

    @MockBean
    private TaskCriteriaRepository taskCriteriaRepository;

    @MockBean
    private ReferenceTokenRepository referenceTokenRepository;

    @Test
    @DisplayName("Testing Creating task")
    public void createTask() {
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskStatus(TaskStatus.PENDING);
        taskDto.setTaskPriority(TaskPriority.HIGH);
        taskDto.setAuthor("Anton");
        taskDto.setDescription("Deploy Project");
        taskDto.setTitle("Java Development");
        taskDto.setPerformerId(1L);
        var user = User.builder()
                .id(1L)
                .email("dan@gmail.com")
                .password("123")
                .role(Role.ROLE_PERFORMER)
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        taskService.addTask(taskDto);
        assertEquals(taskDto.getPerformerId(),user.getId());
    }


    @Test
    @DisplayName("Testing creating task for an admin")
    public void throwExceptionWhenCreatingTaskForAdmin()
    {
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskStatus(TaskStatus.PENDING);
        taskDto.setTaskPriority(TaskPriority.HIGH);
        taskDto.setAuthor("Anton");
        taskDto.setDescription("Deploy Project");
        taskDto.setTitle("Java Development");
        taskDto.setPerformerId(1L);
        doThrow(new TaskException("Task cannot be assigned to an admin")).when(userRepository).findById(1L);
        assertThrows(TaskException.class,()->taskService.addTask(taskDto));
    }

    @Test
    @DisplayName("Testing creating task for un existed performer")
    public void throwExceptionWhenCreatingTaskForUnExistedPerformer()
    {
        TaskDto taskDto = new TaskDto();
        taskDto.setTaskStatus(TaskStatus.PENDING);
        taskDto.setTaskPriority(TaskPriority.HIGH);
        taskDto.setAuthor("Anton");
        taskDto.setDescription("Deploy Project");
        taskDto.setTitle("Java Development");
        doThrow(new TaskException("Performer with id + " + taskDto.getPerformerId() + " does not exist")).when(userRepository).findById(taskDto.getPerformerId());
        assertThrows(TaskException.class,()->taskService.addTask(taskDto));
    }



    @Test
    @DisplayName("getting tasks using filters test")
    public void getTasksTest()
    {
        TaskPage taskPage = new TaskPage();
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskPriority(TaskPriority.HIGH);
        task.setAuthor("ALI");
        task.setPerformerId(2L);
        task.setComment("has to be solved");
        TaskSearchCriteria taskSearchCriteria = new TaskSearchCriteria();
        Page<Task> page = new PageImpl<>(Collections.singletonList(task));
        when(taskCriteriaRepository.findAllWithFilters(taskPage,taskSearchCriteria)).thenReturn(page);
        assertEquals(taskService.getTasks(taskPage,taskSearchCriteria),taskMapper.taskToTaskDtoPage(page));
    }


    @Test
    @DisplayName("Testing update task for the performer")
    public void updateTaskByPerformerIdTest()
    {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setTaskPriority(TaskPriority.HIGH);
        task.setAuthor("ALI");
        task.setPerformerId(2L);
        task.setComment("has to be solved");
        doNothing().when(taskCriteriaRepository).updateTaskByPerformerId(task,1L);
        assertThat(task).isNotNull();
    }

    @Test
    @DisplayName("Testing update my task")
    public void updateMyTaskTest()
    {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReferenceToken referenceToken = new ReferenceToken();
        referenceToken.setReferenceToken("1231242-3DSAFGWT-GFDGASDFGS-HWGRWER3RRF");
        referenceToken.setPerformerId(1L);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJVU0VSIl0sImlkIjoiZDc2MWU3YWItMGFiNy00MTM5LTk0ZjktOWJhYzUxZDY0MWFmIiwiZW1haWwiOiJHT09GWUBnbWFpbC5jb20iLCJzdWIiOiJHT09GWUBnbWFpbC5jb20iLCJpYXQiOjE3MjUwMTU5NjMsImV4cCI6MTcyNTE1OTk2M30.Hp0XgHrVTmHBLO9r42uMyHCQ-5mMPfEBvjyHejVpoGQ";
        Cookie cookie1 = new Cookie("access_token",token);
        Cookie cookie2 = new Cookie("reference_token",referenceToken.getReferenceToken());
        Cookie[] cookies = new Cookie[]{cookie1,cookie2};
        when(request.getCookies()).thenReturn(cookies);
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setPerformerId(2L);
        task.setComment("Task has to be solved");
        when(referenceTokenRepository.findByReferenceToken(cookies[1].getValue())).thenReturn(referenceToken);
        when(taskRepository.findByPerformerId(referenceToken.getPerformerId())).thenReturn(task);
        task.setTaskStatus(TaskStatus.COMPLETED);
        task.setPerformerId(2L);
        task.setComment("Task solved");
        when(taskRepository.save(task)).thenReturn(task);
        assertEquals(task.getComment(),"Task solved");
    }



    @Test
    @DisplayName("Testing view my task")
    public void view_my_task()
    {
        HttpServletRequest request = mock(HttpServletRequest.class);
        ReferenceToken referenceToken = new ReferenceToken();
        referenceToken.setReferenceToken("1231242-3DSAFGWT-GFDGASDFGS-HWGRWER3RRF");
        referenceToken.setPerformerId(1L);
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJVU0VSIl0sImlkIjoiZDc2MWU3YWItMGFiNy00MTM5LTk0ZjktOWJhYzUxZDY0MWFmIiwiZW1haWwiOiJHT09GWUBnbWFpbC5jb20iLCJzdWIiOiJHT09GWUBnbWFpbC5jb20iLCJpYXQiOjE3MjUwMTU5NjMsImV4cCI6MTcyNTE1OTk2M30.Hp0XgHrVTmHBLO9r42uMyHCQ-5mMPfEBvjyHejVpoGQ";
        Cookie cookie1 = new Cookie("access_token",token);
        Cookie cookie2 = new Cookie("reference_token",referenceToken.getReferenceToken());
        Cookie[] cookies = new Cookie[]{cookie1,cookie2};
        when(request.getCookies()).thenReturn(cookies);
        when(referenceTokenRepository.findByReferenceToken(cookies[1].getValue())).thenReturn(referenceToken);
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setPerformerId(1L);
        task.setComment("Task has to be solved");
        when(taskRepository.findByPerformerId(referenceToken.getPerformerId())).thenReturn(task);
        assertThat(task).isNotNull();
    }



    @Test
    @DisplayName("Testing deleting Task for existing performer")
    public void deleteTaskByPerformerIdTest()
    {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setPerformerId(1L);
        task.setComment("Task has to be solved");
        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).deleteById(task.getId());
    }



    @Test
    @DisplayName("Testing deleting Task for the un existing performer")
    public void throwWhenDeletingUnexistedTaskByPerformerIdTest()
    {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("deployment");
        task.setDescription("deploying a project");
        task.setTaskStatus(TaskStatus.PENDING);
        task.setPerformerId(1L);
        task.setComment("Task has to be solved");
        doThrow(new TaskException("performer with id " + task.getPerformerId() + " does not exist")).when(taskRepository).deleteById(task.getId());
        assertThrows(TaskException.class,()->taskService.deleteTaskByPerformerId(task.getPerformerId()));
    }



}
