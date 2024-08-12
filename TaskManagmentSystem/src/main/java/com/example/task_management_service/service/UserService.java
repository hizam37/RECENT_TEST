package com.example.task_management_service.service;


import com.example.task_management_service.model.User;
import com.example.task_management_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(User user)
    {
        userRepository.save(user);
    }


    public User getByUserName(String username)
    {
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    public UserDetailsService userDetailsService()
    {
        return this::getByUserName;
    }



}

