package com.example.wallet.service;

import com.example.wallet.entity.User;
import com.example.wallet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // Rolls back changes after each test so your Neon DB stays clean
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateWalletWhenUserIsRegistered() {
        // Act
        User savedUser = userService.registerUser("Test User", "09000000000", "password123");

        // Assert
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getWallet()).isNotNull();
        assertThat(savedUser.getWallet().getBalance()).isEqualByComparingTo("0.00");

        // Double check database persistence
        User found = userRepository.findById(savedUser.getId()).get();
        assertThat(found.getWallet()).isNotNull();
    }
}