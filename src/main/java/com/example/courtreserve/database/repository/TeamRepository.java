package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Team;
import com.example.courtreserve.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    boolean existsByName(String name);

    @Query("SELECT t.id FROM Team t WHERE t.captain.id = :userId")
    Optional<Long> findCaptainTeamId(@Param("userId") Long userId);
}
