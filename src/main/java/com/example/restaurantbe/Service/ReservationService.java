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

        Reservation reservation = new Reservation();
        reservation.setStartTime(requestDto.getStartTime()); // thời gian khách đặt tới
        reservation.setNumberOfGuests(requestDto.getNumberOfGuests());
        reservation.setStatus("Pending");
        reservation.setNotes(requestDto.getNotes());
        reservation.setGuestName(requestDto.getGuestName());
        reservation.setGuestPhone(requestDto.getGuestPhone());
        return reservationRepository.save(reservation);
    }



    public Reservation updateReservationStatus(Integer id, String status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Bàn Đặt Không Tìm Thấy id: " + id));

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

    public Reservation confirmReservation(Integer reservationId, Long tableId) {
        // Lấy reservation từ database
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn đặt bàn với ID: " + reservationId));

        // Kiểm tra trạng thái reservation phải là "Pending"
        if (!"Pending".equals(reservation.getStatus())) {
            throw new IllegalStateException("Chỉ có thể xác nhận đơn đặt bàn đang ở trạng thái 'Đang chờ'.");
        }

        // Lấy bàn được nhân viên chọn
        RestaurantTable table = tableRepository.findById(Math.toIntExact(tableId))
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bàn với ID: " + tableId));

        // Kiểm tra trạng thái bàn
        if (!"Available".equals(table.getStatus())) {
            throw new IllegalStateException("Bàn đã được sử dụng hoặc chưa sẵn sàng.");
        }

        // Gán bàn cho reservation
        table.setStatus("Reserved");
        table.setReservedTime(reservation.getStartTime());
        tableRepository.save(table);

        reservation.setTable(table);
        reservation.setStatus("Confirmed");

        return reservationRepository.save(reservation);
    }



    public Reservation checkInCustomer(Integer id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không Thấy Bàn Đặt id: " + id));

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
