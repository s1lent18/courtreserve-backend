package com.example.courtreserve.dto;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AddReviewResponse {
    Long id;
    User user;
    Court court;
    Integer rating;
    String review;
    LocalDateTime created;
}