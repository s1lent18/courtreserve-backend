package com.example.courtreserve.service;

import com.example.courtreserve.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourtService {

    AddCourtResponse addCourt(Long vendorId, AddCourtRequest request);

    GetSingleCourtResponse getCourtById(Long Id);

    GetSingleCourtVendorResponse getVendorCourtById(Long Id);

    List<GetPopularCourts> getCourtsOfVendor(Long Id);

    PaginatedResponse<GetPopularCourts> getPopularCourts(String location, Pageable pageable);

    String removeCourt(Long Id);

    PaginatedResponse<GetPopularCourts> searchCourts(String query, Pageable pageable);
}
