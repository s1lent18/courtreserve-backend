package com.example.courtreserve.service;


import com.example.courtreserve.dto.AddVendorRequest;
import com.example.courtreserve.dto.AddVendorResponse;
import com.example.courtreserve.dto.LoginVendorResponse;

public interface VendorService {

    LoginVendorResponse getVendor(String email);

    AddVendorResponse addNewVendor(AddVendorRequest addVendorRequest);
}
