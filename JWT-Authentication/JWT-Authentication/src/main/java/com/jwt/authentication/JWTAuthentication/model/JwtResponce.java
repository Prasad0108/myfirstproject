package com.jwt.authentication.JWTAuthentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jwt.authentication.JWTAuthentication.entity.Role;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public class JwtResponce {

    private String jwtToken;

    private String username;

    private Role role;

    public JwtResponce(String jwtToken, String username, Role role) {
        this.jwtToken = jwtToken;
        this.username = username;
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public JwtResponce(String jwtToken, String username) {
        this.jwtToken = jwtToken;
        this.username = username;

    }

    @Override
    public String toString() {
        return "JwtResponce{" +
                "jwtToken='" + jwtToken + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }

    public JwtResponce() {
    }

}
