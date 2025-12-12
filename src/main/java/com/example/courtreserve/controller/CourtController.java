package com.example.courtreserve.controller;

import com.example.courtreserve.dto.AddCourtRequest;
import com.example.courtreserve.dto.GetPopularCourts;
import com.example.courtreserve.dto.PaginatedResponse;
import com.example.courtreserve.service.CourtService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/court")
public class CourtController {

        @Autowired
        private final CourtService courtService;

        public CourtController(CourtService courtService) {
                this.courtService = courtService;
        }

        @GetMapping("/popular")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<?> getPopularCourts(
            @RequestParam String location,
            @Parameter(hidden = true) Pageable pageable
        ) {
            PaginatedResponse<GetPopularCourts> courts = courtService.getPopularCourts(location, pageable);

            return ResponseEntity.ok(
            Map.of(
                "message", "Popular Courts Retrieved",
                "page", courts.getPage(),
                "size", courts.getSize(),
                "totalPages", courts.getTotalPages(),
                "totalElements", courts.getTotalElements(),
                "content", courts.getContent())
            );
        }

        @GetMapping("/{id}")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<?> getCourtById(
            @PathVariable Long id
        ) {
            var court = courtService.getCourtById(id);
            return ResponseEntity.status(HttpStatus.OK)
                            .body(Map.of("message", "Court Found Successfully", "court", court));
        }

        @PostMapping("/vendor/{vendorId}")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> registerCourt(
            @PathVariable Long vendorId,
            @RequestBody AddCourtRequest request
        ) {
            var court = courtService.addCourt(vendorId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Court added successfully", "court", court));
        }

        @GetMapping("/vendor/{id}")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> getCourtsByVendor(
            @PathVariable Long id
        ) {
            var court = courtService.getCourtsOfVendor(id);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Courts Found Successfully", "court", court));
        }

        @GetMapping("/vendor/single/{id}")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> getSingleCourt(
            @PathVariable Long id
        ) {
            var singleCourt = courtService.getVendorCourtById(id);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Single Court Returned", "court", singleCourt));
        }

        @GetMapping("/search")
        @PreAuthorize("hasRole('USER')")
        public ResponseEntity<?> searchCourts(
            @RequestParam String q,
            @Parameter(hidden = true) Pageable pageable
        ) {
            PaginatedResponse<GetPopularCourts> courts = courtService.searchCourts(q, pageable);

            return ResponseEntity.ok(Map.of(
                "message", "Search Results",
                "page", courts.getPage(),
                "size", courts.getSize(),
                "totalPages", courts.getTotalPages(),
                "totalElements", courts.getTotalElements(),
                "content", courts.getContent()));
        }

        @DeleteMapping("/{id}")
        @PreAuthorize("hasRole('VENDOR')")
        public ResponseEntity<?> deleteCourt(
            @PathVariable Long id
        ) {
            String message = courtService.removeCourt(id);
            return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", message));
        }
}
