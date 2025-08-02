package com.example.demo.controller;

import com.example.demo.dto.ReservationDTO;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.model.Reservation;
import com.example.demo.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
  private final ReservationService svc;

  @PostMapping
  public Reservation create(@RequestBody ReservationRequest req) {
    return svc.create(req);
  }

  @GetMapping
  public List<Reservation> all() {
    return svc.findAll();
  }

  @GetMapping("/date")
  public List<Reservation> byDate(@RequestParam("d") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate day) {
    LocalDateTime start = day.atStartOfDay();
    LocalDateTime end = day.plusDays(1).atStartOfDay().minusNanos(1);
    return svc.findAll().stream()
        .filter(r -> !r.getReservationTime().isBefore(start)
            && r.getReservationTime().isBefore(end))
        .toList();
  }

  @GetMapping("/pet/{petId}")
  public List<Reservation> byPet(@PathVariable Long petId) {
    return svc.findByPetId(petId);
  }

  @PatchMapping("/{id}/cancel")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void cancel(@PathVariable long id,
      @RequestBody CancelReservationDto body) {
    svc.cancel(id, body.phone());
  }

  public record CancelReservationDto(String phone) {
  }

  @GetMapping("/dashboard")
  public List<ReservationDTO> dashboard(
      @RequestParam(required = false) String phone,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

    return svc.findAll().stream()
        .filter(r -> phone == null || r.getOwnerPhone().contains(phone))
        .filter(r -> date == null || r.getReservationTime().toLocalDate().equals(date))
        .map(this::toReservationDTO)
        .toList();
  }

  private ReservationDTO toReservationDTO(Reservation r) {
    return new ReservationDTO(
        r.getId(),
        r.getPet().getName(),
        r.getPet().getOwnerName(),
        r.getOwnerPhone(),
        r.getReservationTime().toLocalDate(),
        r.getReservationTime().toLocalTime(),
        r.getPet().getSpecies(),
        r.getStatus().name());
  }
}