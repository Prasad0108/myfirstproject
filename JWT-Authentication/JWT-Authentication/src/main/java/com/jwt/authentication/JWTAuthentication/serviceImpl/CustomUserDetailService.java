package com.jwt.authentication.JWTAuthentication.serviceImpl;

import com.jwt.authentication.JWTAuthentication.entity.User;
import com.jwt.authentication.JWTAuthentication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

    User user=userRepository.findByEmail(username).orElseThrow(()-> new RuntimeException("User not found"));

        return user;
    }
}
