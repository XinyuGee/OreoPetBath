package com.example.demo.auth;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

  private final UserRepo users;
  private final PasswordEncoder encoder;
  private final JwtService jwt;

  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody LoginRequest req) {
    var user = users.findByUsername(req.username).orElse(null);
    if (user == null || !encoder.matches(req.password, user.getPasswordHash())) {
      return ResponseEntity.status(401).body(new ErrorDto("Invalid credentials"));
    }
    // Reject non-OWNER logins (customers don't log in)
    if (!"OWNER".equals(user.getRole())) {
      return ResponseEntity.status(403).body(new ErrorDto("Owner account required"));
    }
    var token = jwt.issue(user.getUsername(), user.getRole());
    return ResponseEntity.ok(new LoginResponse(token, user.getUsername(), user.getRole()));
  }

  @Data
  static class LoginRequest {
    public String username;
    public String password;
  }

  public record ErrorDto(String message) {
  }

  public record LoginResponse(String token, String username, String role) {
  }
}
