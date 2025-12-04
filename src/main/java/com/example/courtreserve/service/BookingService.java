package com.example.courtreserve.service;

import com.example.courtreserve.dto.AddBookingRequest;
import com.example.courtreserve.dto.AddBookingResponse;
import com.example.courtreserve.dto.BookingResponse;
import com.example.courtreserve.dto.GetBookingResponse;
import org.springframework.scheduling.annotation.Scheduled;
import java.util.List;

public interface BookingService {

    List<GetBookingResponse> getAllBookings (Long userId);

    AddBookingResponse createBooking(AddBookingRequest request);

    List<BookingResponse> getPendingBooking(Long vendorId);

    BookingResponse confirmBooking(Long bookingId);

    BookingResponse rejectBooking(Long bookingId);

    BookingResponse cancelBooking(Long bookingId);

    void deleteUserBooking(Long Id, Long bookingId);

    void deleteVendorBooking(Long Id, Long bookingId);

    @Scheduled(cron = "0 0 0 * * *")
    void checkExpiredBookings();
}