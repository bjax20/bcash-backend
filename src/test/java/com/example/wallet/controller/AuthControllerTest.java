package com.example.wallet.controller;

import com.example.wallet.config.SecurityConfig;
import com.example.wallet.dto.LoginResponse;
import com.example.wallet.exception.AuthenticationException;
import com.example.wallet.exception.GlobalExceptionHandler;
import com.example.wallet.security.JwtAuthenticationFilter;
import com.example.wallet.security.JwtService;
import com.example.wallet.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        JwtAuthenticationFilter.class,
        JwtService.class,
        GlobalExceptionHandler.class
})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldRegisterUserAndReturnSuccess() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "name": "Juan Dela Cruz",
                    "phoneNumber": "09123456789",
                    "password": "securePassword123"
                }
            """))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLoginAndReturnTokenAndUser() throws Exception {
        // Arrange
        LoginResponse.UserView mockUser = new LoginResponse.UserView("Juan Dela Cruz", "09123456789");
        LoginResponse mockResponse = new LoginResponse("mock-jwt-token", mockUser);

        when(authService.login("09123456789", "securePassword123"))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                    "phoneNumber": "09123456789",
                    "password": "securePassword123"
                }
            """))
                .andExpect(status().isOk())
                // Verify Token
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                // Verify Nested User Object
                .andExpect(jsonPath("$.user.name").value("Juan Dela Cruz"))
                .andExpect(jsonPath("$.user.phoneNumber").value("09123456789"));
    }
    @Test
    void shouldReturn401WhenLoginFails() throws Exception {
        when(authService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"phoneNumber\":\"09111111111\", \"password\":\"wrong\"}"))
                .andExpect(status().isUnauthorized());
    }
}