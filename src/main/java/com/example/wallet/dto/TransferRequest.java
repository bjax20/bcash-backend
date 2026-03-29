package com.example.wallet.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class TransferRequest {
    @NotBlank(message = "Sender phone is required")
    private String fromPhoneNumber;

    @NotBlank(message = "Receiver phone is required")
    private String toPhoneNumber;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    // Getters and Setters for these new String fields...
    public String getFromPhoneNumber() { return fromPhoneNumber; }
    public void setFromPhoneNumber(String fromPhoneNumber) { this.fromPhoneNumber = fromPhoneNumber; }
    public String getToPhoneNumber() { return toPhoneNumber; }
    public void setToPhoneNumber(String toPhoneNumber) { this.toPhoneNumber = toPhoneNumber; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}