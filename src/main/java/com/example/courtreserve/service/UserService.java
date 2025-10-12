package com.example.courtreserve.service;

import com.example.courtreserve.database.models.Role;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.RoleRepository;
import com.example.courtreserve.database.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Getter @Setter
    @AllArgsConstructor
    public static class AddUserRequest {
        String name;
        String email;
        String password;
        String location;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class AddUserResponse {
        Long id;
        String name;
        String email;
        String location;
        LocalDateTime createdAt;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class LoginUserRequest {
        String email;
        String password;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class LoginUserResponse {
        Long id;
        String name;
        String email;
        String location;
        LocalDateTime createdAt;
        String token;

        public LoginUserResponse(Long id, String name, String email, LocalDateTime createdAt, String location) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.location = location;
            this.createdAt = createdAt;
        }
    }

    public LoginUserResponse getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found"));

        return new LoginUserResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getCreated(),
            user.getLocation()
        );
    }

    public AddUserResponse addNewUser(AddUserRequest addUserRequest) {
        boolean exists = userRepository.existsByEmail(addUserRequest.getEmail());
        if (exists) {
            throw new RuntimeException("User with email " + addUserRequest.getEmail() + " already exists!");
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role USER not found. Please seed roles table."));

        User newUser = User.builder()
                .name(addUserRequest.getName())
                .email(addUserRequest.getEmail())
                .password(passwordEncoder.encode(addUserRequest.getPassword()))
                .location(addUserRequest.getLocation())
                .created(LocalDateTime.now())
                .build();

        newUser.getRoles().add(userRole);

        User savedUser = userRepository.save(newUser);

        return new AddUserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getLocation(),
                savedUser.getCreated()
        );
    }
}