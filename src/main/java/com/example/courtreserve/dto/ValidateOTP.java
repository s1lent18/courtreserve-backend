package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateOTP {
    Long teamId;
    Long Id;
    String code;
}
