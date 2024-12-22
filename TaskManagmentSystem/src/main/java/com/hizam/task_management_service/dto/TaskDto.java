package com.hizam.task_management_service.dto;

import com.hizam.task_management_service.model.TaskPriority;
import com.hizam.task_management_service.model.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;


@Data
public class TaskDto {

    private Long id;

    private String title;

    private String description;


    private TaskStatus taskStatus;


    private TaskPriority taskPriority;


    private String author;


    private Long performerId;


    private String comment;

}
