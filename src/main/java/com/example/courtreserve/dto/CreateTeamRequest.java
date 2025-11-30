package com.example.courtreserve.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateTeamRequest {
    private String name;
    private String sport;
    @Schema(type = "array", implementation = Long.class)
    private List<Long> memberIds;
}