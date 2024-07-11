package com.jwt.authentication.JWTAuthentication.security;

import com.jwt.authentication.JWTAuthentication.entity.Role;
import com.jwt.authentication.JWTAuthentication.model.BlackListToken;
import com.jwt.authentication.JWTAuthentication.serviceImpl.CustomUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;

@Component
public class JwtHelper {

    //requirement :

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Autowired
    private BlackListToken blackListToken;
    public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

    //    public static final long JWT_TOKEN_VALIDITY =  60;
    private String secret = "afafasfafafasfasfasfafacasdasfasxASFACASDFACASDFASFASFDAFASFASDAADSCSDFADCVSGCFVADXCcadwavfsfarvf";

    //retrieve username from jwt token
    public String getUsernameFromToken(String token) {

        return getClaimFromToken(token, Claims::getSubject);
    }

    //retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {

        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    //for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setAllowedClockSkewSeconds(1000)
                .setSigningKey(secret).parseClaimsJws(token).getBody();

//         .setAllowedClockSkewSeconds(60) // Allow a 60-second (1-minute) time difference
//                .setSigningKey(SECRET_KEY)
//                .parseClaimsJws(token)
//                .getBody();
    }

    //check if the token has expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public String generateToken(String userEmail ,Role role) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(userEmail);
        Map<String, Object> claims = new HashMap<>();

                // Create a set of roles to ensure uniqueness
                Set<String> roles = new HashSet<>();

                // Add the existing role to the set
                if (role != null) {
                    roles.add(role.name());
                }
                claims.put("role",roles);
        return generateToken(claims, userDetails);
    }

    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {

        return Jwts.builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    //validate token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token) && !blackListToken.isTokenBlacklisted(token));
    }



}
