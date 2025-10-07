package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
