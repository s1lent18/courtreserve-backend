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
public class VendorService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AddVendorRequest {
        String name;
        String email;
        String password;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class AddVendorResponse {
        Long id;
        String name;
        String email;
        LocalDateTime createdAt;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class LoginVendorRequest {
        String email;
        String password;
    }

    @Getter @Setter
    @AllArgsConstructor
    public static class LoginVendorResponse {
        Long id;
        String name;
        String email;
        LocalDateTime createdAt;
        String token;

        public LoginVendorResponse(Long id, String name, String email, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.createdAt = createdAt;
        }
    }

    public VendorService.LoginVendorResponse getVendor(String email) {
        User vendor = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Vendor Not Found"));

        return new VendorService.LoginVendorResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getEmail(),
                vendor.getCreated()
        );
    }

    public VendorService.AddVendorResponse addNewVendor(VendorService.AddVendorRequest addVendorRequest) {
        boolean exists = userRepository.existsByEmail(addVendorRequest.getEmail());
        if (exists) {
            throw new RuntimeException("Vendor with email " + addVendorRequest.getEmail() + " already exists!");
        }

        Role vendorRole = roleRepository.findByName("VENDOR")
                .orElseThrow(() -> new RuntimeException("Default role VENDOR not found. Please seed roles table."));

        User newVendor = User.builder()
                .name(addVendorRequest.getName())
                .email(addVendorRequest.getEmail())
                .password(passwordEncoder.encode(addVendorRequest.getPassword()))
                .created(LocalDateTime.now())
                .build();

        newVendor.getRoles().add(vendorRole);

        User savedVendor = userRepository.save(newVendor);

        return new VendorService.AddVendorResponse(
                savedVendor.getId(),
                savedVendor.getName(),
                savedVendor.getEmail(),
                savedVendor.getCreated()
        );
    }
}
