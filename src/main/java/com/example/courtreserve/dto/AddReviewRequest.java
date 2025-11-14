package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddReviewRequest {
    Long userId;
    Long facilityId;
    Integer rating;
    String review;
}