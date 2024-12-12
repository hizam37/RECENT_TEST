package com.hizam.task_management_service.service.implementations;

import com.hizam.task_management_service.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserServiceImpl {


    void createUser(User user);

    User getByEmail(String username);

    UserDetailsService userDetailsService();
}
