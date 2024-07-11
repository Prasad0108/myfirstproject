package com.jwt.authentication.JWTAuthentication.exception;

public class ValidationException extends RuntimeException{
    public ValidationException(String message) {
        super(message);
    }
}
