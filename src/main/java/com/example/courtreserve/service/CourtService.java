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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CourtService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourtRepository courtRepository;

    @Autowired
    BookingRepository bookingRepository;

    @Getter @Setter
    @AllArgsConstructor
    public static class AddCourtRequest {
        private String name;
        private String description;
        private String location;
        private Integer price;
        private String openTime;
        private String closeTime;
        private String type;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class AddCourtResponse {
        private Long id;
        private String name;
        private String description;
        private String location;
        private Integer price;
        private String openTime;
        private String closeTime;
        private String type;
    }

    public interface GetPopularCourts {
        Long getId();
        String getName();
        String getDescription();
        String getLocation();
        String getType();
        Double getPrice();
        String getOpen();
        String getClose();
        Long getVendorId();
        Long getBookingCount();
        Double getAvgRating();
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class GetSingleCourtResponse {
        Long id;
        String name;
        String description;
        String location;
        String type;
        Double price;
        String open;
        String close;
        Long vendorId;
        Long bookingCount;
        Double avgRating;
        List<BookingService.BookingTimeProjection> bookedTimes;
    }

    @Getter @Setter @AllArgsConstructor
    public static class GetSingleCourtVendorResponse {
        Long id;
        LocalTime closeTime;
        LocalDateTime createdAt;
        String description;
        String location;
        String name;
        LocalTime openTime;
        Integer price;
        String type;
        List<Booking> bookings;
    }

    public AddCourtResponse addCourt(Long vendorId, AddCourtRequest request) {
        User vendor = userRepository.findById(vendorId).orElseThrow(() -> new RuntimeException("Vendor Not Found"));

        boolean isVendor = vendor.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VENDOR"));
        if (!isVendor) {
            throw new RuntimeException("not authorized");
        }

        Court court = Court.builder()
            .vendor(vendor)
            .name(request.getName())
            .description(request.getDescription())
            .location(request.getLocation())
            .price(request.getPrice())
            .openTime(LocalTime.parse(request.getOpenTime()))
            .closeTime(LocalTime.parse(request.getCloseTime()))
            .type(request.getType())
            .created(LocalDateTime.now())
            .build();

        Court savedCourt = courtRepository.save(court);

        return new AddCourtResponse(
            savedCourt.getId(),
            savedCourt.getName(),
            savedCourt.getDescription(),
            savedCourt.getLocation(),
            savedCourt.getPrice(),
            savedCourt.getOpenTime().toString(),
            savedCourt.getCloseTime().toString(),
            savedCourt.getType()
        );
    }

    public GetSingleCourtResponse getCourtById(Long Id) {
        GetPopularCourts courtInfo = courtRepository.findByPopularId(Id);

        List<BookingService.BookingTimeProjection> reservedTimes = bookingRepository.findBookingTimesByCourtId(Id);

        return new GetSingleCourtResponse(
                courtInfo.getId(),
                courtInfo.getName(),
                courtInfo.getDescription(),
                courtInfo.getLocation(),
                courtInfo.getType(),
                courtInfo.getPrice(),
                courtInfo.getOpen(),
                courtInfo.getClose(),
                courtInfo.getVendorId(),
                courtInfo.getBookingCount(),
                courtInfo.getAvgRating(),
                reservedTimes
        );
    }

    public GetSingleCourtVendorResponse getVendorCourtById(Long Id) {
        Court court = courtRepository.findById(Id).orElseThrow(() -> new RuntimeException("Court Not Found"));

        return new GetSingleCourtVendorResponse(
                court.getId(),
                court.getCloseTime(),
                court.getCreated(),
                court.getDescription(),
                court.getLocation(),
                court.getName(),
                court.getOpenTime(),
                court.getPrice(),
                court.getType(),
                court.getBookings()
        );
    }

    public List<GetPopularCourts> getCourtsOfVendor(Long Id) {
        return courtRepository.findAllOfSingleVendor(Id);
    }
}
