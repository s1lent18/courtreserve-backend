package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AddVendorResponse {
    Long id;
    String name;
    String email;
    LocalDateTime createdAt;
}