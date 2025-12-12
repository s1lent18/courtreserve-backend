package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Role;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.*;
import com.example.courtreserve.database.repository.RoleRepository;
import com.example.courtreserve.database.repository.TeamMemberRepository;
import com.example.courtreserve.database.repository.TeamRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.AddUserRequest;
import com.example.courtreserve.dto.AddUserResponse;
import com.example.courtreserve.dto.LoginUserResponse;
import com.example.courtreserve.dto.TeamAssociation;
import com.example.courtreserve.dto.UpdateUserRequest;
import com.example.courtreserve.exception.ConflictException;
import com.example.courtreserve.exception.ForeignKeyConstraintException;
import com.example.courtreserve.exception.ResourceNotFoundException;
import com.example.courtreserve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

        @Value("${defaultImage.url}")
        String coverImage;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private TeamRepository teamRepository;

        @Autowired
        private TeamMemberRepository teamMemberRepository;

        @Autowired
        private TournamentRepository tournamentRepository;

        @Autowired
        private BookingRepository bookingRepository;

        @Autowired
        private ReviewRepository reviewRepository;

        @Autowired
        private MatchRequestRepository matchRequestRepository;

        @Autowired
        private MatchRequestApplicationRepository matchRequestApplicationRepository;

        @Autowired
        private CourtRepository courtRepository;

        @Autowired
        private PasswordEncoder passwordEncoder;

        public LoginUserResponse getUser(String email) {
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

                return new LoginUserResponse(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getCreated(),
                                user.getLocation(),
                                user.getCoverImage());
        }

        @Transactional
        public AddUserResponse addNewUser(AddUserRequest addUserRequest) {
                boolean exists = userRepository.existsByEmail(addUserRequest.getEmail());
                if (exists) {
                        throw new ConflictException("User", "email", addUserRequest.getEmail());
                }

                Role userRole = roleRepository.findByName("USER")
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Default role USER not found. Please seed roles table."));

                User newUser = User.builder()
                                .name(addUserRequest.getName())
                                .email(addUserRequest.getEmail())
                                .password(passwordEncoder.encode(addUserRequest.getPassword()))
                                .location(addUserRequest.getLocation())
                                .created(LocalDateTime.now())
                                .coverImage(coverImage)
                                .build();

                newUser.getRoles().add(userRole);

                User savedUser = userRepository.save(newUser);

                return new AddUserResponse(
                                savedUser.getId(),
                                savedUser.getName(),
                                savedUser.getEmail(),
                                savedUser.getLocation(),
                                savedUser.getCoverImage(),
                                savedUser.getCreated());
        }

        @Override
        public TeamAssociation findTeamAssociation(Long Id) {
                User user = userRepository.findById(Id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", Id));

                Optional<Long> captainTeamId = teamRepository.findCaptainTeamId(user.getId());

                Optional<Long> memberTeamId = teamMemberRepository.findMemberTeamIds(user.getId());

                return new TeamAssociation(
                                captainTeamId.orElse(null),
                                memberTeamId.orElse(null));
        }

        @Override
        public User findById(Long id) {
                return userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        }

        @Override
        public User findByEmail(String email) {
                return userRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        }

        @Override
        @Transactional
        public LoginUserResponse updateUser(Long id, UpdateUserRequest request) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

                if (request.getName() != null) {
                        user.setName(request.getName());
                }
                if (request.getLocation() != null) {
                        user.setLocation(request.getLocation());
                }
                if (request.getProfileImage() != null) {
                        user.setCoverImage(request.getProfileImage());
                }

                User updatedUser = userRepository.save(user);

                return new LoginUserResponse(
                                updatedUser.getId(),
                                updatedUser.getName(),
                                updatedUser.getEmail(),
                                updatedUser.getCreated(),
                                updatedUser.getLocation(),
                                updatedUser.getCoverImage());
        }

        @Override
        @Transactional
        public void deleteUser(Long id) {
                User user = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

                List<String> dependencies = new ArrayList<>();

                if (teamRepository.existsByCaptain_Id(id)) {
                        dependencies.add("Teams (as captain)");
                }
                if (teamMemberRepository.existsByUser_Id(id)) {
                        dependencies.add("Team memberships");
                }
                if (tournamentRepository.existsByOrganizer_Id(id)) {
                        dependencies.add("Tournaments (as organizer)");
                }
                if (bookingRepository.existsByUser_Id(id)) {
                        dependencies.add("Bookings");
                }
                if (reviewRepository.existsByUser_Id(id)) {
                        dependencies.add("Reviews");
                }
                if (matchRequestRepository.existsByRequester_Id(id)) {
                        dependencies.add("Match requests");
                }
                if (matchRequestApplicationRepository.existsByApplicant_Id(id)) {
                        dependencies.add("Match request applications");
                }
                if (courtRepository.existsByVendor_Id(id)) {
                        dependencies.add("Courts (as vendor)");
                }

                if (!dependencies.isEmpty()) {
                        throw new ForeignKeyConstraintException("User", id, dependencies);
                }

                userRepository.delete(user);
        }
}
