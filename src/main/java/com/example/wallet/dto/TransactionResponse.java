package com.example.wallet.dto;

import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private String senderName;
    private String receiverName;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDateTime timestamp;

    public TransactionResponse(Transaction tx) {
        this.senderName = tx.getSender() != null ? tx.getSender().getName() : "System";
        this.receiverName = tx.getReceiver() != null ? tx.getReceiver().getName() : "System";
        this.amount = tx.getAmount();
        this.type = tx.getType();
        this.timestamp = tx.getTimestamp();
    }

    // Getters
    public String getSenderName() { return senderName; }
    public String getReceiverName() { return receiverName; }
    public BigDecimal getAmount() { return amount; }
    public TransactionType getType() { return type; }
    public LocalDateTime getTimestamp() { return timestamp; }
}