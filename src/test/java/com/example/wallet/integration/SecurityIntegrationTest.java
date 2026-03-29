package com.example.wallet.integration;

import com.example.wallet.config.SecurityConfig;
import com.example.wallet.exception.GlobalExceptionHandler;
import com.example.wallet.security.JwtAuthenticationFilter;
import com.example.wallet.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        GlobalExceptionHandler.class
})
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn403WhenAccessingProtectedResourceWithoutToken() throws Exception {
        // We are NOT using @WithMockUser here
        mockMvc.perform(get("/wallets/09123456789/balance"))
                .andExpect(status().isUnauthorized()); // Or 401/403 depending on your config
    }

    @Test
    void shouldAccessPublicEndpointWithoutToken() throws Exception {
        // Register endpoint should be publicly accessible
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))  // Don't care about valid data here
                .andExpect(status().isBadRequest()); // Will fail validation, but not auth
    }
}