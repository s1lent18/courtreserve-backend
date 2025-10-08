package com.example.courtreserve.controller;

import com.example.courtreserve.JWT.JwtUtil;
import com.example.courtreserve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, UserService.AddUserResponse>> registerUser(
            @RequestBody UserService.AddUserRequest request
    ) {
        try {
            Map<String, UserService.AddUserResponse> response = new HashMap<>();

            UserService.AddUserResponse addUserResponse = userService.addNewUser(request);

            response.put("registerUserData", addUserResponse);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, UserService.LoginUserResponse>> loginUser(
            @RequestBody UserService.LoginUserRequest request
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());

            UserService.LoginUserResponse req = userService.getUser(request.getEmail());

            req.setToken(jwtUtil.generateToken(userDetails));

            Map<String, UserService.LoginUserResponse> response = new HashMap<>();

            response.put("userData", req);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}