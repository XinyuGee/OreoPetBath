package com.example.demo.repo;

import com.example.demo.model.Reservation;
import com.example.demo.model.ReservationStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {
  List<Reservation> findByReservationTimeBetween(LocalDateTime start, LocalDateTime end);

  List<Reservation> findByPetId(Long petId);

  List<Reservation> findByReservationTimeBetweenAndStatus(
      LocalDateTime start,
      LocalDateTime end,
      ReservationStatus status);
}
