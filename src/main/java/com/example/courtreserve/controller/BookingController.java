package com.example.courtreserve.controller;

import com.example.courtreserve.dto.AddBookingRequest;
import com.example.courtreserve.service.BookingService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/booking")
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/createBooking")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createBooking(
            @RequestBody AddBookingRequest request
    ) {
        try {
            var booking = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Booking created successfully", "booking", booking));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getAllBookings")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllBookings(
            @RequestParam Long id
    ) {
        try {
            var getBookings = bookingService.getAllBookings(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Bookings returned successfully", "bookings", getBookings));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{bookingId}/cancelBooking")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId
    ) {
        try {
            var canceledBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Booking Canceled", "canceledBooking", canceledBooking));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getPendingBookings")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getPendingBookings(
            @RequestParam Long id
    ) {
        try {
            var pendingBookings = bookingService.getPendingBooking(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Pending Bookings returned", "pendingBookings", pendingBookings));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{bookingId}/confirmBooking")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> confirmBooking(
            @PathVariable Long bookingId
    ) {
        try {
            var confirmedBooking = bookingService.confirmBooking(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Booking Confirmed", "confirmedBooking", confirmedBooking));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{bookingId}/rejectBooking")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> rejectBooking(
            @PathVariable Long bookingId
    ) {
        try {
            var rejectedBooking = bookingService.rejectBooking(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Booking Rejected", "rejectedBooking", rejectedBooking));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}