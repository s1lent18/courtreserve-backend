package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Team;
import com.example.courtreserve.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByCaptain(User captain);

    List<Team> findBySport(String sport);

    boolean existsByName(String name);
}
