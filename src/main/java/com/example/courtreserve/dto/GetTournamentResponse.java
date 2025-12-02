package com.example.courtreserve.dto;

import com.example.courtreserve.database.models.TournamentTeam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Data
@AllArgsConstructor
public class GetTournamentResponse {
    Long id;
    String name;
    String sport;
    Long organizerId;
    String organizerName;
    Long courtId;
    String courtName;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    Integer prize;
    LocalDateTime created;
    @Schema(type = "array", implementation = Object.class)
    List<GetTournamentTeam> teams;
    String eliminationType;
    Boolean isAutoMode;
}