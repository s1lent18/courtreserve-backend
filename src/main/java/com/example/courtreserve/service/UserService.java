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
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class AddUserResponse {
        Long id;
        String name;
        String email;
        LocalDateTime createdAt;
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
                .build();

        newUser.getRoles().add(userRole);

        User savedUser = userRepository.save(newUser);

        return new AddUserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getCreated()
        );
    }
}