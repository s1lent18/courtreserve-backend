package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.*;
import com.example.courtreserve.database.repository.*;
import com.example.courtreserve.dto.GetMatchResponse;
import com.example.courtreserve.dto.GetTournamentBracketResponse;
import com.example.courtreserve.dto.ScheduleMatchRequest;
import com.example.courtreserve.dto.UpdateMatchResultRequest;
import com.example.courtreserve.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Override
    @Transactional
    public void generateBracket(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        if (!"CONFIRMED".equals(tournament.getStatus())) {
            throw new RuntimeException("Tournament must be confirmed before generating bracket");
        }

        // Get all registered teams
        List<TournamentTeam> tournamentTeams = tournament.getRegisteredTeams();
        if (tournamentTeams == null || tournamentTeams.isEmpty()) {
            throw new RuntimeException("No teams registered for tournament");
        }

        List<Team> teams = tournamentTeams.stream()
                .map(TournamentTeam::getTeam)
                .collect(Collectors.toList());

        // Shuffle teams for random seeding
        Collections.shuffle(teams);

        if ("SINGLE".equals(tournament.getEliminationType())) {
            generateSingleEliminationBracket(tournament, teams);
        } else if ("DOUBLE".equals(tournament.getEliminationType())) {
            generateDoubleEliminationBracket(tournament, teams);
        } else {
            throw new RuntimeException("Invalid elimination type");
        }

        // Update tournament status
        tournament.setStatus("IN_PROGRESS");
        tournamentRepository.save(tournament);
    }

    private void generateSingleEliminationBracket(Tournament tournament, List<Team> teams) {
        int teamCount = teams.size();
        
        // Find next power of 2
        int bracketSize = 1;
        while (bracketSize < teamCount) {
            bracketSize *= 2;
        }

        // Create first round matches
        List<Match> firstRoundMatches = new ArrayList<>();
        int matchPosition = 0;

        for (int i = 0; i < bracketSize / 2; i++) {
            Team team1 = i < teamCount ? teams.get(i * 2) : null;
            Team team2 = (i * 2 + 1) < teamCount ? teams.get(i * 2 + 1) : null;

            if (team1 != null || team2 != null) {
                Match match = Match.builder()
                        .tournament(tournament)
                        .court(tournament.getCourt())
                        .team1(team1)
                        .team2(team2)
                        .round(1)
                        .bracketType("WINNER")
                        .matchPosition(matchPosition++)
                        .status(team2 == null ? "BYE" : "PENDING")
                        .build();

                // If one team is null (bye), set winner immediately
                if (team2 == null && team1 != null) {
                    match.setWinnerTeam(team1);
                    match.setStatus("COMPLETED");
                } else if (team1 == null && team2 != null) {
                    match.setWinnerTeam(team2);
                    match.setStatus("COMPLETED");
                }

                firstRoundMatches.add(match);
            }
        }

        // Save first round matches
        firstRoundMatches = matchRepository.saveAll(firstRoundMatches);

        // Generate subsequent rounds
        List<Match> previousRoundMatches = firstRoundMatches;
        int round = 2;

        while (previousRoundMatches.size() > 1) {
            List<Match> currentRoundMatches = new ArrayList<>();
            matchPosition = 0;

            for (int i = 0; i < previousRoundMatches.size(); i += 2) {
                Match match = Match.builder()
                        .tournament(tournament)
                        .court(tournament.getCourt())
                        .round(round)
                        .bracketType("WINNER")
                        .matchPosition(matchPosition++)
                        .status("PENDING")
                        .build();

                currentRoundMatches.add(match);
            }

            // Save current round matches
            currentRoundMatches = matchRepository.saveAll(currentRoundMatches);

            // Link previous round matches to current round
            for (int i = 0; i < previousRoundMatches.size(); i++) {
                Match prevMatch = previousRoundMatches.get(i);
                Match nextMatch = currentRoundMatches.get(i / 2);
                prevMatch.setNextWinnerMatch(nextMatch);
                matchRepository.save(prevMatch);
            }

            previousRoundMatches = currentRoundMatches;
            round++;
        }
    }

    private void generateDoubleEliminationBracket(Tournament tournament, List<Team> teams) {
        int teamCount = teams.size();
        
        // Find next power of 2
        int bracketSize = 1;
        while (bracketSize < teamCount) {
            bracketSize *= 2;
        }

        // Create first round matches in winner bracket
        List<Match> winnerBracketRound1 = new ArrayList<>();
        int matchPosition = 0;

        for (int i = 0; i < bracketSize / 2; i++) {
            Team team1 = i < teamCount ? teams.get(i * 2) : null;
            Team team2 = (i * 2 + 1) < teamCount ? teams.get(i * 2 + 1) : null;

            if (team1 != null || team2 != null) {
                Match match = Match.builder()
                        .tournament(tournament)
                        .court(tournament.getCourt())
                        .team1(team1)
                        .team2(team2)
                        .round(1)
                        .bracketType("WINNER")
                        .matchPosition(matchPosition++)
                        .status(team2 == null ? "BYE" : "PENDING")
                        .build();

                // Handle bye matches
                if (team2 == null && team1 != null) {
                    match.setWinnerTeam(team1);
                    match.setStatus("COMPLETED");
                } else if (team1 == null && team2 != null) {
                    match.setWinnerTeam(team2);
                    match.setStatus("COMPLETED");
                }

                winnerBracketRound1.add(match);
            }
        }

        // Save winner bracket round 1
        winnerBracketRound1 = matchRepository.saveAll(winnerBracketRound1);

        // Create loser bracket structure
        List<Match> loserBracketMatches = new ArrayList<>();
        matchPosition = 0;

        // Loser bracket receives losers from winner bracket round 1
        int loserRound = 1;
        List<Match> previousLoserMatches = new ArrayList<>();

        // First loser bracket round - pairs of losers from WB R1
        for (int i = 0; i < winnerBracketRound1.size() / 2; i++) {
            Match loserMatch = Match.builder()
                    .tournament(tournament)
                    .court(tournament.getCourt())
                    .round(loserRound)
                    .bracketType("LOSER")
                    .matchPosition(matchPosition++)
                    .status("PENDING")
                    .build();
            
            loserBracketMatches.add(loserMatch);
            previousLoserMatches.add(loserMatch);
        }

        loserBracketMatches = matchRepository.saveAll(loserBracketMatches);

        // Link winner bracket R1 to loser bracket
        for (int i = 0; i < winnerBracketRound1.size(); i++) {
            Match wbMatch = winnerBracketRound1.get(i);
            Match lbMatch = loserBracketMatches.get(i / 2);
            wbMatch.setNextLoserMatch(lbMatch);
            matchRepository.save(wbMatch);
        }

        // Generate winner bracket subsequent rounds
        List<Match> previousWinnerMatches = winnerBracketRound1;
        int winnerRound = 2;

        while (previousWinnerMatches.size() > 1) {
            List<Match> currentRoundMatches = new ArrayList<>();
            matchPosition = 0;

            for (int i = 0; i < previousWinnerMatches.size(); i += 2) {
                Match match = Match.builder()
                        .tournament(tournament)
                        .court(tournament.getCourt())
                        .round(winnerRound)
                        .bracketType("WINNER")
                        .matchPosition(matchPosition++)
                        .status("PENDING")
                        .build();

                currentRoundMatches.add(match);
            }

            currentRoundMatches = matchRepository.saveAll(currentRoundMatches);

            // Link to loser bracket
            List<Match> newLoserMatches = new ArrayList<>();
            matchPosition = 0;
            loserRound++;

            for (int i = 0; i < currentRoundMatches.size(); i++) {
                Match loserMatch = Match.builder()
                        .tournament(tournament)
                        .court(tournament.getCourt())
                        .round(loserRound)
                        .bracketType("LOSER")
                        .matchPosition(matchPosition++)
                        .status("PENDING")
                        .build();
                
                newLoserMatches.add(loserMatch);
            }

            newLoserMatches = matchRepository.saveAll(newLoserMatches);

            // Link previous winner round to current round and loser bracket
            for (int i = 0; i < previousWinnerMatches.size(); i++) {
                Match prevMatch = previousWinnerMatches.get(i);
                Match nextMatch = currentRoundMatches.get(i / 2);
                Match loserMatch = newLoserMatches.get(i / 2);
                
                prevMatch.setNextWinnerMatch(nextMatch);
                prevMatch.setNextLoserMatch(loserMatch);
                matchRepository.save(prevMatch);
            }

            // Create loser bracket progression
            List<Match> loserProgression = new ArrayList<>();
            matchPosition = 0;
            loserRound++;

            for (int i = 0; i < newLoserMatches.size(); i += 2) {
                if (i + 1 < newLoserMatches.size()) {
                    Match progressMatch = Match.builder()
                            .tournament(tournament)
                            .court(tournament.getCourt())
                            .round(loserRound)
                            .bracketType("LOSER")
                            .matchPosition(matchPosition++)
                            .status("PENDING")
                            .build();
                    
                    loserProgression.add(progressMatch);
                }
            }

            if (!loserProgression.isEmpty()) {
                loserProgression = matchRepository.saveAll(loserProgression);

                for (int i = 0; i < newLoserMatches.size(); i++) {
                    if (i / 2 < loserProgression.size()) {
                        newLoserMatches.get(i).setNextWinnerMatch(loserProgression.get(i / 2));
                        matchRepository.save(newLoserMatches.get(i));
                    }
                }

                previousLoserMatches = loserProgression;
            }

            previousWinnerMatches = currentRoundMatches;
            winnerRound++;
        }

        // Create grand final
        Match grandFinal = Match.builder()
                .tournament(tournament)
                .court(tournament.getCourt())
                .round(winnerRound)
                .bracketType("GRAND_FINAL")
                .matchPosition(0)
                .status("PENDING")
                .build();

        grandFinal = matchRepository.save(grandFinal);

        // Link winner bracket final and loser bracket final to grand final
        if (!previousWinnerMatches.isEmpty()) {
            Match wbFinal = previousWinnerMatches.get(0);
            wbFinal.setNextWinnerMatch(grandFinal);
            matchRepository.save(wbFinal);
        }

        if (!previousLoserMatches.isEmpty()) {
            Match lbFinal = previousLoserMatches.get(previousLoserMatches.size() - 1);
            lbFinal.setNextWinnerMatch(grandFinal);
            matchRepository.save(lbFinal);
        }
    }

    @Override
    @Transactional
    public Match updateMatchResult(UpdateMatchResultRequest request) {
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (!"SCHEDULED".equals(match.getStatus()) && !"PENDING".equals(match.getStatus())) {
            throw new RuntimeException("Match is not in a state to be updated");
        }

        Team winnerTeam = teamRepository.findById(request.getWinnerTeamId())
                .orElseThrow(() -> new RuntimeException("Winner team not found"));

        if (!winnerTeam.equals(match.getTeam1()) && !winnerTeam.equals(match.getTeam2())) {
            throw new RuntimeException("Winner team must be one of the teams in the match");
        }

        // Update match
        match.setWinnerTeam(winnerTeam);
        match.setStatus("COMPLETED");
        match = matchRepository.save(match);

        // Save scores
        if (request.getTeam1Score() != null) {
            Score score1 = Score.builder()
                    .match(match)
                    .team(match.getTeam1())
                    .score(request.getTeam1Score())
                    .remarks(request.getRemarks())
                    .build();
            scoreRepository.save(score1);
        }

        if (request.getTeam2Score() != null) {
            Score score2 = Score.builder()
                    .match(match)
                    .team(match.getTeam2())
                    .score(request.getTeam2Score())
                    .remarks(request.getRemarks())
                    .build();
            scoreRepository.save(score2);
        }

        // Progress winner and loser to next matches
        Team loserTeam = winnerTeam.equals(match.getTeam1()) ? match.getTeam2() : match.getTeam1();

        // Progress winner
        if (match.getNextWinnerMatch() != null) {
            Match nextMatch = match.getNextWinnerMatch();
            if (nextMatch.getTeam1() == null) {
                nextMatch.setTeam1(winnerTeam);
            } else if (nextMatch.getTeam2() == null) {
                nextMatch.setTeam2(winnerTeam);
            }
            matchRepository.save(nextMatch);
        }

        // Progress loser (double elimination only)
        if (match.getNextLoserMatch() != null && loserTeam != null) {
            Match nextLoserMatch = match.getNextLoserMatch();
            if (nextLoserMatch.getTeam1() == null) {
                nextLoserMatch.setTeam1(loserTeam);
            } else if (nextLoserMatch.getTeam2() == null) {
                nextLoserMatch.setTeam2(loserTeam);
            }
            matchRepository.save(nextLoserMatch);
        }

        // Check if tournament is complete
        checkTournamentCompletion(match.getTournament());

        return match;
    }

    private void checkTournamentCompletion(Tournament tournament) {
        List<Match> allMatches = matchRepository.findByTournament(tournament);
        
        boolean allComplete = allMatches.stream()
                .allMatch(m -> "COMPLETED".equals(m.getStatus()) || "BYE".equals(m.getStatus()));

        if (allComplete) {
            tournament.setStatus("COMPLETED");
            tournamentRepository.save(tournament);
        }
    }

    @Override
    public GetTournamentBracketResponse getTournamentBracket(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        List<Match> allMatches = matchRepository.findByTournament(tournament);

        List<GetMatchResponse> winnerBracket = allMatches.stream()
                .filter(m -> "WINNER".equals(m.getBracketType()) || "GRAND_FINAL".equals(m.getBracketType()))
                .map(this::convertToMatchResponse)
                .sorted(Comparator.comparing(GetMatchResponse::getRound)
                        .thenComparing(GetMatchResponse::getMatchPosition))
                .collect(Collectors.toList());

        List<GetMatchResponse> loserBracket = allMatches.stream()
                .filter(m -> "LOSER".equals(m.getBracketType()))
                .map(this::convertToMatchResponse)
                .sorted(Comparator.comparing(GetMatchResponse::getRound)
                        .thenComparing(GetMatchResponse::getMatchPosition))
                .collect(Collectors.toList());

        return new GetTournamentBracketResponse(
                tournament.getId(),
                tournament.getName(),
                tournament.getEliminationType(),
                tournament.getIsAutoMode(),
                tournament.getStatus(),
                winnerBracket,
                loserBracket
        );
    }

    @Override
    @Transactional
    public Match scheduleMatch(ScheduleMatchRequest request) {
        Match match = matchRepository.findById(request.getMatchId())
                .orElseThrow(() -> new RuntimeException("Match not found"));

        if (match.getTeam1() == null || match.getTeam2() == null) {
            throw new RuntimeException("Both teams must be assigned before scheduling");
        }

        match.setStartTime(request.getStartTime());
        match.setEndTime(request.getEndTime());
        match.setStatus("SCHEDULED");

        return matchRepository.save(match);
    }

    @Override
    public List<GetMatchResponse> getUnscheduledMatches(Long tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new RuntimeException("Tournament not found"));

        List<Match> matches = matchRepository.findByTournament(tournament);

        return matches.stream()
                .filter(m -> "PENDING".equals(m.getStatus()) && m.getTeam1() != null && m.getTeam2() != null)
                .map(this::convertToMatchResponse)
                .sorted(Comparator.comparing(GetMatchResponse::getRound)
                        .thenComparing(GetMatchResponse::getMatchPosition))
                .collect(Collectors.toList());
    }

    @Override
    public GetMatchResponse getMatchById(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        return convertToMatchResponse(match);
    }

    private GetMatchResponse convertToMatchResponse(Match match) {
        GetMatchResponse response = new GetMatchResponse();
        response.setId(match.getId());
        response.setTournamentId(match.getTournament().getId());
        response.setTournamentName(match.getTournament().getName());
        response.setCourtId(match.getCourt().getId());
        response.setCourtName(match.getCourt().getName());
        
        if (match.getTeam1() != null) {
            response.setTeam1Id(match.getTeam1().getId());
            response.setTeam1Name(match.getTeam1().getName());
        }
        
        if (match.getTeam2() != null) {
            response.setTeam2Id(match.getTeam2().getId());
            response.setTeam2Name(match.getTeam2().getName());
        }
        
        response.setStartTime(match.getStartTime());
        response.setEndTime(match.getEndTime());
        response.setStatus(match.getStatus());
        response.setRound(match.getRound());
        response.setBracketType(match.getBracketType());
        response.setMatchPosition(match.getMatchPosition());
        
        if (match.getWinnerTeam() != null) {
            response.setWinnerTeamId(match.getWinnerTeam().getId());
            response.setWinnerTeamName(match.getWinnerTeam().getName());
        }
        
        return response;
    }
}

