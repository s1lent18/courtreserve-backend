package com.example.courtreserve.database.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "bookings",
    uniqueConstraints = {
            @UniqueConstraint(
                    name = "unique_booking_slot",
                    columnNames = {"facility_id", "start_time", "end_time"}
            )
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_id", nullable = false)
    private Court facility;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(length = 20)
    private String status = "PENDING";

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer advance;

    @Column(name = "to_be_paid", nullable = false)
    private Integer toBePaid;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;
}