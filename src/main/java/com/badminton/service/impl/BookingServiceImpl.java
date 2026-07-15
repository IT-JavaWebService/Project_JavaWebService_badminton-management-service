package com.badminton.service.impl;

import com.badminton.dto.BookingDTO;
import com.badminton.dto.request.BookingRequest;
import com.badminton.dto.response.CustomerBookingHistoryDTO;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
        Court court = courtRepository.findById(request.getCourtId())
                .orElseThrow(() -> new CustomException("Court not found", HttpStatus.NOT_FOUND));

        TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
                .orElseThrow(() -> new CustomException("Time slot not found", HttpStatus.NOT_FOUND));

        boolean hasConflict = bookingRepository.existsByCourtIdAndBookingDateAndTimeSlotIdAndStatusIn(
                request.getCourtId(),
                request.getBookingDate(),
                request.getTimeSlotId(),
                List.of("PENDING", "CONFIRMED")
        );
        if (hasConflict) {
            throw new CustomException("This time slot is already booked on the selected date", HttpStatus.CONFLICT);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

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

    @Override
    @Transactional(readOnly = true)
    public List<CustomerBookingHistoryDTO> getCustomerBookingHistory(String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        List<Booking> bookings = bookingRepository.findByCustomerId(customer.getId());

        return bookings.stream()
                .map(b -> CustomerBookingHistoryDTO.builder()
                        .bookingId(b.getId())
                        .courtName(b.getCourt().getName())
                        .bookingDate(b.getBookingDate())
                        .timeSlot(b.getTimeSlot().getLabel())
                        .status(b.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void approveBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException("Booking not found", HttpStatus.NOT_FOUND));
        
        if (!"PENDING".equals(booking.getStatus())) {
            throw new CustomException("Booking status must be PENDING to approve", HttpStatus.BAD_REQUEST);
        }
        
        String managerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (booking.getCourt().getManager() == null || !booking.getCourt().getManager().getEmail().equals(managerEmail)) {
            throw new CustomException("You do not have permission to approve/reject bookings for this court", HttpStatus.FORBIDDEN);
        }
        
        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void rejectBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new CustomException("Booking not found", HttpStatus.NOT_FOUND));
        
        if (!"PENDING".equals(booking.getStatus())) {
            throw new CustomException("Booking status must be PENDING to reject", HttpStatus.BAD_REQUEST);
        }
        
        String managerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        if (booking.getCourt().getManager() == null || !booking.getCourt().getManager().getEmail().equals(managerEmail)) {
            throw new CustomException("You do not have permission to approve/reject bookings for this court", HttpStatus.FORBIDDEN);
        }
        
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getMonthlyRevenue(int year, int month, String managerEmail) {
        List<Booking> bookings = bookingRepository.findAll();

        return bookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .filter(b -> b.getBookingDate().getYear() == year && b.getBookingDate().getMonthValue() == month)
                .filter(b -> managerEmail == null || (b.getCourt().getManager() != null && b.getCourt().getManager().getEmail().equals(managerEmail)))
                .mapToDouble(b -> b.getCourt().getPricePerHour())
                .sum();
    }
}
