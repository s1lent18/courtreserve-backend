package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Booking;
import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.BookingRepository;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.AddBookingRequest;
import com.example.courtreserve.dto.AddBookingResponse;
import com.example.courtreserve.dto.BookingResponse;
import com.example.courtreserve.dto.GetBookingResponse;
import com.example.courtreserve.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CourtRepository courtRepository;

    public BookingServiceImpl(UserRepository userRepository, BookingRepository bookingRepository, CourtRepository courtRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.courtRepository = courtRepository;
    }

    @Cacheable(value = "bookings", key = "#userId")
    public List<GetBookingResponse> getAllBookings (Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User Not Found"));

        List<Booking> bookings = bookingRepository.findAllByUser_Id(userId);
        List<GetBookingResponse> getBookingResponses = new ArrayList<>();

        for (Booking booking : bookings) {
            GetBookingResponse getBookingResponse = new GetBookingResponse(
                    booking.getId(),
                    booking.getFacility().getId(),
                    booking.getFacility().getName(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    booking.getStatus(),
                    booking.getPrice(),
                    booking.getAdvance(),
                    booking.getToBePaid(),
                    booking.getCreated()
            );

            getBookingResponses.add(getBookingResponse);
        }

        return getBookingResponses;
    }

    @CacheEvict(value = "bookings", key = "#request.userId")
    public AddBookingResponse createBooking(AddBookingRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User Not Found"));
        Court court = courtRepository.findById(request.getFacilityId()).orElseThrow(() -> new RuntimeException("Court Not Found"));

        Booking newBooking = Booking.builder()
                .user(user)
                .facility(court)
                .created(LocalDateTime.now())
                .advance(request.getAdvance())
                .price(request.getPrice())
                .status("PENDING")
                .toBePaid(request.getPrice() - request.getAdvance())
                .endTime(request.getEndTime())
                .startTime(request.getStartTime())
                .build();

        Booking savedBooking = bookingRepository.save(newBooking);

        return new AddBookingResponse(
                savedBooking.getId(),
                savedBooking.getUser().getId(),
                savedBooking.getUser().getName(),
                savedBooking.getFacility().getId(),
                savedBooking.getFacility().getName(),
                savedBooking.getStartTime(),
                savedBooking.getEndTime(),
                savedBooking.getStatus(),
                savedBooking.getPrice(),
                savedBooking.getAdvance(),
                savedBooking.getToBePaid(),
                savedBooking.getCreated()
        );
    }

    public List<BookingResponse> getPendingBooking(Long vendorId) {
        userRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor Not Found"));

        List<Long> courtIds = courtRepository.findCourtIdsByVendorId(vendorId);

        List<Booking> bookings = bookingRepository.findPendingBookings(courtIds);

        List<BookingResponse> bookingResponses = new ArrayList<>();

        for (Booking booking : bookings) {
            BookingResponse temp = new BookingResponse(
                    booking.getId(),
                    booking.getUser().getId(),
                    booking.getUser().getName(),
                    booking.getFacility().getId(),
                    booking.getFacility().getName(),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    booking.getStatus(),
                    booking.getPrice(),
                    booking.getAdvance(),
                    booking.getToBePaid(),
                    booking.getCreated()
            );
            bookingResponses.add(temp);
        }

        return bookingResponses;
    }

    @CacheEvict(value = "bookings", key = "#result.user.id")
    public BookingResponse confirmBooking(Long bookingId) {
        Booking pendingBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking Not Found"));

        LocalDateTime now = LocalDateTime.now();

        if (pendingBooking.getStartTime().isAfter(now)) {
            pendingBooking.setStatus("CONFIRMED");
        } else {
            pendingBooking.setStatus("EXPIRED");
        }

        Booking response = bookingRepository.save(pendingBooking);

        return new BookingResponse(
                response.getId(),
                response.getUser().getId(),
                response.getUser().getName(),
                response.getFacility().getId(),
                response.getFacility().getName(),
                response.getStartTime(),
                response.getEndTime(),
                response.getStatus(),
                response.getPrice(),
                response.getAdvance(),
                response.getToBePaid(),
                response.getCreated()
        );
    }

    @CacheEvict(value = "bookings", key = "#result.user.id")
    public BookingResponse rejectBooking(Long bookingId) {
        Booking pendingBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking Not Found"));

        pendingBooking.setStatus("REJECTED");

        Booking response = bookingRepository.save(pendingBooking);

        return new BookingResponse(
                response.getId(),
                response.getUser().getId(),
                response.getUser().getName(),
                response.getFacility().getId(),
                response.getFacility().getName(),
                response.getStartTime(),
                response.getEndTime(),
                response.getStatus(),
                response.getPrice(),
                response.getAdvance(),
                response.getToBePaid(),
                response.getCreated()
        );
    }

    @CacheEvict(value = "bookings", key = "#result.user.id")
    public BookingResponse cancelBooking(Long bookingId) {
        Booking pendingBooking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking Not Found"));

        pendingBooking.setStatus("CANCELED");

        Booking response = bookingRepository.save(pendingBooking);

        return new BookingResponse(
                response.getId(),
                response.getUser().getId(),
                response.getUser().getName(),
                response.getFacility().getId(),
                response.getFacility().getName(),
                response.getStartTime(),
                response.getEndTime(),
                response.getStatus(),
                response.getPrice(),
                response.getAdvance(),
                response.getToBePaid(),
                response.getCreated()
        );
    }

    @Override
    public void deleteUserBooking(Long Id, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking Not Found"));

        userRepository.findById(Id).orElseThrow(() -> new RuntimeException("User Not Found"));

        if (!Id.equals(booking.getUser().getId())) {
            throw new RuntimeException("Booking doesn't belong to user");
        }

        booking.setDeletedByUser(true);
        booking.setDeletedAtUser(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    @Override
    public void deleteVendorBooking(Long Id, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new RuntimeException("Booking Not Found"));

        userRepository.findById(Id).orElseThrow(() -> new RuntimeException("Vendor Not Found"));

        if (!Id.equals(booking.getFacility().getVendor().getId())) {
            throw new RuntimeException("Booking doesn't belong to vendor");
        }

        booking.setDeletedByVendor(true);
        booking.setDeletedAtVendor(LocalDateTime.now());
        bookingRepository.save(booking);
    }

    @CacheEvict(value = "bookings", allEntries = true)
    @Scheduled(cron = "0 0 0 * * *")
    public void checkExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> expiredBookings = bookingRepository.findAllByStartTimeBefore(now);

        for (Booking booking : expiredBookings) {
            booking.setStatus("EXPIRED");
        }

        bookingRepository.saveAll(expiredBookings);
    }
}
