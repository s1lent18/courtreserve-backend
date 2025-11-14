package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Booking;
import com.example.courtreserve.dto.AddBookingRequest;
import com.example.courtreserve.dto.AddBookingResponse;
import com.example.courtreserve.dto.GetBookingResponse;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;

public interface BookingService {

    List<GetBookingResponse> getAllBookings (Long userId);

    AddBookingResponse createBooking(AddBookingRequest request);

    List<Booking> getPendingBooking(Long vendorId);

    Booking confirmBooking(Long bookingId);

    Booking rejectBooking(Long bookingId);

    Booking cancelBooking(Long bookingId);

    @Scheduled(cron = "0 0 0 * * *")
    void checkExpiredBookings();
}