package com.example.courtreserve.database.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "court_id")
    private Court court;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team1_id")
    private Team team1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team2_id")
    private Team team2;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    private Integer round; // Round number (1 for first round, 2 for second, etc.)

    @Column(name = "bracket_type")
    private String bracketType; // "WINNER" for main bracket, "LOSER" for losers bracket in double elimination

    @Column(name = "match_position")
    private Integer matchPosition; // Position in the current round

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winner_team_id")
    private Team winnerTeam;

    private Long nextWinnerMatch; // Next match for winner

    private Long nextLoserMatch; // Next match for loser (used in double elimination)
}