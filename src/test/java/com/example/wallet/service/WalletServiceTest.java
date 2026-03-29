package com.example.wallet.service;

import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional // Automatically rolls back changes so Neon DB stays clean
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService; // Need this to create users

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    private final String testPhone = "09123456789";
    @Autowired
    private TransactionRepository transactionRepository;
    @BeforeEach
    void setUp() {
        // 2. Clear in the correct order: Child -> Parent
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();

        // 3. Setup fresh data
        userService.registerUser("Test User", testPhone, "password123");
        walletService.deposit(testPhone, new BigDecimal("100.00"));
    }

    @Test
    void shouldReturnCorrectBalance() {
        // Changed method to getBalanceByPhone
        BigDecimal result = walletService.getBalanceByPhone(testPhone);
        assertEquals(0, new BigDecimal("100.00").compareTo(result));
    }

    @Test
    void shouldIncreaseBalanceAfterDeposit() {
        // Changed to use phone number string
        BigDecimal result = walletService.deposit(testPhone, new BigDecimal("50.00"));
        assertEquals(0, new BigDecimal("150.00").compareTo(result));
    }

    @Test
    void shouldRejectNegativeDeposit() {
        assertThrows(IllegalArgumentException.class, () -> {
            walletService.deposit(testPhone, new BigDecimal("-10.00"));
        });
    }

    @Test
    void shouldTransferMoneySuccessfully() {
        // Arrange
        String receiverPhone = "09998887766";
        userService.registerUser("Receiver", receiverPhone, "password123");
        walletService.deposit(receiverPhone, new BigDecimal("50.00"));

        // Act - Using the new transferByPhone method
        walletService.transferByPhone(testPhone, receiverPhone, new BigDecimal("30.00"));

        // Assert
        assertEquals(0, new BigDecimal("70.00").compareTo(walletService.getBalanceByPhone(testPhone)));
        assertEquals(0, new BigDecimal("80.00").compareTo(walletService.getBalanceByPhone(receiverPhone)));
    }
}