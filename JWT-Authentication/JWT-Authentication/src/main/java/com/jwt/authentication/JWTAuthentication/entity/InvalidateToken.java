package com.jwt.authentication.JWTAuthentication.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvalidateToken {
    private String email;
    private String secretCode;
}
