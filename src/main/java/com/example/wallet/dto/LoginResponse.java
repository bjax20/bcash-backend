package com.example.wallet.dto;

/**
 * A simple record to wrap the JWT token in a JSON object: {"token": "..."}
 */
public record LoginResponse(String token) {
}