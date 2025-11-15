package com.example.courtreserve.service;

import com.example.courtreserve.dto.*;
import java.util.List;

public interface CourtService {

    AddCourtResponse addCourt(Long vendorId, AddCourtRequest request);

    GetSingleCourtResponse getCourtById(Long Id);

    GetSingleCourtVendorResponse getVendorCourtById(Long Id);

    List<GetPopularCourts> getCourtsOfVendor(Long Id);
}
