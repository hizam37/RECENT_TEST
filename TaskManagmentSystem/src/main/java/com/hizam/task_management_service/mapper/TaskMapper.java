package com.hizam.task_management_service.mapper;
import com.hizam.task_management_service.dto.TaskDto;
import com.hizam.task_management_service.model.Task;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;


@Mapper(componentModel = "spring")
public interface TaskMapper {


    TaskDto taskToTaskDto(Task task);


    Task taskDtoToTaskEntity(TaskDto taskDto);


    default Page<TaskDto> taskToTaskDtoPage(Page<Task> tasks) {
        List<TaskDto> taskDtos = tasks.getContent().stream()
                .map(this::taskToTaskDto)
                .toList();
        return new PageImpl<>(taskDtos, tasks.getPageable(), tasks.getTotalElements());
    }


}
