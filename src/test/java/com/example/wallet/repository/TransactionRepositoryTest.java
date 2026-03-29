package com.example.wallet.repository;

import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.User;
import com.example.wallet.entity.TransactionType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndRetrieveTransaction() {
        // Arrange
        User sender = userRepository.save(new User("Juan", "09111111111", "password123"));
        User receiver = userRepository.save(new User("Maria", "09222222222", "password123"));

        Transaction tx = new Transaction();
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setAmount(new BigDecimal("200.00"));
        tx.setType(TransactionType.TRANSFER);
        tx.setTimestamp(LocalDateTime.now());

        // Act
        Transaction saved = transactionRepository.save(tx);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getSender().getPhoneNumber()).isEqualTo("09111111111");
        assertThat(saved.getAmount()).isEqualByComparingTo("200.00");
    }
}