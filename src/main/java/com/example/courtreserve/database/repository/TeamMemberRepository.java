package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.Team;
import com.example.courtreserve.database.models.TeamMember;
import com.example.courtreserve.database.models.TeamMemberId;
import com.example.courtreserve.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    List<TeamMember> findByTeam(Team team);

    List<TeamMember> findByUser(User user);

    boolean existsByTeamAndUser(Team team, User user);
}