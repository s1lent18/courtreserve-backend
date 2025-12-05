package com.example.courtreserve.controller;

import com.example.courtreserve.JWT.JwtUtil;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
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
    JwtUtil jwtUtil;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private final UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    public UserController(UserService userService) {
        this.userService = userService;
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

    @GetMapping("/association")
    public ResponseEntity<?> teamAssociation(
            @RequestParam Long Id
    ) {
        try {
            var teamAssociation = userService.findTeamAssociation(Id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of("message", "Team Association", "teamAssociation", teamAssociation));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}