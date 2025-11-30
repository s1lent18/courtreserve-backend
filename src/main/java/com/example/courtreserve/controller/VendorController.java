package com.example.courtreserve.controller;

import com.example.courtreserve.JWT.JwtUtil;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.BookingService;
import com.example.courtreserve.service.CourtService;
import com.example.courtreserve.service.TournamentService;
import com.example.courtreserve.service.VendorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private VendorService vendorService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, AddVendorResponse>> registerVendor(
            @RequestBody AddVendorRequest request
    ) {
        try {
            Map<String, AddVendorResponse> response = new HashMap<>();

            AddVendorResponse addUserResponse = vendorService.addNewVendor(request);

            response.put("registerVendorData", addUserResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, LoginVendorResponse>> loginUser(
            @RequestBody LoginVendorRequest request
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            LoginVendorResponse req = vendorService.getVendor(request.getEmail());

            req.setToken(jwtUtil.generateToken(userDetails));

            Map<String, LoginVendorResponse> response = new HashMap<>();

            response.put("vendorData", req);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}