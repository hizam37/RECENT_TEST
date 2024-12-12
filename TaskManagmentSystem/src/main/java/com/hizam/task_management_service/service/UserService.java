package com.hizam.task_management_service.service;


import com.hizam.task_management_service.exception.RegistrationException;
import com.hizam.task_management_service.model.User;
import com.hizam.task_management_service.repository.UserRepository;
import com.hizam.task_management_service.service.implementations.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class UserService implements UserServiceImpl {

    private final UserRepository userRepository;

    @Override
    public void createUser(User user)
    {
        if(userRepository.existsByEmail(user.getEmail()))
        {
            throw new RegistrationException("User with email "+ user.getEmail() + " already exist");
        }
        userRepository.save(user);
    }

    @Override
    public User getByEmail(String email)
    {
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserDetailsService userDetailsService()
    {
        return this::getByEmail;
    }



}

