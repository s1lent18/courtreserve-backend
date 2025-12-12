package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Booking;
import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.BookingRepository;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.exception.ForbiddenException;
import com.example.courtreserve.exception.ForeignKeyConstraintException;
import com.example.courtreserve.exception.ResourceNotFoundException;
import com.example.courtreserve.service.CourtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
            User vendor = userRepository.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", vendorId));

            boolean isVendor = vendor.getRoles().stream()
                .anyMatch(role -> role.getName().equalsIgnoreCase("VENDOR"));
            if (!isVendor) {
                    throw new ForbiddenException("User is not authorized to add courts");
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
                savedCourt.getType());
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

        public GetSingleCourtVendorResponse getVendorCourtById(Long id) {
            Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Court", "id", id));

            int size = (int) court.getBookings().stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .count();

            int sales = court.getBookings().stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .mapToInt(Booking::getPrice)
                .sum();

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
                size,
                sales
            );
        }

        public List<GetPopularCourts> getCourtsOfVendor(Long Id) {
                return courtRepository.findAllOfSingleVendor(Id);
        }

        @Override
        public PaginatedResponse<GetPopularCourts> getPopularCourts(String location, Pageable pageable) {
            Page<GetPopularCourts> courtsPage = courtRepository.findCourtStatsByCount(location, pageable);

            return new PaginatedResponse<>(
                courtsPage.getNumber(),
                courtsPage.getSize(),
                courtsPage.getTotalPages(),
                courtsPage.getTotalElements(),
                courtsPage.getContent()
            );
        }

        @Override
        public String removeCourt(Long Id) {
            Court court = courtRepository.findById(Id)
                            .orElseThrow(() -> new ResourceNotFoundException("Court", "id", Id));

            List<String> dependencies = new ArrayList<>();

            if (bookingRepository.existsByFacility_Id(Id)) {
                    dependencies.add("Bookings");
            }

            if (!dependencies.isEmpty()) {
                    throw new ForeignKeyConstraintException("Court", Id, dependencies);
            }

            courtRepository.delete(court);
            return "Court deleted successfully";
        }

        @Override
        public PaginatedResponse<GetPopularCourts> searchCourts(String query, Pageable pageable) {
                Page<Court> courtsPage = courtRepository.
                    findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(query, query, pageable);

                List<GetPopularCourts> courtsList = courtsPage.getContent().stream()
                    .map(court -> courtRepository.findByPopularId(court.getId()))
                    .toList();

                return new PaginatedResponse<>(
                    courtsPage.getNumber(),
                    courtsPage.getSize(),
                    courtsPage.getTotalPages(),
                    courtsPage.getTotalElements(),
                    courtsList
                );
        }
}
