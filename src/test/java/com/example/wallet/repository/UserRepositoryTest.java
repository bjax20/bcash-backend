package com.example.wallet.repository;

import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUserWithWallet() {
        // Arrange
        User user = new User();
        user.setPhoneNumber("09123456789");
        user.setName("Juan Dela Cruz");
        user.setPassword("dummy_hash");

        Wallet wallet = new Wallet(new BigDecimal("100.00"));
        user.setWallet(wallet);
        wallet.setUser(user);

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getWallet().getBalance()).isEqualByComparingTo("100.00");
        assertThat(savedUser.getWallet().getUser().getPhoneNumber()).isEqualTo("09123456789");
    }
}