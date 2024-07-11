package com.jwt.authentication.JWTAuthentication.controller;

import com.jwt.authentication.JWTAuthentication.model.BlackListToken;
import com.jwt.authentication.JWTAuthentication.security.JwtHelper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class LoginController {

    private JwtHelper jwtHelper;
    private BlackListToken blackListToken;

    public LoginController(JwtHelper jwtHelper, BlackListToken blackListToken) {
        this.jwtHelper = jwtHelper;
        this.blackListToken = blackListToken;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            blackListToken.addToBlacklist(token);
        }
        return ResponseEntity.ok("Logged out successfully");
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7); // 7 is the length of "Bearer "
        }
        return null;
    }

}
