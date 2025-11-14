package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.BookingRepository;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.CourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class CourtServiceImpl implements CourtService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourtRepository courtRepository;

    @Autowired
    BookingRepository bookingRepository;

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

        List<BookingTimeProjection> reservedTimes = bookingRepository.findBookingTimesByCourtId(Id);

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
