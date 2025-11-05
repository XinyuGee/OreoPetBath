package com.example.demo.auth;

import com.example.demo.auth.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String secret;
    private long ttlMillis;

    @BeforeEach
    void setUp() {
        // Generate a valid base64 secret key for testing
        secret = Base64.getEncoder().encodeToString("test-secret-key-for-jwt-signing-12345678901234567890".getBytes());
        ttlMillis = 3600000L; // 1 hour in milliseconds
        jwtService = new JwtService(secret, ttlMillis);
    }

    @Test
    void testIssue_ShouldGenerateValidToken() {
        // Given
        String username = "testuser";
        String role = "OWNER";

        // When
        String token = jwtService.issue(username, role);

        // Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.contains(".")); // JWT has 3 parts separated by dots
    }

    @Test
    void testIssue_ShouldIncludeUsernameInToken() {
        // Given
        String username = "testuser";
        String role = "OWNER";

        // When
        String token = jwtService.issue(username, role);
        Jws<Claims> parsed = jwtService.parse(token);

        // Then
        assertEquals(username, parsed.getBody().getSubject());
    }

    @Test
    void testIssue_ShouldIncludeRoleInToken() {
        // Given
        String username = "testuser";
        String role = "OWNER";

        // When
        String token = jwtService.issue(username, role);
        Jws<Claims> parsed = jwtService.parse(token);

        // Then
        assertEquals(role, parsed.getBody().get("role"));
    }

    @Test
    void testParse_WithValidToken_ShouldReturnClaims() {
        // Given
        String username = "testuser";
        String role = "OWNER";
        String token = jwtService.issue(username, role);

        // When
        Jws<Claims> parsed = jwtService.parse(token);

        // Then
        assertNotNull(parsed);
        assertNotNull(parsed.getBody());
        assertEquals(username, parsed.getBody().getSubject());
        assertEquals(role, parsed.getBody().get("role"));
    }

    @Test
    void testParse_WithInvalidToken_ShouldThrowJwtException() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(JwtException.class, () -> jwtService.parse(invalidToken));
    }

    @Test
    void testIssue_WithDifferentUsers_ShouldGenerateDifferentTokens() {
        // Given
        String username1 = "user1";
        String username2 = "user2";
        String role = "OWNER";

        // When
        String token1 = jwtService.issue(username1, role);
        String token2 = jwtService.issue(username2, role);

        // Then
        assertNotEquals(token1, token2);
    }

    @Test
    void testIssue_ShouldSetExpiration() {
        // Given
        String username = "testuser";
        String role = "OWNER";

        // When
        String token = jwtService.issue(username, role);
        Jws<Claims> parsed = jwtService.parse(token);

        // Then
        assertNotNull(parsed.getBody().getExpiration());
        assertTrue(parsed.getBody().getExpiration().after(parsed.getBody().getIssuedAt()));
    }
}
