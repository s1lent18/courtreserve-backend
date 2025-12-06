package com.example.courtreserve.controller;

import com.example.courtreserve.dto.AddReviewRequest;
import com.example.courtreserve.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/createReview")
    public ResponseEntity<?> createReview(
            @RequestBody AddReviewRequest request
    ) {
        var review = reviewService.addReview(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Review given successfully", "review", review));
    }
}
