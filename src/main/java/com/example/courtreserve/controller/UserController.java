package com.example.courtreserve.controller;

import com.example.courtreserve.JWT.JwtUtil;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.BookingService;
import com.example.courtreserve.service.CourtService;
import com.example.courtreserve.service.MatchService;
import com.example.courtreserve.service.ReviewService;
import com.example.courtreserve.service.TeamService;
import com.example.courtreserve.service.TournamentService;
import com.example.courtreserve.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private final UserService userService;

    @Autowired
    private final ReviewService reviewService;

    @Autowired
    private final BookingService bookingService;

    @Autowired
    private final CourtService courtService;

    @Autowired
    private final CourtRepository courtRepository;

    @Autowired
    private final TournamentService tournamentService;

    @Autowired
    private final TeamService teamService;

    @Autowired
    private MatchService matchService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserDetailsService userDetailsService;

    public UserController(UserService userService, ReviewService reviewService, BookingService bookingService, CourtService courtService, CourtRepository courtRepository, TournamentService tournamentService, TeamService teamService) {
        this.userService = userService;
        this.reviewService = reviewService;
        this.bookingService = bookingService;
        this.courtService = courtService;
        this.courtRepository = courtRepository;
        this.tournamentService = tournamentService;
        this.teamService = teamService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, AddUserResponse>> registerUser(
            @RequestBody AddUserRequest request
    ) {
        try {
            Map<String, AddUserResponse> response = new HashMap<>();

            AddUserResponse addUserResponse = userService.addNewUser(request);

            response.put("registerUserData", addUserResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, LoginUserResponse>> loginUser(
            @RequestBody LoginUserRequest request
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            LoginUserResponse req = userService.getUser(request.getEmail());

            req.setToken(jwtUtil.generateToken(userDetails));

            Map<String, LoginUserResponse> response = new HashMap<>();

            response.put("userData", req);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getPopularCourts")
    public ResponseEntity<Map<String, Object>> getPopularCourts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String location
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<GetPopularCourts> courtsPage = courtRepository.findCourtStatsByCount(location, pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("courts", courtsPage.getContent());
        response.put("currentPage", courtsPage.getNumber());
        response.put("totalItems", courtsPage.getTotalElements());
        response.put("totalPages", courtsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/createBooking")
    public ResponseEntity<?> createBooking(
        @RequestBody AddBookingRequest request
    ) {
        try {
            var booking = bookingService.createBooking(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Booking created successfully", "booking", booking));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getAllBookings")
    public ResponseEntity<?> getAllBookings(
            @RequestParam Long id
    ) {
        try {
            var getBookings = bookingService.getAllBookings(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Bookings returned successfully", "bookings", getBookings));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/createReview")
    public ResponseEntity<?> createReview(
        @RequestBody AddReviewRequest request
    ) {
        try {
            var review = reviewService.addReview(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Review given successfully", "review", review));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getCourt")
    public ResponseEntity<?> getCourtById(
            @RequestParam Long id
    ) {
        try {
            var court = courtService.getCourtById(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Court Found Successfully", "court", court));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/createTournament")
    public ResponseEntity<?> createTournament(
            @RequestBody CreateTournamentRequest request,
            @RequestParam Long organizerId
    ) {
        try {
            var tournament = tournamentService.createTournament(organizerId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Tournament created successfully", "tournament", tournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/createTeam")
    public ResponseEntity<?> createTeam(
            @RequestBody CreateTeamRequest request,
            @RequestParam Long captainId
    ) {
        try {
            var team = teamService.createTeam(captainId, request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Team created successfully", "team", team));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/joinTournament")
    public ResponseEntity<?> joinTournament(
            @RequestBody JoinTournamentRequest request
    ) {
        try {
            var participation = teamService.joinTournament(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Team successfully registered for tournament", "participation", participation));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{tournamentId}/cancelTournament")
    public ResponseEntity<?> cancelTournament(
            @PathVariable Long tournamentId
    ) {
        try {
            var canceledTournament = tournamentService.rejectTournament(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Tournament Canceled", "canceledTournament", canceledTournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/{bookingId}/cancelBooking")
    public ResponseEntity<?> cancelBooking(
            @PathVariable Long bookingId
    ) {
        try {
            var canceledBooking = bookingService.cancelBooking(bookingId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Booking Canceled", "canceledBooking", canceledBooking));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getAllTournaments")
    public ResponseEntity<?> getAllTournaments(
            @RequestParam String location,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            var tournaments = tournamentService.getAllTournaments(location, page, size);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(
                            "message", "All Tournaments Obtained",
                            "page", tournaments.getNumber(),
                            "size", tournaments.getSize(),
                            "totalPages", tournaments.getTotalPages(),
                            "totalElements", tournaments.getTotalElements(),
                            "content", tournaments.getContent()
                    ));

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getSingleTournament")
    public ResponseEntity<?> getSingleTournament(
            @RequestParam Long Id
    ) {
        try {
            var tournament = tournamentService.getSingleTournament(Id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Single Tournament Returned", "singleTournament", tournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/getSingleTeam")
    public ResponseEntity<?> getSingleTeam(
            @RequestParam Long Id
    ) {
        try {
            var team = teamService.getSingleTeam(Id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Single Team Returned", "singleTeam", team));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{tournamentId}/startTournament")
    public ResponseEntity<?> startTournament(
            @PathVariable Long tournamentId
    ) {
        try {
            var tournament = tournamentService.startTournament(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Tournament started and bracket generated", "tournament", tournament));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/bracket")
    public ResponseEntity<?> getTournamentBracket(
            @PathVariable Long tournamentId
    ) {
        try {
            var bracket = matchService.getTournamentBracket(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Tournament bracket retrieved", "bracket", bracket));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/match/schedule")
    public ResponseEntity<?> scheduleMatch(
            @RequestBody ScheduleMatchRequest request
    ) {
        try {
            var match = matchService.scheduleMatch(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Match scheduled successfully", "match", match));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/match/updateResult")
    public ResponseEntity<?> updateMatchResult(
            @RequestBody UpdateMatchResultRequest request
    ) {
        try {
            var match = matchService.updateMatchResult(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Match result updated successfully", "match", match));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{tournamentId}/unscheduledMatches")
    public ResponseEntity<?> getUnscheduledMatches(
            @PathVariable Long tournamentId
    ) {
        try {
            var matches = matchService.getUnscheduledMatches(tournamentId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Unscheduled matches retrieved", "matches", matches));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<?> getMatchById(
            @PathVariable Long matchId
    ) {
        try {
            var match = matchService.getMatchById(matchId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Match retrieved successfully", "match", match));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/team/addMember")
    public ResponseEntity<?> addTeamMember(
            @RequestBody AddTeamMemberRequest request
    ) {
        try {
            var member = teamService.addTeamMember(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Team member added successfully", "member", member));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/team/removeMember")
    public ResponseEntity<?> removeTeamMember(
            @RequestBody RemoveTeamMemberRequest request
    ) {
        try {
            teamService.removeTeamMember(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Team member removed successfully"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/team/updateMember")
    public ResponseEntity<?> updateTeamMember(
            @RequestBody UpdateTeamMemberRequest request
    ) {
        try {
            var member = teamService.updateTeamMember(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Team member updated successfully", "member", member));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        }
    }
}