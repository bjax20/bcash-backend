package com.example.wallet.integration;

import com.example.wallet.entity.User;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.entity.TransactionType;
import com.example.wallet.repository.TransactionRepository;
import java.math.BigDecimal;

import com.example.wallet.service.UserService;
import com.example.wallet.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // This is key: it rolls back the DB after the test so Neon stays clean!
class WalletIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();        // Then users (due to foreign keys)
    }

    @Test
    void shouldTransferMoneyBetweenUsersByPhoneNumber() {
        // 1. Arrange: Added "password" to the registerUser calls
        User sender = userService.registerUser("Juan", "09111111111", "password123");
        User receiver = userService.registerUser("Maria", "09222222222", "password123");

        // 2. Deposit
        walletService.deposit("09111111111", new BigDecimal("500.00"));

        // 3. Act
        walletService.transferByPhone("09111111111", "09222222222", new BigDecimal("200.00"));

        // 4. Assert
        assertThat(walletService.getBalanceByPhone("09111111111")).isEqualByComparingTo("300.00");
        assertThat(walletService.getBalanceByPhone("09222222222")).isEqualByComparingTo("200.00");
    }

    @Test
    void shouldRecordTransactionAfterTransfer() {
        // Arrange: Added "password" here too
        userService.registerUser("Sender", "09111111111", "password123");
        userService.registerUser("Receiver", "09222222222", "password123");

        walletService.deposit("09111111111", new BigDecimal("500.00"));

        // Act
        walletService.transferByPhone("09111111111", "09222222222", new BigDecimal("200.00"));

        // Assert
        var transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(2);

        var transferTx = transactions.stream()
                .filter(t -> t.getType() == TransactionType.TRANSFER)
                .findFirst()
                .orElseThrow();

        assertThat(transferTx.getAmount()).isEqualByComparingTo("200.00");
    }
}