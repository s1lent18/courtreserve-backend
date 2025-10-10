package com.example.courtreserve.database.models;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TournamentTeamId implements Serializable {
    private Long tournamentId;
    private Long teamId;
}