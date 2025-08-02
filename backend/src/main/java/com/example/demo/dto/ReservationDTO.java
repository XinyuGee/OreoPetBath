package com.example.demo.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationDTO(
    Long id,
    String petName,
    String ownerName,
    String phone,
    LocalDate date,
    LocalTime time,
    String species,
    String status) {
}
