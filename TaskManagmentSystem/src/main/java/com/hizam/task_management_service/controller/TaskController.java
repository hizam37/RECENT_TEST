package com.hizam.task_management_service.controller;


import com.hizam.task_management_service.dto.JwtAuthenticationResponse;
import com.hizam.task_management_service.exception.TaskException;
import com.hizam.task_management_service.model.*;
import com.hizam.task_management_service.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * This class provides endpoints used for controlling tasks
 *
 * @author Daniil Hizam
 */
@RestController
@RequestMapping("/api/v1/auth/login")
@AllArgsConstructor
@Tag(name = "Task api", description = "This class provides endpoints used for controlling tasks")
public class TaskController {


    private final TaskService taskService;


    /**
     * Creates a task
     * exclusively by the Admin without interfering with other users ex:performer
     *
     * @param task carries the info of the task
     * @return the entity task
     * @throws TaskException if the admin is assigning the task to another admin instead of the performer
     */


    @Operation(summary = "Creates a task " +
            "exclusively by the Admin without interfering with other users ex:performer", parameters = {@Parameter(name = "task", description = "carries the info of the task"),
            @Parameter(name = "response", description = "saves the accessToken and referenceToken inside the cookies automatically")},
            responses = {@ApiResponse(responseCode = "200", description = "If done successfully"),
                    @ApiResponse(responseCode = "400", description = "throws TaskException with message cannot be assigned to an admin if the admin is assigning the task to another admin instead of the performer ",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskException.class))),
                    @ApiResponse(responseCode = "400", description = "throws TaskException with message Performer with id + task.getPerformerId() + does not exist if the performer does not exist")})
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Task createTask(@RequestBody Task task) {
        return taskService.addTask(task);
    }


    /**
     * Views tasks
     * exclusively used by Admin without interfering with other users ex:performer
     *
     * @param taskPage           is used as sortation and paging
     * @param taskSearchCriteria uses title of the task for simplifying the sortation and paging
     * @return entity or list of task entities depending on the sortation and paging request
     */

    @Operation(summary = "Views tasks " +
            "exclusively by the Admin without interfering with other users ex:performer", parameters = {@Parameter(name = "task", description = "carries the info of the task"),
            @Parameter(name = "taskPage", description = "is used as sortation and paging"),
            @Parameter(name = "taskSearchCriteria", description = "uses title of the task for simplifying the sortation and paging")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))})
    @GetMapping("/view_tasks")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<Task>> getTasks(TaskPage taskPage, TaskSearchCriteria taskSearchCriteria) {
        return new ResponseEntity<>(taskService.getTasks(taskPage, taskSearchCriteria), HttpStatus.OK);
    }


    /**
     * Deletes task using the performer's id
     * exclusively used by Admin without interfering with other users ex:performer
     *
     * @param taskId carries the id of the performer
     * @throws TaskException if the performer does not exist
     */

    @Operation(summary = "Deletes task using the performer's id" +
            " exclusively used by Admin without interfering with other users ex:performer " +
            "exclusively by the Admin without interfering with other users ex:performer", parameters = {@Parameter(name = "task", description = "carries the info of the task"),
            @Parameter(name = "taskId", description = "carries the id of the performer")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))
                    , @ApiResponse(responseCode = "400", description = "throws TaskException with message if the performer does not exist", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskException.class)))})
    @DeleteMapping("/delete_task_by_id/{taskId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteTaskByPerformerId(@PathVariable Long taskId) {
        taskService.deleteTaskByPerformerId(taskId);
    }


    /**
     * Views task
     * exclusively used by performer without interfering with other users ex:admin or performers with another id
     *
     * @param request uses the reference token which carries the id of the performer
     *                that was saved in the cookie when the performer logged in
     * @return entity task
     * @throws TaskException if task does not exist
     */


    @Operation(summary = "Views task " +
            "exclusively used by performer without interfering with other users ex:admin or performers with another id",
            parameters = {@Parameter(name = "request", description = "uses the reference token which carries the id of the performer\n" +
                    "     *                that was saved in the cookie when the performer logged in")},
            responses = {@ApiResponse(responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))
                    , @ApiResponse(responseCode = "400", description = "throws TaskException with message task does not exist if the task is created yet by the admin", content = @Content(mediaType = "application/json", schema = @Schema(implementation = TaskException.class)))})
    @GetMapping("/view_my_task")
    @PreAuthorize("hasRole('ROLE_PERFORMER')")
    public Task getMyTask(HttpServletRequest request) {
        return taskService.viewMyTask(request);
    }


    /**
     * Updates task
     * exclusively used by performer without interfering with other users ex:admin or performers with another id
     *
     * @param request         uses the reference token which carries the id of the performer
     *                        that was saved in the cookie when the performer logged in
     * @param taskToBeUpdated carries the status and comment of the task that the performer has made
     * @return response body "Task updated successfully" if done successfully
     */


    @Operation(summary = "Updates task" +
            "exclusively used by performer without interfering with other users ex:admin or performers with another id",
            parameters = {@Parameter(name = "request", description = "uses the reference token which carries the id of the performer\n" +
                    "     *                that was saved in the cookie when the performer logged in")},
            responses = {@ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))})
    @PutMapping("/update_my_task")
    @PreAuthorize("hasRole('PERFORMER')")
    public ResponseEntity<?> updateMyTask(@RequestBody Task taskToBeUpdated, HttpServletRequest request) {
        taskService.updateMyTask(taskToBeUpdated, request);
        return ResponseEntity.ok().body("Task updated successfully");
    }


    /**
     * Updates task
     * exclusively used by admin without interfering with other users ex:admin or performers with another id
     *
     * @param task        carries the updated info the task that was made by the admin
     * @param performerId carries the assigned id the performer
     * @return response body "Task updated successfully" if done successfully
     */


    @Operation(summary = "Updates task" +
            "exclusively used by admin without interfering with other users ex:admin or performers with another id",
            parameters = {@Parameter(name = "task", description = "carries the updated info the task that was made by the admin"),
                    @Parameter(name = "performerId", description = "carries the assigned id the performer")},
            responses = {@ApiResponse(responseCode = "200", description = "Task updated successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Task.class)))})
    @PutMapping("/update/{performerId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTaskOfThePerformer(@RequestBody Task task, @PathVariable Long performerId) {
        taskService.updateTaskByPerformerId(task, performerId);
        return ResponseEntity.ok().body("Task updated successfully");
    }
}
