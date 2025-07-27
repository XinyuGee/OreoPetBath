package com.example.demo.controller;

import com.example.demo.dto.ReservationRequest;
import com.example.demo.model.Reservation;
import com.example.demo.repo.*;
import com.example.demo.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
  private final ReservationRepo reservationRepo;
  private final PetRepo petRepo;
  private final ServiceRepo serviceRepo;

  @PostMapping
  public Reservation create(@RequestBody ReservationRequest req) {
    var pet = petRepo.findById(req.petId()).orElseThrow();
    var service = serviceRepo.findById(req.serviceId()).orElseThrow();

    return reservationRepo.save(
        Reservation.builder()
            .pet(pet)
            .service(service)
            .reservationTime(req.reservationTime())
            .notes(req.notes())
            .build());
  }

  @GetMapping
  public List<Reservation> all() {
    return reservationRepo.findAll();
  }

  // /api/reservations/date?d=2025-07-27
  @GetMapping("/date")
  public List<Reservation> byDate(
      @RequestParam("d") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {

    LocalDateTime start = day.atStartOfDay();
    LocalDateTime end = day.plusDays(1).atStartOfDay().minusNanos(1);
    return reservationRepo.findByReservationTimeBetween(start, end);
  }

  @GetMapping("/pet/{petId}")
  public List<Reservation> byPet(@PathVariable Long petId) {
    return reservationRepo.findByPetId(petId);
  }
}