package com.example.courtreserve.database.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = {
            @UniqueConstraint(columnNames = {"user_id", "facility_id"})
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Court facility;

    @Column(nullable = false)
    private Integer rating;

    @Column(columnDefinition = "text")
    private String review;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;
}
