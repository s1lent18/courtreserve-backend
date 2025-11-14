package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Booking;
import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.BookingRepository;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface CourtService {

    AddCourtResponse addCourt(Long vendorId, AddCourtRequest request);

    GetSingleCourtResponse getCourtById(Long Id);

    GetSingleCourtVendorResponse getVendorCourtById(Long Id);

    List<GetPopularCourts> getCourtsOfVendor(Long Id);
}
