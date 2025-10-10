package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Match;
import com.example.courtreserve.database.models.Score;
import com.example.courtreserve.database.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByMatch(Match match);

    Optional<Score> findByMatchAndTeam(Match match, Team team);
}