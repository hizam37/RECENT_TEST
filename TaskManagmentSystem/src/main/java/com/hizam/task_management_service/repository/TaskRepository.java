package com.hizam.task_management_service.repository;

import com.hizam.task_management_service.model.Task;
import com.hizam.task_management_service.model.TaskPriority;
import com.hizam.task_management_service.model.TaskStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface TaskRepository extends CrudRepository<Task,Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Task findByPerformerId(Long performerId);

}
