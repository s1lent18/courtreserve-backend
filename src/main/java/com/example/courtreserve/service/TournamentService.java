package com.example.courtreserve.service;

import com.example.courtreserve.dto.*;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TournamentService {

    CreateTournamentResponse createTournament(Long organizerId, CreateTournamentRequest request);

    List<TournamentResponse> getPendingTournaments(Long vendorId);

    TournamentResponse confirmTournament(Long tournamentId);

    TournamentResponse rejectTournament(Long TournamentId);

    TournamentResponse cancelTournament(Long TournamentId);

    PaginatedResponse<GetTournamentResponse> getAllTournaments(String location, Pageable pageable);

    GetSingleTournamentResponse getSingleTournament(Long Id);

    TournamentResponse startTournament(Long tournamentId);
}
