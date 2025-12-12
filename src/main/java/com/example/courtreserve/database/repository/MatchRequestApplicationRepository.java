package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.MatchRequest;
import com.example.courtreserve.database.models.MatchRequestApplication;
import com.example.courtreserve.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRequestApplicationRepository extends JpaRepository<MatchRequestApplication, Long> {

    List<MatchRequestApplication> findByMatchRequest(MatchRequest matchRequest);

    List<MatchRequestApplication> findByApplicant(User applicant);

    boolean existsByApplicant_Id(Long userId);
}
