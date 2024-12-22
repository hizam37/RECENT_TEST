package com.hizam.task_management_service.repository;

import com.hizam.task_management_service.model.Task;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TaskRepository extends CrudRepository<Task,Long> {

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    Task findByPerformerId(Long performerId);

}
