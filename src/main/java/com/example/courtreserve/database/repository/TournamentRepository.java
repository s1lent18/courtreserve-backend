package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Court;
import com.example.courtreserve.database.models.Tournament;
import com.example.courtreserve.database.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Long> {

    List<Tournament> findByOrganizer(User organizer);

    List<Tournament> findBySport(String sport);

    List<Tournament> findByCourt(Court court);

    List<Tournament> findByStatus(String status);

    List<Tournament> findByStatusAndIsAutoMode(String status, Boolean isAutoMode);

    @Query("SELECT t FROM Tournament t WHERE t.court.id IN :courtIds AND t.status = 'PENDING'")
    List<Tournament> findPendingTournaments(@Param("courtIds") List<Long> courtIds);

    Page<Tournament> findAllByCourt_Location(String location, Pageable pageable);

    boolean existsByOrganizer_Id(Long userId);

    Tournament findByOrganizer_Id(Long userId);
}