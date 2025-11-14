package com.example.courtreserve.service;

import com.example.courtreserve.dto.AddReviewRequest;
import com.example.courtreserve.dto.AddReviewResponse;

public interface ReviewService {

    AddReviewResponse addReview(AddReviewRequest request);
}
