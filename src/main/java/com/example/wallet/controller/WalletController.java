package com.example.wallet.controller;

import com.example.wallet.dto.BalanceResponse;
import com.example.wallet.dto.TransactionRequest;
import com.example.wallet.dto.TransferRequest;
import com.example.wallet.exception.AuthenticationException;
import com.example.wallet.service.WalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.example.wallet.dto.TransactionResponse;
import java.math.BigDecimal;
import java.util.Map;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    private void validateOwner(String phoneNumber) {
        String authenticatedPhone = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!authenticatedPhone.equals(phoneNumber)) {
            throw new AuthenticationException("Access Denied: You do not own this wallet.");
        }
    }

    // Changed {id} to {phoneNumber} and Long to String
    @GetMapping("/{phoneNumber}/balance")
    public ResponseEntity<BalanceResponse> getBalance(@PathVariable String phoneNumber) {
        validateOwner(phoneNumber); // Check ownership first!
        return ResponseEntity.ok(new BalanceResponse(walletService.getBalanceByPhone(phoneNumber)));
    }

    @PostMapping("/{phoneNumber}/deposit")
    public ResponseEntity<?> deposit(@PathVariable String phoneNumber, @Valid @RequestBody TransactionRequest request) {
        validateOwner(phoneNumber); // Check ownership first!
        BigDecimal newBalance = walletService.deposit(phoneNumber, request.getAmount());
        return ResponseEntity.ok(Map.of("balance", newBalance));
    }

    @PostMapping("/{phoneNumber}/withdraw")
    public ResponseEntity<?> withdraw(
            @PathVariable String phoneNumber,
            @Valid @RequestBody TransactionRequest request) {

        validateOwner(phoneNumber); // check ownership first
        // Now this matches the Service method
        BigDecimal newBalance = walletService.withdrawByPhone(phoneNumber, request.getAmount());

        return ResponseEntity.ok(Map.of("balance", newBalance));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest request) {

        // Using the new transferByPhone method we just wrote!
        walletService.transferByPhone(
                request.getFromPhoneNumber(), // Make sure your TransferRequest DTO uses String phone numbers!
                request.getToPhoneNumber(),
                request.getAmount()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{phoneNumber}/transactions")
    public ResponseEntity<List<TransactionResponse>> getHistory(@PathVariable String phoneNumber) {
        validateOwner(phoneNumber); // Check ownership first!
        List<TransactionResponse> history = walletService.getTransactionHistory(phoneNumber)
                .stream()
                .map(TransactionResponse::new)
                .toList();

        return ResponseEntity.ok(history);
    }
}