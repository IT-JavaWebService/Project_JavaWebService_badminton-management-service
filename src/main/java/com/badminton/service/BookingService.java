package com.badminton.service;

import com.badminton.dto.BookingDTO;
import com.badminton.dto.request.BookingRequest;

public interface BookingService {
    BookingDTO createBooking(BookingRequest request);
}
