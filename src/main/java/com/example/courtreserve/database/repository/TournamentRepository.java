package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.Tournament;
import com.example.courtreserve.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findByOrganizer(User organizer);

    List<Tournament> findBySport(String sport);

    List<Tournament> findByCourt(Court court);

    List<Tournament> findByStatus(String status);
}