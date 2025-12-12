package com.example.courtreserve.controller;

import com.example.courtreserve.JWT.JwtUtil;
import com.example.courtreserve.dto.*;
import com.example.courtreserve.service.UserService;
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
            @RequestBody AddUserRequest request) {
        Map<String, AddUserResponse> response = new HashMap<>();
        AddUserResponse addUserResponse = userService.addNewUser(request);
        response.put("registerUserData", addUserResponse);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, LoginUserResponse>> loginUser(
            @RequestBody LoginUserRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        LoginUserResponse req = userService.getUser(request.getEmail());
        req.setToken(jwtUtil.generateToken(userDetails));

        Map<String, LoginUserResponse> response = new HashMap<>();
        response.put("userData", req);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/association")
    public ResponseEntity<?> teamAssociation(
            @RequestParam Long Id) {
        var teamAssociation = userService.findTeamAssociation(Id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("message", "Team Association", "teamAssociation", teamAssociation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, LoginUserResponse>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {
        LoginUserResponse updatedUser = userService.updateUser(id, request);
        Map<String, LoginUserResponse> response = new HashMap<>();
        response.put("userData", updatedUser);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id) {
        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
