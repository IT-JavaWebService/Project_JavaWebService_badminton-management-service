package com.badminton.service.impl;

import com.badminton.dto.BookingDTO;
import com.badminton.dto.request.BookingRequest;
import com.badminton.entity.Booking;
import com.badminton.entity.Court;
import com.badminton.entity.TimeSlot;
import com.badminton.entity.User;
import com.badminton.exception.CustomException;
import com.badminton.repository.BookingRepository;
import com.badminton.repository.CourtRepository;
import com.badminton.repository.TimeSlotRepository;
import com.badminton.repository.UserRepository;
import com.badminton.service.BookingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final CourtRepository courtRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              CourtRepository courtRepository,
                              TimeSlotRepository timeSlotRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.courtRepository = courtRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public BookingDTO createBooking(BookingRequest request) {
        // 1. Check if Court exists
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new CustomException("Court not found", HttpStatus.NOT_FOUND));

        // 2. Check if TimeSlot exists
        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new CustomException("Time slot not found", HttpStatus.NOT_FOUND));

        // 3. Check for conflict (PENDING or CONFIRMED status on that date and court)
        boolean hasConflict = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotIdAndStatusIn(
                request.getCourtId(),
                request.getBookingDate(),
                request.getTimeSlotId(),
                List.of("PENDING", "CONFIRMED")
        );
        if (hasConflict) {
            throw new CustomException("This time slot is already booked on the selected date", HttpStatus.CONFLICT);
        }

        // Get or create a default customer for Day 1 (since JWT authentication is not active today)
        User customer = userRepository.findByUsername("default_customer")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("default_customer")
                        .email("customer@badminton.com")
                        .password("$2a$10$qV9.f7h.P7tJ3nN9XwQOpeLg3r6C.j3v.y83GZ7l2v7/92b7oE5.K") // BCrypt for "password"
                        .role("CUSTOMER")
                        .active(true)
                        .build()
                ));

        // 4. Create and save new booking
        Booking booking = Booking.builder()
                .customer(customer)
                .court(court)
                .timeSlot(timeSlot)
                .bookingDate(request.getBookingDate())
                .status("PENDING")
                .build();

        Booking savedBooking = bookingRepository.save(booking);

        return BookingDTO.builder()
                .id(savedBooking.getId())
                .customerId(savedBooking.getCustomer().getId())
                .courtId(savedBooking.getCourt().getId())
                .timeSlotId(savedBooking.getTimeSlot().getId())
                .bookingDate(savedBooking.getBookingDate())
                .status(savedBooking.getStatus())
                .createdAt(savedBooking.getCreatedAt())
                .build();
    }
}
