package com.example.restaurantbe.Service;

import com.example.restaurantbe.DTO.ReservationRequestDto;
import com.example.restaurantbe.Entity.Reservation;
import com.example.restaurantbe.Entity.RestaurantTable;
import com.example.restaurantbe.Entity.User;
import com.example.restaurantbe.Repository.ReservationRepository;
import com.example.restaurantbe.Repository.RestaurantTableRepository;
import com.example.restaurantbe.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final UserRepository userRepository;
    private final RestaurantTableRepository tableRepository;
    private ReservationRepository reservationRepository;
    @Autowired
    public ReservationService(UserRepository userRepository, RestaurantTableRepository tableRepository, ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.tableRepository = tableRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation getReservationById(Integer id) {
        return reservationRepository.findById(id).orElse(null);
    }

    public List<Reservation> getReservationsByCustomerName(String customerName) {
        return reservationRepository.findByUserName(customerName);
    }
    public List<Reservation> getReservationsByTableNumber(String tableNumber) {
        return reservationRepository.findByTable_TableNumber(tableNumber);
    }
    public Reservation createReservation(ReservationRequestDto requestDto) {
        User user = userRepository.findById(Long.valueOf(requestDto.getUserId()))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + requestDto.getUserId()));

        RestaurantTable table = tableRepository.findById(requestDto.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + requestDto.getTableId()));
        if ("Reserved".equalsIgnoreCase(table.getStatus())) {
            throw new IllegalStateException("Table is already reserved!");
        }

        // Cập nhật trạng thái bàn thành "Reserved"
        table.setStatus("Reserved");
        tableRepository.save(table);
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setReservationDate(LocalDateTime.now());
        reservation.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "Pending");
        reservation.setNotes(requestDto.getNotes());

        return reservationRepository.save(reservation);
    }
    public Reservation updateReservationStatus(Integer id, String newStatus) {
        Reservation reservation = getReservationById(id);
        reservation.setStatus(newStatus);
        return reservationRepository.save(reservation);
    }
    public void cancelReservation(Integer id) {
        Reservation reservation = getReservationById(id);
        reservation.setStatus("Cancelled");
        reservationRepository.save(reservation);
    }
}
