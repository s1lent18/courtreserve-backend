package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class CourtService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourtRepository courtRepository;

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

    @Getter @Setter
    @AllArgsConstructor
    public static class GetPopularCourts {
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
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class GetSingleCourt {
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

    public GetSingleCourt getCourtById(Long courtId) {
        Court court = courtRepository.findById(courtId)
                .orElseThrow(() -> new RuntimeException("Court not found with id: " + courtId));

        // Get booking count and average rating using the same logic as popular courts
        Long bookingCount = courtRepository.countBookingsByCourtId(courtId);
        Double avgRating = courtRepository.getAverageRatingByCourtId(courtId);

        return new GetSingleCourt(
            court.getId(),
            court.getName(),
            court.getDescription(),
            court.getLocation(),
            court.getType(),
            court.getPrice().doubleValue(),
            court.getOpenTime().toString(),
            court.getCloseTime().toString(),
            court.getVendor().getId(),
            bookingCount != null ? bookingCount : 0L,
            avgRating != null ? avgRating : 0.0
        );
    }
}
