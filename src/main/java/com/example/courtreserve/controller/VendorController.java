package com.example.courtreserve.controller;

import com.example.courtreserve.JWT.JwtUtil;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @RequestBody AddVendorRequest request) {
        Map<String, AddVendorResponse> response = new HashMap<>();
        AddVendorResponse addVendorResponse = vendorService.addNewVendor(request);
        response.put("registerVendorData", addVendorResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, LoginVendorResponse>> loginVendor(
            @RequestBody LoginVendorRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        LoginVendorResponse req = vendorService.getVendor(request.getEmail());
        req.setToken(jwtUtil.generateToken(userDetails));

        Map<String, LoginVendorResponse> response = new HashMap<>();
        response.put("vendorData", req);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, LoginVendorResponse>> updateVendor(
            @PathVariable Long id,
            @RequestBody UpdateVendorRequest request) {
        LoginVendorResponse updatedVendor = vendorService.updateVendor(id, request);
        Map<String, LoginVendorResponse> response = new HashMap<>();
        response.put("vendorData", updatedVendor);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteVendor(
            @PathVariable Long id) {
        vendorService.deleteVendor(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Vendor deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
