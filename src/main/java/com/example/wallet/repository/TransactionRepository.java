package com.example.wallet.repository;

import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // Find all transactions where the user was either the sender or receiver
    List<Transaction> findBySenderOrReceiverOrderByTimestampDesc(User sender, User receiver);
}