package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Role;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.RoleRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.AddVendorRequest;
import com.example.courtreserve.dto.AddVendorResponse;
import com.example.courtreserve.dto.LoginVendorResponse;
import com.example.courtreserve.exception.ConflictException;
import com.example.courtreserve.exception.ResourceNotFoundException;
import com.example.courtreserve.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class VendorServiceImpl implements VendorService {

    @Value("${defaultImage.url}")
    String coverImage;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginVendorResponse getVendor(String email) {
        User vendor = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "email", email));

        return new LoginVendorResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getEmail(),
                vendor.getCreated(),
                vendor.getLocation(),
                vendor.getCoverImage()
        );
    }

    public AddVendorResponse addNewVendor(AddVendorRequest addVendorRequest) {
        boolean exists = userRepository.existsByEmail(addVendorRequest.getEmail());
        if (exists) {
            throw new ConflictException("Vendor", "email", addVendorRequest.getEmail());
        }

        Role vendorRole = roleRepository.findByName("VENDOR")
                .orElseThrow(() -> new ResourceNotFoundException("Default role VENDOR not found. Please seed roles table."));

        User newVendor = User.builder()
                .name(addVendorRequest.getName())
                .email(addVendorRequest.getEmail())
                .password(passwordEncoder.encode(addVendorRequest.getPassword()))
                .location(addVendorRequest.getLocation())
                .created(LocalDateTime.now())
                .coverImage(coverImage)
                .build();

        newVendor.getRoles().add(vendorRole);

        User savedVendor = userRepository.save(newVendor);

        return new AddVendorResponse(
                savedVendor.getId(),
                savedVendor.getName(),
                savedVendor.getEmail(),
                savedVendor.getCreated(),
                savedVendor.getLocation(),
                savedVendor.getCoverImage()
        );
    }
}
