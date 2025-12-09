package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.MatchRequest;
import com.example.courtreserve.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MatchRequestRepository extends JpaRepository<MatchRequest, Long> {

    List<MatchRequest> findByRequester(User requester);

    List<MatchRequest> findByCourtAndDate(Court court, LocalDate date);

    List<MatchRequest> findByStatus(String status);

    List<MatchRequest> findByRequestType(String requestType);

    List<MatchRequest> findByCourtAndDateAndStatus(Court court, LocalDate date, String status);
}