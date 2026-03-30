package com.example.wallet.dto;

/**
 * Composite response containing the JWT and essential user profile data.
 */
public record LoginResponse(
        String token,
        UserView user
) {
    /**
     * Minimalist view of the user for frontend consumption.
     * Excludes sensitive data like password hashes or heavy transaction lists.
     */
    public record UserView(
            String name,
            String phoneNumber
    ) {}
}