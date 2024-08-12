package com.example.task_management_service.repository;


import com.example.task_management_service.model.ReferenceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ReferenceTokenRepository extends JpaRepository<ReferenceToken,Long> {
    Optional<ReferenceToken> findByToken(String token);


}