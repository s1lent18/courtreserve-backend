package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVendorRequest {
    private String name;
    private String location;
    private String password;
    private String profileImage;
}
