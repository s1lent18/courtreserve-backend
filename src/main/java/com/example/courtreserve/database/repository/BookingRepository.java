package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Booking;
import com.example.courtreserve.dto.BookingTimeProjection;
import com.example.courtreserve.service.BookingService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b.startTime AS startTime, b.endTime AS endTime FROM Booking b WHERE b.facility.id = :courtId")
    List<BookingTimeProjection> findBookingTimesByCourtId(@Param("courtId") Long courtId);

    List<Booking> findAllByStartTimeBefore(LocalDateTime time);

    @Query("SELECT b FROM Booking b WHERE b.facility.id IN :courtIds AND b.status = 'PENDING'")
    List<Booking> findPendingBookings(@Param("courtIds") List<Long> courtIds);

    List<Booking> findAllByUser_Id(Long Id);
}