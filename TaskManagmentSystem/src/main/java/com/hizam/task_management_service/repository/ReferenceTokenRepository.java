package com.hizam.task_management_service.repository;


import com.hizam.task_management_service.model.ReferenceToken;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;


@Repository
public interface ReferenceTokenRepository extends JpaRepository<ReferenceToken,Long> {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    ReferenceToken findByReferenceToken(String referenceToken);
}