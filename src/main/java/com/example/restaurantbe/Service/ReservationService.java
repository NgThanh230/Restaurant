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

import java.time.LocalDate;
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
        User user = null;
        if (requestDto.getUserId() != null) {
            user = userRepository.findById(requestDto.getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setStartTime(requestDto.getStartTime()); // thời gian khách đặt tới
        reservation.setNumberOfGuests(requestDto.getNumberOfPeople());
        reservation.setStatus("Pending");
        reservation.setNotes(requestDto.getNotes());

        if (user == null) {
            reservation.setGuestName(requestDto.getGuestName());
            reservation.setGuestPhone(requestDto.getGuestPhone());
        }

        return reservationRepository.save(reservation); // createdAt sẽ tự động gán
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

    public Reservation confirmReservation(Integer reservationId) {
        // Lấy reservation từ database
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + reservationId));

        // Kiểm tra trạng thái reservation phải là "Pending" mới có thể xác nhận
        if (!"Pending".equals(reservation.getStatus())) {
            throw new IllegalStateException("Reservation must be in 'Pending' status to be confirmed.");
        }
        // Tìm bàn trống
        RestaurantTable availableTable = (RestaurantTable) tableRepository.findByStatus("Available");
        if (availableTable == null) {
            throw new IllegalStateException("No available table for the reservation.");
        }

        // Cập nhật trạng thái bàn và gán bàn cho reservation
        availableTable.setStatus("Reserved");
        availableTable.setReservedTime(reservation.getStartTime()); // Gán thời gian đặt
        tableRepository.save(availableTable); // Lưu lại trạng thái bàn

        // Gán bàn cho reservation
        reservation.setTable(availableTable);

        // Cập nhật trạng thái reservation
        reservation.setStatus("Confirmed");

        // Lưu và trả về reservation đã cập nhật
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

        List<Reservation> expiredReservations = reservationRepository.findByStatusAndStartTimeBefore("Confirmed", thresholdTime);

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
