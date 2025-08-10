package com.example.demo.config;

import com.example.demo.auth.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.io.IOException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtService jwt;

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable());
    http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    http.cors(cors -> cors.configurationSource(corsSource()));

    http.authorizeHttpRequests(auth -> auth
        // --- public auth endpoint (login) ---
        .requestMatchers("/api/auth/**").permitAll()

        // --- public customer endpoints (Reservation.jsx) ---
        .requestMatchers(HttpMethod.GET, "/api/services/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/pets").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/reservations").permitAll()
        .requestMatchers(HttpMethod.PATCH, "/api/reservations/*/cancel").permitAll()

        // --- owner-only dashboard & reservation management (OwnerDashboard.jsx) ---
        .requestMatchers(HttpMethod.GET, "/api/reservations/dashboard/**").hasRole("OWNER")
        .requestMatchers(HttpMethod.PATCH, "/api/reservations/*/complete").hasRole("OWNER")
        .requestMatchers("/api/reservations/**").hasRole("OWNER")

        // anything else must be authenticated
        .anyRequest().authenticated());

    http.addFilterBefore(new JwtAuthFilter(jwt), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  CorsConfigurationSource corsSource() {
    var cfg = new CorsConfiguration();
    cfg.setAllowedOrigins(List.of("http://localhost:5173"));
    cfg.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    cfg.setAllowedHeaders(List.of("Authorization", "Content-Type"));
    cfg.setAllowCredentials(true);
    var source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", cfg);
    return source;
  }

  static class JwtAuthFilter extends org.springframework.web.filter.OncePerRequestFilter {
    private final JwtService jwt;

    JwtAuthFilter(JwtService jwt) {
      this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
        throws ServletException, IOException {
      String h = req.getHeader("Authorization");
      if (h != null && h.startsWith("Bearer ")) {
        String token = h.substring(7);
        try {
          var jws = jwt.parse(token);
          Claims c = jws.getBody();
          String username = c.getSubject();
          String role = (String) c.get("role");
          var auth = new UsernamePasswordAuthenticationToken(
              username, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
          SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (Exception ignored) {
        }
      }
      chain.doFilter(req, res);
    }
  }
}
