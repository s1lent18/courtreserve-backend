package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidateOTP {
    String captainEmail;
    Long Id;
    String code;
}
