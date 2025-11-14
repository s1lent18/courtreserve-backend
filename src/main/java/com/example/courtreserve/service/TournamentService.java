package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Tournament;
import com.example.courtreserve.dto.CreateTournamentRequest;
import com.example.courtreserve.dto.CreateTournamentResponse;
import java.util.List;

public interface TournamentService {

    CreateTournamentResponse createTournament(Long organizerId, CreateTournamentRequest request);

    List<Tournament> getPendingTournaments(Long vendorId);

    Tournament confirmTournament(Long tournamentId);

    Tournament rejectTournament(Long TournamentId);

    Tournament cancelTournament(Long TournamentId);
}
