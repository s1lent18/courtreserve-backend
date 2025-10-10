package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Team;
import com.example.courtreserve.database.models.Tournament;
import com.example.courtreserve.database.models.TournamentTeam;
import com.example.courtreserve.database.models.TournamentTeamId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TournamentTeamRepository extends JpaRepository<TournamentTeam, TournamentTeamId> {

    List<TournamentTeam> findByTournament(Tournament tournament);

    List<TournamentTeam> findByTeam(Team team);

    boolean existsByTournamentAndTeam(Tournament tournament, Team team);
}
