package com.badminton.controller;

import com.badminton.dto.BookingDTO;
import com.badminton.dto.request.BookingRequest;
import com.badminton.dto.response.ResponseDTO;
import com.badminton.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customer/bookings")
public class CustomerBookingController {

    private final BookingService bookingService;

    public CustomerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO<BookingDTO>> createBooking(@Valid @RequestBody BookingRequest request) {
        BookingDTO bookingDTO = bookingService.createBooking(request);
        ResponseDTO<BookingDTO> response = ResponseDTO.<BookingDTO>builder()
                .success(true)
                .message("Booking created successfully")
                .data(bookingDTO)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
