package com.example.demo.repo;

import com.example.demo.model.Reservation;
import com.example.demo.model.ReservationStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepo extends JpaRepository<Reservation, Long> {
  List<Reservation> findByReservationTimeBetween(LocalDateTime start, LocalDateTime end);

  List<Reservation> findByPetId(Long petId);

  List<Reservation> findByReservationTimeBetweenAndStatus(
      LocalDateTime start,
      LocalDateTime end,
      ReservationStatus status);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
         "FROM Reservation r " +
         "WHERE r.reservationTime BETWEEN :start AND :end " +
         "AND r.status = :status " +
         "AND r.service.code != :serviceExcluded")
  boolean existsByReservationTimeBetweenAndStatusAndService_CodeNot(
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end,
      @Param("status") ReservationStatus status,
      @Param("serviceExcluded") String serviceExcluded);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT r FROM Reservation r WHERE r.id = :id")
  Optional<Reservation> findByIdWithLock(@Param("id") Long id);
}
