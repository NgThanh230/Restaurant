package com.example.restaurantbe.Controller;

import com.example.restaurantbe.DTO.ReservationRequestDto;
import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Entity.User;
import com.example.restaurantbe.Service.ReservationService;
import com.example.restaurantbe.Service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;
    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return new ResponseEntity<>(reservations, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequestDto requestDto, @RequestParam(required = false) Long userId) {
        if (userId != null) {
            requestDto.setUserId(userId);
        }

        Reservation reservation = reservationService.createReservation(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Integer id) {
        Reservation reservation = reservationService.getReservationById(id);
        return new ResponseEntity<>(reservation, HttpStatus.OK);
    }
    //hủy đặt bàn
    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservation> updateReservationStatus(
            @PathVariable Integer id,
            @RequestParam String status) {
        Reservation updatedReservation = reservationService.updateReservationStatus(id, status);
        return new ResponseEntity<>(updatedReservation, HttpStatus.OK);
    }
    @PutMapping("/{reservationId}/confirm")
    public ResponseEntity<?> confirmReservation(@PathVariable Integer reservationId, @RequestBody Map<String, Long> body) {
        Long tableId = body.get("tableId");
        Reservation reservation = reservationService.confirmReservation(reservationId, tableId);
        return ResponseEntity.ok(reservation);
    }

    @PatchMapping("/{id}/check-in")
    public ResponseEntity<Reservation> checkInCustomer(@PathVariable Integer id) {
        try {
            Reservation updatedReservation = reservationService.checkInCustomer(id);
            return ResponseEntity.ok(updatedReservation);
        } catch (EntityNotFoundException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
