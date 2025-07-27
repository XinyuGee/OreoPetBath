package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Entity
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "service")
public class ServiceOffering {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String code;
  private String name;
  private String description;

  @Column(name = "allowed_days")
  private String allowedDays;
  private LocalTime startTime;
  private LocalTime endTime;
}