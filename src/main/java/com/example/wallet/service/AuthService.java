package com.example.wallet.service;

import com.example.wallet.dto.LoginResponse; // Ensure this is imported
import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Updated to return LoginResponse (Token + UserView)
     * instead of a raw String.
     */
    public LoginResponse login(String phoneNumber, String rawPassword) {
        // 1. Find the user
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // 2. Check password matches the HASH
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        // 3. Generate the token
        String token = jwtService.generateToken(phoneNumber);

        // 4. Map to DTO and return
        // This provides the 'user' object your React AuthContext is looking for
        return new LoginResponse(
                token,
                new LoginResponse.UserView(user.getName(), user.getPhoneNumber())
        );
    }

    @Transactional
    public void register(String name, String phoneNumber, String rawPassword) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        // 1. Hash the password
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 2. Create User
        User user = new User(name, phoneNumber, encodedPassword);

        // 3. Attach empty Wallet
        Wallet wallet = new Wallet(BigDecimal.ZERO);
        wallet.setUser(user);
        user.setWallet(wallet);

        userRepository.save(user);
    }
}