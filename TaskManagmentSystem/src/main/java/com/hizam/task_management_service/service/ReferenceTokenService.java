package com.hizam.task_management_service.service;

import com.hizam.task_management_service.model.ReferenceToken;
import com.hizam.task_management_service.repository.ReferenceTokenRepository;
import com.hizam.task_management_service.service.implementations.ReferenceTokenImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReferenceTokenService implements ReferenceTokenImpl {


    private final ReferenceTokenRepository referenceTokenRepository;

    @Transactional
    @Override
    public ReferenceToken generateReferenceTokenById(Long performerId)
    {
        var refreshToken = ReferenceToken.builder().referenceToken(UUID.randomUUID().toString())
                .performerId(performerId)
                .build();
        referenceTokenRepository.save(refreshToken);
        return refreshToken;
    }

}


