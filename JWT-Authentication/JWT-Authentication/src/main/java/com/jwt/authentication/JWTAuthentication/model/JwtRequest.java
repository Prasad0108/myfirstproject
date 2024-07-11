package com.jwt.authentication.JWTAuthentication.model;


import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JwtRequest {
    private String email;
    private String password;

}

