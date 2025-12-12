package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.Review;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.ReviewRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.AddReviewRequest;
import com.example.courtreserve.dto.AddReviewResponse;
import com.example.courtreserve.dto.UpdateReviewRequest;
import com.example.courtreserve.exception.ResourceNotFoundException;
import com.example.courtreserve.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class ReviewServiceImpl implements ReviewService {

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private ReviewRepository reviewRepository;

        @Autowired
        private CourtRepository courtRepository;

        @Transactional
        public AddReviewResponse addReview(AddReviewRequest request) {
                User user = userRepository.findById(request.getUserId())
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));
                Court court = courtRepository.findById(request.getFacilityId())
                                .orElseThrow(() -> new ResourceNotFoundException("Court", "id",
                                                request.getFacilityId()));

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
                                savedReview.getCreated());
        }

        @Override
        @Transactional
        public AddReviewResponse updateReview(Long id, UpdateReviewRequest request) {
                Review review = reviewRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

                // Update only non-null fields
                if (request.getRating() != null) {
                        review.setRating(request.getRating());
                }
                if (request.getReview() != null) {
                        review.setReview(request.getReview());
                }

                Review updatedReview = reviewRepository.save(review);

                return new AddReviewResponse(
                                updatedReview.getId(),
                                updatedReview.getUser().getId(),
                                updatedReview.getUser().getName(),
                                updatedReview.getFacility().getId(),
                                updatedReview.getFacility().getName(),
                                updatedReview.getRating(),
                                updatedReview.getReview(),
                                updatedReview.getCreated());
        }

        @Override
        @Transactional
        public void deleteReview(Long id) {
                Review review = reviewRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Review", "id", id));

                reviewRepository.delete(review);
        }
}
