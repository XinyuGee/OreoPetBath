package com.example.demo.config;

import com.example.demo.auth.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataSeed {
  private final UserRepo users;
  private final org.springframework.security.crypto.password.PasswordEncoder encoder;

  @Bean
  CommandLineRunner seed() {
    return args -> {
      if (users.findByUsername("OreoPetBath").isEmpty()) {
        users.save(User.builder()
            .username("OreoPetBath")
            .passwordHash(encoder.encode("123456"))
            .role("OWNER")
            .build());
      }
    };
  }
}
