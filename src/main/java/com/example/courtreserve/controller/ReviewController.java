package com.example.courtreserve.controller;

import com.example.courtreserve.dto.AddReviewRequest;
import com.example.courtreserve.dto.UpdateReviewRequest;
import com.example.courtreserve.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(
        @RequestBody AddReviewRequest request
    ) {
        var review = reviewService.addReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Review given successfully", "review", review));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateReview(
        @PathVariable Long id,
        @RequestBody UpdateReviewRequest request
    ) {
        var review = reviewService.updateReview(id, request);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Review updated successfully", "review", review));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(
        @PathVariable Long id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.status(HttpStatus.OK)
            .body(Map.of("message", "Review deleted successfully"));
    }
}