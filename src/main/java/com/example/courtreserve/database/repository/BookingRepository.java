package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Booking;
import com.example.courtreserve.service.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b.start_time AS startTime, b.end_time AS endTime FROM Bookings b WHERE b.facility_id = :courtId")
    List<BookingService.BookingTimeProjection> findBookingTimesByCourtId(@Param("courtId") Long courtId);
}
