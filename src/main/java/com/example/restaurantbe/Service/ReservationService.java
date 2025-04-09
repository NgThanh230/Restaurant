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
import org.springframework.scheduling.annotation.Scheduled;
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
        RestaurantTable table = tableRepository.findById(requestDto.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + requestDto.getTableId()));

        LocalDateTime startTime = requestDto.getStartTime();
        Integer duration = requestDto.getDurationMinutes() != null ? requestDto.getDurationMinutes() : 90;
        LocalDateTime endTime = startTime.plusMinutes(duration);

        // Kiểm tra trùng giờ
        boolean isConflict = reservationRepository.isTimeConflict(table, startTime, endTime);
        if (isConflict) {
            throw new IllegalStateException("This table is already reserved at the selected time.");
        }

        // Nếu có user → lấy user, không thì khách vãng lai
        User user = null;
        if (requestDto.getUserId() != null) {
            user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }

        // Cập nhật trạng thái bàn
        table.setStatus("Reserved");
        table.setReservedTime(startTime); // optional
        tableRepository.save(table);

        // Tạo reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus(requestDto.getStatus() != null ? requestDto.getStatus() : "Pending");
        reservation.setNotes(requestDto.getNotes());

        // Nếu là khách vãng lai
        if (user == null) {
            reservation.setGuestName(requestDto.getGuestName());
            reservation.setGuestPhone(requestDto.getGuestPhone());
            reservation.setGuestEmail(requestDto.getGuestEmail());
        }

        return reservationRepository.save(reservation);
    }

    public Reservation updateReservationStatus(Integer id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));

        reservation.setStatus(status);
        if ("Cancelled".equalsIgnoreCase(status)) {
            RestaurantTable table = reservation.getTable();
            if (table != null) {
                table.setStatus("Available");
                tableRepository.save(table);
            }
        }
        return reservationRepository.save(reservation);
    }
    public Reservation confirmReservation(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));

        if (!"Pending".equals(reservation.getStatus())) {
            throw new IllegalStateException("Reservation must be in 'Pending' status to be confirmed.");
        }

        reservation.setStatus("Confirmed");
        return reservationRepository.save(reservation);
    }

    public Reservation checkInCustomer(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));

        if (!"Confirmed".equals(reservation.getStatus())) {
            throw new IllegalStateException("Reservation must be in 'Confirmed' status to check-in.");
        }

        reservation.setStatus("Seated"); // Khách đã ngồi vào bàn
        return reservationRepository.save(reservation);
    }

    @Scheduled(fixedRate = 300000)
    public void checkNoShowReservations() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thresholdTime = now.minusMinutes(15); // Quá 15 phút so với thời gian đặt bàn

        List<Reservation> expiredReservations = reservationRepository.findByStatusAndReservationDateBefore("Confirmed", thresholdTime);

        for (Reservation reservation : expiredReservations) {
            // Cập nhật trạng thái đặt bàn thành "No-Show"
            reservation.setStatus("No-Show");

            // Giải phóng bàn (trạng thái "Available")
            RestaurantTable table = reservation.getTable();
            if (table != null) {
                table.setStatus("Available");
                tableRepository.save(table);
            }

            reservationRepository.save(reservation);
        }
    }
}
