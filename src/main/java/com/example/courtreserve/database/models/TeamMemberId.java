package com.example.courtreserve.database.models;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class TeamMemberId implements Serializable {
    private Long teamId;
    private Long userId;
}