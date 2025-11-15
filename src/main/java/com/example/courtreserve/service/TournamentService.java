package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Tournament;
import com.example.courtreserve.dto.CreateTournamentRequest;
import com.example.courtreserve.dto.CreateTournamentResponse;
import com.example.courtreserve.dto.GetTournamentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TournamentService {

    CreateTournamentResponse createTournament(Long organizerId, CreateTournamentRequest request);

    List<Tournament> getPendingTournaments(Long vendorId);

    Tournament confirmTournament(Long tournamentId);

    Tournament rejectTournament(Long TournamentId);

    Tournament cancelTournament(Long TournamentId);

    Page<GetTournamentResponse> getAllTournaments(String location, int page, int size);
}
