package com.example.wallet.service;

import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.TransactionType;
import com.example.wallet.entity.User;
import com.example.wallet.entity.Wallet;
import com.example.wallet.repository.TransactionRepository;
import com.example.wallet.repository.UserRepository;
import com.example.wallet.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public WalletService(WalletRepository walletRepository,
                         UserRepository userRepository,
                         TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public BigDecimal getBalanceByPhone(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getWallet().getBalance();
    }

    public BigDecimal deposit(String phoneNumber, BigDecimal amount) {
        validateAmount(amount);
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = user.getWallet();
        wallet.setBalance(wallet.getBalance().add(amount));

        // Record Deposit
        createTransaction(null, user, amount, TransactionType.DEPOSIT);

        return walletRepository.save(wallet).getBalance();
    }

    public BigDecimal withdrawByPhone(String phoneNumber, BigDecimal amount) {
        validateAmount(amount);
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = user.getWallet();

        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));

        // Record Withdrawal
        createTransaction(user, null, amount, TransactionType.WITHDRAW);

        return walletRepository.save(wallet).getBalance();
    }

    public void transferByPhone(String fromPhone, String toPhone, BigDecimal amount) {
        if (fromPhone.equals(toPhone)) {
            throw new IllegalArgumentException("Cannot transfer to yourself");
        }
        validateAmount(amount);

        User sender = userRepository.findByPhoneNumber(fromPhone)
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findByPhoneNumber(toPhone)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Wallet senderWallet = sender.getWallet();
        Wallet receiverWallet = receiver.getWallet();

        if (senderWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }

        senderWallet.setBalance(senderWallet.getBalance().subtract(amount));
        receiverWallet.setBalance(receiverWallet.getBalance().add(amount));

        // Record Transfer
        createTransaction(sender, receiver, amount, TransactionType.TRANSFER);

        walletRepository.save(senderWallet);
        walletRepository.save(receiverWallet);
    }

    public List<Transaction> getTransactionHistory(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return transactionRepository.findBySenderOrReceiverOrderByTimestampDesc(user, user);
    }

    private void createTransaction(User sender, User receiver, BigDecimal amount, TransactionType type) {
        Transaction tx = new Transaction();
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setAmount(amount);
        tx.setType(type);
        tx.setTimestamp(LocalDateTime.now());
        transactionRepository.save(tx);
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
}