package com.jwt.authentication.JWTAuthentication.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvalidateTokenService {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomUserDetailService customUserDetailService;


}
