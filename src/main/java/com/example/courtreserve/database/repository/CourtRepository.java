package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.service.CourtService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {

    @Query(value = """
    SELECT
        c1.id AS id,
        c1.name AS name,
        c1.description AS description,
        c1.location AS location,
        c1.type AS type,
        c1.price AS price,
        c1.open AS open,
        c1.close AS close,
        c1.vendor_id AS vendorId,
        COUNT(b1.id) AS bookingCount,
        AVG(r1.rating) AS avgRating
    FROM courts c1
    JOIN bookings b1 ON c1.id = b1.facility_id
    JOIN reviews r1 ON c1.id = r1.facility_id
    WHERE c1.location = :location
    GROUP BY
        c1.id, c1.name, c1.description, c1.location, c1.type,
        c1.price, c1.open, c1.close, c1.vendor_id
    """,
            countQuery = """
        SELECT COUNT(*) FROM courts c1 WHERE c1.location = :location
    """,
            nativeQuery = true)
    Page<CourtService.GetPopularCourts> findCourtStatsByCount(@Param("location") String location, Pageable pageable);


}