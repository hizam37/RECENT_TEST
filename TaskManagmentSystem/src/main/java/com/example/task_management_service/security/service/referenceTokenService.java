package com.example.task_management_service.security.service;

import com.example.task_management_service.model.ReferenceToken;
import com.example.task_management_service.repository.ReferenceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class referenceTokenService {


    private final ReferenceTokenRepository referenceTokenRepository;

    public ReferenceToken generateRefreshTokenByPerformerId(Long performerId)
    {
        var refreshToken = ReferenceToken.builder().token(UUID.randomUUID().toString())
                .performerId(performerId)
                .build();
        referenceTokenRepository.save(refreshToken);
        return refreshToken;
    }

}


