package com.badminton.controller;

import com.badminton.dto.response.ResponseDTO;
import com.badminton.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/manager/bookings")
public class ManagerBookingController {

    private final BookingService bookingService;

    public ManagerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ResponseDTO<Void>> approveBooking(@PathVariable Long id) {
        bookingService.approveBooking(id);
        ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                .success(true)
                .message("Booking approved successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ResponseDTO<Void>> rejectBooking(@PathVariable Long id) {
        bookingService.rejectBooking(id);
        ResponseDTO<Void> response = ResponseDTO.<Void>builder()
                .success(true)
                .message("Booking rejected successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revenue-report")
    public ResponseEntity<ResponseDTO<Double>> getRevenueReport(
            @RequestParam int year,
            @RequestParam int month) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Double revenue = bookingService.getMonthlyRevenue(year, month, email);
        ResponseDTO<Double> response = ResponseDTO.<Double>builder()
                .success(true)
                .message("Fetched monthly revenue report successfully")
                .data(revenue)
                .build();
        return ResponseEntity.ok(response);
    }
}
