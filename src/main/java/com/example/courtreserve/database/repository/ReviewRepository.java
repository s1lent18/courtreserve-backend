package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}