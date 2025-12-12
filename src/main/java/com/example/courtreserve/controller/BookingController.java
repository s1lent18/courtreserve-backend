package com.example.courtreserve.controller;

import com.example.courtreserve.dto.AddBookingRequest;
import com.example.courtreserve.service.BookingService;
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

        @PostMapping
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<?> createBooking(
            @RequestBody AddBookingRequest request
        ) {
            var booking = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Booking created successfully", "booking", booking));
        }

        @GetMapping
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<?> getAllBookings(
            @RequestParam Long id
        ) {
            var getBookings = bookingService.getAllBookings(id);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Bookings returned successfully", "bookings", getBookings));
        }

        @PatchMapping("/{bookingId}/cancel")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId
        ) {
            var canceledBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Booking Canceled", "canceledBooking", canceledBooking));
        }

        @GetMapping("/pending")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> getPendingBookings(
            @RequestParam Long id
        ) {
            var pendingBookings = bookingService.getPendingBooking(id);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Pending Bookings returned", "pendingBookings", pendingBookings));
        }

        @PatchMapping("/{bookingId}/confirm")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> confirmBooking(
            @PathVariable Long bookingId
        ) {
            var confirmedBooking = bookingService.confirmBooking(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Booking Confirmed", "confirmedBooking", confirmedBooking));
        }

        @PatchMapping("/{bookingId}/reject")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> rejectBooking(
            @PathVariable Long bookingId
        ) {
            var rejectedBooking = bookingService.rejectBooking(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Booking Rejected", "rejectedBooking", rejectedBooking));
        }

        @DeleteMapping("/user/{userId}/{bookingId}")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> deleteUserBooking(
            @PathVariable Long userId,
            @PathVariable Long bookingId
        ) {
            bookingService.deleteUserBooking(userId, bookingId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Booking deleted successfully"));
        }

        @DeleteMapping("/vendor/{vendorId}/{bookingId}")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> deleteVendorBooking(
            @PathVariable Long vendorId,
            @PathVariable Long bookingId
        ) {
            bookingService.deleteVendorBooking(vendorId, bookingId);
            return ResponseEntity.status(HttpStatus.OK).body(Map.of("message", "Booking deleted successfully"));
        }
}
