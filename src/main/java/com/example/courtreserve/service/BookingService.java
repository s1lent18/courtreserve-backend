package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Booking;
import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.BookingRepository;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class BookingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CourtRepository courtRepository;

    public BookingService(UserRepository userRepository, BookingRepository bookingRepository, CourtRepository courtRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.courtRepository = courtRepository;
    }

    @Setter @Getter
    @AllArgsConstructor
    public static class AddBookingRequest {
        Long userId;
        Long facilityId;
        LocalDateTime startTime;
        LocalDateTime endTime;
        Integer price;
        Integer advance;
        Integer toBePaid;
    }

    @Setter @Getter
    @AllArgsConstructor
    public static class AddBookingResponse {
        Long id;
        User user;
        Court court;
        LocalDateTime startTime;
        LocalDateTime endTime;
        String status;
        Integer price;
        Integer advance;
        Integer toBePaid;
        LocalDateTime created;
    }

    public AddBookingResponse createBooking(AddBookingRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User Not Found"));
        Court court = courtRepository.findById(request.getFacilityId()).orElseThrow(() -> new RuntimeException("Court Not Found"));

        Booking newBooking = Booking.builder()
            .user(user)
            .facility(court)
            .created(LocalDateTime.now())
            .advance(request.getAdvance())
            .price(request.getPrice())
            .toBePaid(request.getToBePaid())
            .endTime(request.getEndTime())
            .startTime(request.getStartTime())
            .build();

        Booking savedBooking = bookingRepository.save(newBooking);

        return new AddBookingResponse(
            savedBooking.getId(),
            savedBooking.getUser(),
            savedBooking.getFacility(),
            savedBooking.getStartTime(),
            savedBooking.getEndTime(),
            savedBooking.getStatus(),
            savedBooking.getPrice(),
            savedBooking.getAdvance(),
            savedBooking.getToBePaid(),
            savedBooking.getCreated()
        );
    }
}
