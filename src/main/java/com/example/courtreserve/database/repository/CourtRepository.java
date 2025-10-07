package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Court;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourtRepository extends JpaRepository<Court, Long> {

}
