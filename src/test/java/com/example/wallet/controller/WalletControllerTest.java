package com.example.wallet.controller;

import com.example.wallet.config.SecurityConfig;
import com.example.wallet.exception.GlobalExceptionHandler;
import com.example.wallet.security.JwtAuthenticationFilter;
import com.example.wallet.security.JwtService;
import com.example.wallet.service.WalletService;
import com.example.wallet.entity.Transaction;
import com.example.wallet.entity.TransactionType;
import java.time.LocalDateTime;
import java.util.List;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WalletController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        GlobalExceptionHandler.class
})
@AutoConfigureMockMvc(addFilters = false)
//@WithMockUser // apply to all tests in this class. we are going to pretend the one calling this are real logged in user.
//@WithMockUser(username = "09123456789") // MUST match your PHONE constant
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WalletService walletService;

    private final String PHONE = "09123456789";

    @Test
    @WithMockUser(username = PHONE)
    void shouldReturnBalance() throws Exception {
        // Updated method name and parameter type
        when(walletService.getBalanceByPhone(PHONE)).thenReturn(new BigDecimal("100.00"));

        mockMvc.perform(get("/wallets/" + PHONE + "/balance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(100.00));
    }

    @Test
    @WithMockUser(username = PHONE)
    void shouldDepositMoney() throws Exception {
        when(walletService.deposit(eq(PHONE), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("150.00"));

        mockMvc.perform(post("/wallets/" + PHONE + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":50.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(150.00));
    }

    @Test
    @WithMockUser(username = PHONE)
    void shouldWithdrawMoney() throws Exception {
        when(walletService.withdrawByPhone(eq(PHONE), any(BigDecimal.class)))
                .thenReturn(new BigDecimal("70.00"));

        mockMvc.perform(post("/wallets/" + PHONE + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\":30.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(70.00));
    }

    @Test
    void shouldTransferMoney() throws Exception {
        mockMvc.perform(post("/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "fromPhoneNumber": "09123456789",
                    "toPhoneNumber": "09998887766",
                    "amount": 50.00
                }
            """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn400WhenDepositIsNegative() throws Exception {
        mockMvc.perform(post("/wallets/" + PHONE + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"amount\": -50.00}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = PHONE)
    void shouldReturnTransactionHistory() throws Exception {
        // Arrange
        Transaction mockTx = new Transaction();
        mockTx.setAmount(new BigDecimal("100.00"));
        mockTx.setType(TransactionType.TRANSFER);
        mockTx.setTimestamp(LocalDateTime.now());

        // Note: In a real mock, you'd set sender/receiver names here
        when(walletService.getTransactionHistory(PHONE)).thenReturn(List.of(mockTx));

        // Act & Assert
        mockMvc.perform(get("/wallets/" + PHONE + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].type").value("TRANSFER"));
    }

    @Test
    void shouldReturn400WhenTransferAmountIsNegative() throws Exception {
        String invalidJson = """
        {
            "fromPhoneNumber": "09123456789",
            "toPhoneNumber": "09998887766",
            "amount": -100.00
        }
    """;

        mockMvc.perform(post("/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldReturn400WhenInsufficientFunds() throws Exception {
        // Arrange: Mock the service to throw our specific error
        doThrow(new IllegalArgumentException("Insufficient funds"))
                .when(walletService).transferByPhone(eq(PHONE), anyString(), any(BigDecimal.class));

        // Act & Assert
        mockMvc.perform(post("/wallets/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "fromPhoneNumber": "09123456789",
                    "toPhoneNumber": "09998887766",
                    "amount": 1000.00
                }
            """))
                .andExpect(status().isBadRequest()) // Prove it's a 400, not 500
                .andExpect(jsonPath("$.message").value("Insufficient funds"));
    }

    @Test
    @WithMockUser(username = "09111111111") // Logged in as User A
    void shouldReturn403WhenAccessingOtherUsersBalance() throws Exception {
        String otherUserPhone = "09222222222"; // Trying to peek at User B

        mockMvc.perform(get("/wallets/" + otherUserPhone + "/balance"))
                .andExpect(status().isUnauthorized());
    }
}