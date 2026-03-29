package com.example.wallet.service;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(String name, String phoneNumber, String password) {
        // 1. Check if user already exists
        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new IllegalArgumentException("Phone number already registered");
        }

        // 2. Create the User object
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);

        // 3. Create the Wallet and link it (The "GCash" magic)
        Wallet wallet = new Wallet(BigDecimal.ZERO);
        wallet.setUser(user);
        user.setWallet(wallet);

        // 4. Save the user (The wallet saves automatically because of CascadeType.ALL)
        return userRepository.save(user);
    }
}