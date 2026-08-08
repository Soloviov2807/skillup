package com.skillup.user_service.service;


import com.skillup.user_service.exception.UserNotFoundException;
import com.skillup.user_service.model.User;
import com.skillup.user_service.model.UserPrincipal;
import com.skillup.user_service.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo repo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        User user = repo.findByName(username).orElseThrow(() -> new UserNotFoundException("User not found"));

        return new UserPrincipal(user);

    }

}
