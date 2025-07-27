package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // Pet Information
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  private String species;
  private String breed;
  private Integer age;

  // Owner Information
  @Column(nullable = false)
  private String ownerName;
  @Column(nullable = false)
  private String ownerPhone;
}