package com.example.courtreserve.controller;

import com.example.courtreserve.JWT.JwtUtil;
import com.example.courtreserve.service.CourtService;
import com.example.courtreserve.service.VendorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/vendor")
public class VendorController {

    @Autowired
    private VendorService vendorService;

    @Autowired
    private CourtService courtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, VendorService.AddVendorResponse>> registerVendor(
            @RequestBody VendorService.AddVendorRequest request
    ) {
        try {
            Map<String, VendorService.AddVendorResponse> response = new HashMap<>();

            VendorService.AddVendorResponse addUserResponse = vendorService.addNewVendor(request);

            response.put("registerVendorData", addUserResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, VendorService.LoginVendorResponse>> loginUser(
            @RequestBody VendorService.LoginVendorRequest request
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            VendorService.LoginVendorResponse req = vendorService.getVendor(request.getEmail());

            req.setToken(jwtUtil.generateToken(userDetails));

            Map<String, VendorService.LoginVendorResponse> response = new HashMap<>();

            response.put("vendorData", req);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{vendorId}/addCourt")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> registerCourt(
        @PathVariable Long vendorId,
        @RequestBody CourtService.AddCourtRequest request
    ) {
        try {
            var court = courtService.addCourt(vendorId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Court added successfully", "court", court));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getVendorCourts")
    public ResponseEntity<?> getCourtsByVendor(
            @RequestParam Long id
    ) {
        try {
            var court = courtService.getCourtsOfVendor(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Courts Found Successfully", "court", court));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}