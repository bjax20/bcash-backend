package com.example.wallet.controller;

import com.example.wallet.dto.LoginRequest;
import com.example.wallet.dto.LoginResponse;
import com.example.wallet.dto.RegisterRequest;
import com.example.wallet.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request.getName(), request.getPhoneNumber(), request.getPassword());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getPhoneNumber(), request.getPassword());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (RuntimeException e) {
            // This will be caught by your GlobalExceptionHandler and return 401/400
            throw e;
        }
    }
}