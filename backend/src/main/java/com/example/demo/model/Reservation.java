package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  private Pet pet;
  @ManyToOne(optional = false)
  private ServiceOffering service;

  @Column(nullable = false)
  private LocalDateTime reservationTime;
  private String notes;
}
