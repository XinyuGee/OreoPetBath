package com.example.demo.controller;

import com.example.demo.auth.AuthController;
import com.example.demo.auth.JwtService;
import com.example.demo.auth.User;
import com.example.demo.auth.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepo userRepo;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("owner")
                .passwordHash("$2a$10$encodedHash")
                .role("OWNER")
                .build();
    }

    @Test
    void testLogin_WithValidCredentials_ShouldReturnToken() throws Exception {
        // Given
        String requestBody = "{\"username\":\"owner\",\"password\":\"password123\"}";
        String token = "test-jwt-token";

        when(userRepo.findByUsername("owner")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPasswordHash())).thenReturn(true);
        when(jwtService.issue("owner", "OWNER")).thenReturn(token);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token))
                .andExpect(jsonPath("$.username").value("owner"))
                .andExpect(jsonPath("$.role").value("OWNER"));

        verify(userRepo, times(1)).findByUsername("owner");
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPasswordHash());
        verify(jwtService, times(1)).issue("owner", "OWNER");
    }

    @Test
    void testLogin_WithInvalidUsername_ShouldReturn401() throws Exception {
        // Given
        String requestBody = "{\"username\":\"invalid\",\"password\":\"password123\"}";

        when(userRepo.findByUsername("invalid")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));

        verify(userRepo, times(1)).findByUsername("invalid");
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).issue(any(), any());
    }

    @Test
    void testLogin_WithInvalidPassword_ShouldReturn401() throws Exception {
        // Given
        String requestBody = "{\"username\":\"owner\",\"password\":\"wrongpassword\"}";

        when(userRepo.findByUsername("owner")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPasswordHash())).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));

        verify(userRepo, times(1)).findByUsername("owner");
        verify(passwordEncoder, times(1)).matches("wrongpassword", testUser.getPasswordHash());
        verify(jwtService, never()).issue(any(), any());
    }

    @Test
    void testLogin_WithNonOwnerRole_ShouldReturn403() throws Exception {
        // Given
        User customerUser = User.builder()
                .id(2L)
                .username("customer")
                .passwordHash("$2a$10$encodedHash")
                .role("CUSTOMER")
                .build();

        String requestBody = "{\"username\":\"customer\",\"password\":\"password123\"}";

        when(userRepo.findByUsername("customer")).thenReturn(Optional.of(customerUser));
        when(passwordEncoder.matches("password123", customerUser.getPasswordHash())).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Owner account required"));

        verify(userRepo, times(1)).findByUsername("customer");
        verify(passwordEncoder, times(1)).matches("password123", customerUser.getPasswordHash());
        verify(jwtService, never()).issue(any(), any());
    }
}

