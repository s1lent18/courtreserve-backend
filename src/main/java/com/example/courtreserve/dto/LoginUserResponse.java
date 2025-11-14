package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LoginUserResponse {
    Long id;
    String name;
    String email;
    String location;
    LocalDateTime createdAt;
    String token;

    public LoginUserResponse(Long id, String name, String email, LocalDateTime createdAt, String location) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
        this.createdAt = createdAt;
    }
}