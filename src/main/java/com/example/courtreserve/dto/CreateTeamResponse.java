package com.example.courtreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateTeamResponse {
    private Long id;
    private String name;
    private String sport;
    private Long captainId;
    private LocalDateTime created;
    private List<Long> memberIds;
}
