package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Match;
import com.example.courtreserve.database.models.Tournament;
import com.example.courtreserve.database.repository.MatchRepository;
import com.example.courtreserve.database.repository.TournamentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduledTaskService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTaskService.class);

    @Autowired
    private TournamentRepository tournamentRepository;

    @Autowired
    private MatchRepository matchRepository;

    /**
     * Scheduled task that runs daily at 12:00 PM (noon)
     * Checks for tournaments in auto mode and handles scheduled matches
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void checkScheduledMatches() {
        logger.info("Running scheduled check at 12:00 PM for automatic match handling");

        try {
            // Find all tournaments that are in progress and in auto mode
            List<Tournament> autoModeTournaments = tournamentRepository.findByStatusAndIsAutoMode("IN_PROGRESS", true);

            logger.info("Found {} tournaments in auto mode", autoModeTournaments.size());

            for (Tournament tournament : autoModeTournaments) {
                logger.info("Checking tournament: {} (ID: {})", tournament.getName(), tournament.getId());

                // Get all matches for this tournament
                List<Match> matches = matchRepository.findByTournament(tournament);

                // Check for matches that should have started or ended
                LocalDateTime now = LocalDateTime.now();

                for (Match match : matches) {
                    // Check if match is scheduled and has both teams assigned
                    if ("SCHEDULED".equals(match.getStatus()) && 
                        match.getTeam1() != null && 
                        match.getTeam2() != null) {

                        // Check if match start time has passed
                        if (match.getStartTime() != null && match.getStartTime().isBefore(now)) {
                            logger.info("Match {} (ID: {}) has passed its start time. Teams: {} vs {}", 
                                match.getId(), match.getId(),
                                match.getTeam1().getName(), 
                                match.getTeam2().getName());
                            
                            // In auto mode, we can send notifications or prepare match
                            // For now, we'll just log it
                            // Additional logic can be added here for automatic handling
                        }
                    }

                    // Check for pending matches that need scheduling
                    if ("PENDING".equals(match.getStatus()) && 
                        match.getTeam1() != null && 
                        match.getTeam2() != null) {
                        
                        logger.info("Pending match {} (ID: {}) is ready to be scheduled. Teams: {} vs {}",
                            match.getId(), match.getId(),
                            match.getTeam1().getName(),
                            match.getTeam2().getName());
                        
                        // In auto mode, matches can be auto-scheduled
                        // Additional logic can be added here
                    }
                }
            }

            logger.info("Completed scheduled check at 12:00 PM");

        } catch (Exception e) {
            logger.error("Error occurred during scheduled match check", e);
        }
    }

    /**
     * Check tournament completion status
     * This method can be called by the scheduled task to verify tournament status
     */
    public void checkTournamentCompletion() {
        logger.info("Checking tournament completion status");

        try {
            List<Tournament> inProgressTournaments = tournamentRepository.findByStatus("IN_PROGRESS");

            for (Tournament tournament : inProgressTournaments) {
                List<Match> matches = matchRepository.findByTournament(tournament);

                boolean allCompleted = matches.stream()
                        .allMatch(m -> "COMPLETED".equals(m.getStatus()) || "BYE".equals(m.getStatus()));

                if (allCompleted) {
                    tournament.setStatus("COMPLETED");
                    tournamentRepository.save(tournament);
                    logger.info("Tournament {} (ID: {}) marked as COMPLETED", 
                        tournament.getName(), tournament.getId());
                }
            }

        } catch (Exception e) {
            logger.error("Error checking tournament completion", e);
        }
    }
}

