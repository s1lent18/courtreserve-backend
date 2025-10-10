package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Match;
import com.example.courtreserve.database.models.Team;
import com.example.courtreserve.database.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByTournament(Tournament tournament);

    List<Match> findByTeam1OrTeam2(Team team1, Team team2);

    List<Match> findByStatus(String status);
}