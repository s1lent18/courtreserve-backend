package com.example.courtreserve.dto;

import java.time.LocalDateTime;

public interface BookingTimeProjection {
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
}
