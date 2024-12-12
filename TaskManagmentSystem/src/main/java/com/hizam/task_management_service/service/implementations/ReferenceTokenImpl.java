package com.hizam.task_management_service.service.implementations;

import com.hizam.task_management_service.model.ReferenceToken;


public interface ReferenceTokenImpl {
    ReferenceToken generateReferenceTokenById(Long performerId);
}
