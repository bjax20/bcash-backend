package com.example.wallet.exception;

/**
 * Custom exception to specifically signal login or security failures.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}