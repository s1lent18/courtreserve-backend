package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LoginVendorResponse {
    Long id;
    String name;
    String email;
    LocalDateTime createdAt;
    String token;
    String location;
    String coverImage;

    public LoginVendorResponse(Long id, String name, String email, LocalDateTime createdAt, String location, String coverImage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
        this.location = location;
        this.coverImage = coverImage;
    }
}