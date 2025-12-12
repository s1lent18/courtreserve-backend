package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Team;
import com.example.courtreserve.database.models.TeamMember;
import com.example.courtreserve.database.models.TeamMemberId;
import com.example.courtreserve.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    List<TeamMember> findByTeam(Team team);

    List<TeamMember> findByUser(User user);

    boolean existsByTeamAndUser(Team team, User user);

    @Query("SELECT tm.team.id FROM TeamMember tm WHERE tm.user.id = :userId")
    Optional<Long> findMemberTeamIds(@Param("userId") Long userId);

    boolean existsByUser_Id(Long userId);
}