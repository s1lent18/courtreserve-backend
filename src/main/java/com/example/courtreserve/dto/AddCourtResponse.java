package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddCourtResponse {
    private Long id;
    private String name;
    private String description;
    private String location;
    private Integer price;
    private String openTime;
    private String closeTime;
    private String type;
}