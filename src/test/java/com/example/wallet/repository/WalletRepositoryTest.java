package com.example.wallet.repository;

import com.example.wallet.entity.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WalletRepositoryTest {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    void shouldSaveAndFindWalletById() {
        // 1. Arrange: Create a new wallet entity
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("500.00"));

        // 2. Act: Save it to the DB
        Wallet savedWallet = walletRepository.save(wallet);

        // 3. Assert: Can we find it back?
        Optional<Wallet> found = walletRepository.findById(savedWallet.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getBalance()).isEqualByComparingTo("500.00");
    }
}