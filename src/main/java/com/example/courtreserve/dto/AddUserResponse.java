package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AddUserResponse {
    Long id;
    String name;
    String email;
    String location;
    String coverImage;
    LocalDateTime createdAt;
}