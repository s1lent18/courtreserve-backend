package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.Review;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.ReviewRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.AddReviewRequest;
import com.example.courtreserve.dto.AddReviewResponse;
import com.example.courtreserve.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CourtRepository courtRepository;

    public AddReviewResponse addReview(AddReviewRequest request) {
        User user = userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not Found"));
        Court court = courtRepository.findById(request.getFacilityId()).orElseThrow(() -> new RuntimeException("Court Not Found"));

        Review newReview = Review.builder()
                .facility(court)
                .user(user)
                .rating(request.getRating())
                .review(request.getReview())
                .created(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(newReview);

        return new AddReviewResponse(
                savedReview.getId(),
                savedReview.getUser().getId(),
                savedReview.getUser().getName(),
                savedReview.getFacility().getId(),
                savedReview.getFacility().getName(),
                savedReview.getRating(),
                savedReview.getReview(),
                savedReview.getCreated()
        );
    }
}