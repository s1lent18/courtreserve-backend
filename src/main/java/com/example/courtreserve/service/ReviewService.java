package com.example.courtreserve.service;

import com.example.courtreserve.dto.AddReviewRequest;
import com.example.courtreserve.dto.AddReviewResponse;
import com.example.courtreserve.dto.UpdateReviewRequest;

public interface ReviewService {

    AddReviewResponse addReview(AddReviewRequest request);

    AddReviewResponse updateReview(Long id, UpdateReviewRequest request);

    void deleteReview(Long id);
}
