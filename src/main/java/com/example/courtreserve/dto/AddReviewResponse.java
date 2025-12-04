package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AddReviewResponse {
    Long id;
    Long userId;
    String userName;
    Long courtId;
    String courtName;
    Integer rating;
    String review;
    LocalDateTime created;
}