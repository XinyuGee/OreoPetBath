package com.example.demo.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
  private Key key;
  private long ttlMillis;

  public JwtService(
      @Value("${app.jwt.secret}") String secret,
      @Value("${app.jwt.ttlMillis}") long ttlMillis) {
    this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.ttlMillis = ttlMillis;
  }

  public String issue(String username, String role) {
    long now = System.currentTimeMillis();
    return Jwts.builder()
        .setSubject(username)
        .addClaims(Map.of("role", role))
        .setIssuedAt(new Date(now))
        .setExpiration(new Date(now + ttlMillis))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }

}
