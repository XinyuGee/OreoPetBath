package com.example.demo.dto;

import java.time.LocalDateTime;

public record ReservationRequest(
        Long petId,
        Long serviceId,
        LocalDateTime reservationTime,
        String notes) {
}
