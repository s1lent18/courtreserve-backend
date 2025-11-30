package com.example.courtreserve.controller;

import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.dto.AddCourtRequest;
import com.example.courtreserve.dto.GetPopularCourts;
import com.example.courtreserve.service.CourtService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/court")
public class CourtController {

    @Autowired
    private final CourtService courtService;

    @Autowired
    private final CourtRepository courtRepository;

    public CourtController(CourtService courtService, CourtRepository courtRepository) {
        this.courtService = courtService;
        this.courtRepository = courtRepository;
    }

    @GetMapping("/getPopularCourts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Object>> getPopularCourts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String location
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GetPopularCourts> courtsPage = courtRepository.findCourtStatsByCount(location, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("courts", courtsPage.getContent());
        response.put("currentPage", courtsPage.getNumber());
        response.put("totalItems", courtsPage.getTotalElements());
        response.put("totalPages", courtsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getCourt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCourtById(
            @RequestParam Long id
    ) {
        try {
            var court = courtService.getCourtById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Court Found Successfully", "court", court));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{vendorId}/addCourt")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> registerCourt(
            @PathVariable Long vendorId,
            @RequestBody AddCourtRequest request
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
    @PreAuthorize("hasRole('VENDOR')")
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

    @GetMapping("/getSingleCourt")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getSingleCourt(
            @RequestParam Long id
    ) {
        try {
            var singleCourt = courtService.getVendorCourtById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Single Court Returned", "court", singleCourt));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}