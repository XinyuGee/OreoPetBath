package com.example.demo.service;

import com.example.demo.config.ReservationProps;
import com.example.demo.dto.ReservationRequest;
import com.example.demo.exception.BookingConflictException;
import com.example.demo.model.*;
import com.example.demo.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

  private final ReservationRepo reservationRepo;
  private final PetRepo petRepo;
  private final ServiceOfferingRepo serviceRepo;
  private final ReservationProps props;

  public Reservation create(ReservationRequest req) {

    LocalDateTime requested = req.reservationTime();
    LocalDateTime start = requested.minusMinutes(props.bufferMinutes());
    LocalDateTime end = requested.plusMinutes(props.bufferMinutes());

    List<Reservation> clashes = reservationRepo.findByReservationTimeBetween(start, end);

    if (!clashes.isEmpty()) {
      throw new BookingConflictException(
          "Another reservation is already within "
              + props.bufferMinutes() + " minutes of the requested time.");
    }

    var pet = petRepo.findById(req.petId()).orElseThrow();
    var service = serviceRepo.findById(req.serviceId()).orElseThrow();

    return reservationRepo.save(
        Reservation.builder()
            .pet(pet)
            .service(service)
            .reservationTime(requested)
            .notes(req.notes())
            .build());
  }

  public List<Reservation> findAll() {
    return reservationRepo.findAll();
  }

  public List<Reservation> findByPetId(Long petId) {
    return reservationRepo.findByPetId(petId);
  }
}
