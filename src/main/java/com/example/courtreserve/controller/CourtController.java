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

    @GetMapping("/getPopularCourts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getPopularCourts(
            @RequestParam String location,
            @Parameter(hidden = true) Pageable pageable
    ) {
        PaginatedResponse<GetPopularCourts> courts = courtService.getPopularCourts(location, pageable);

        return ResponseEntity.ok(Map.of(
                "message", "Popular Courts Retrieved",
                "page", courts.getPage(),
                "size", courts.getSize(),
                "totalPages", courts.getTotalPages(),
                "totalElements", courts.getTotalElements(),
                "content", courts.getContent()
        ));
    }

    @GetMapping("/getCourt")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getCourtById(
            @RequestParam Long id
    ) {
        var court = courtService.getCourtById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Court Found Successfully", "court", court));
    }

    @PostMapping("/{vendorId}/addCourt")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> registerCourt(
            @PathVariable Long vendorId,
            @RequestBody AddCourtRequest request
    ) {
        var court = courtService.addCourt(vendorId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Court added successfully", "court", court));
    }

    @GetMapping("/getVendorCourts")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getCourtsByVendor(
            @RequestParam Long id
    ) {
        var court = courtService.getCourtsOfVendor(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Courts Found Successfully", "court", court));
    }

    @GetMapping("/getSingleCourt")
    @PreAuthorize("hasRole('VENDOR')")
    public ResponseEntity<?> getSingleCourt(
            @RequestParam Long id
    ) {
        var singleCourt = courtService.getVendorCourtById(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Single Court Returned", "court", singleCourt));
    }
}
