package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Role;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.RoleRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.AddUserRequest;
import com.example.courtreserve.dto.AddUserResponse;
import com.example.courtreserve.dto.LoginUserResponse;
import com.example.courtreserve.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    @Value("${defaultImage.url}")
    String coverImage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginUserResponse getUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User Not Found"));

        return new LoginUserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreated(),
                user.getLocation(),
                user.getCoverImage()
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
                savedUser.getCreated()
        );
    }
}
