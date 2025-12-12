package com.example.courtreserve.service;

import com.example.courtreserve.dto.AddVendorRequest;
import com.example.courtreserve.dto.AddVendorResponse;
import com.example.courtreserve.dto.LoginVendorResponse;
import com.example.courtreserve.dto.UpdateVendorRequest;

public interface VendorService {

    LoginVendorResponse getVendor(String email);

    AddVendorResponse addNewVendor(AddVendorRequest addVendorRequest);

    LoginVendorResponse updateVendor(Long id, UpdateVendorRequest request);

    void deleteVendor(Long id);
}
