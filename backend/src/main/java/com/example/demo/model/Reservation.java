package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

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

  @Column(nullable = false, length = 32)
  private String ownerPhone;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReservationStatus status = ReservationStatus.BOOKED;

  @Column(nullable = false)
  private LocalDateTime reservationTime;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @Version
  @Column(nullable = false)
  private Long version;

  private String notes;
}
