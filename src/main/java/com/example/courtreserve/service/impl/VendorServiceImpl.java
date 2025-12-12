package com.example.courtreserve.service.impl;

import com.example.courtreserve.database.models.Role;
import com.example.courtreserve.database.models.User;
import com.example.courtreserve.database.repository.CourtRepository;
import com.example.courtreserve.database.repository.RoleRepository;
import com.example.courtreserve.database.repository.UserRepository;
import com.example.courtreserve.dto.AddVendorRequest;
import com.example.courtreserve.dto.AddVendorResponse;
import com.example.courtreserve.dto.LoginVendorResponse;
import com.example.courtreserve.dto.UpdateVendorRequest;
import com.example.courtreserve.exception.ConflictException;
import com.example.courtreserve.exception.ForeignKeyConstraintException;
import com.example.courtreserve.exception.ResourceNotFoundException;
import com.example.courtreserve.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {

        @Value("${defaultImage.url}")
        String coverImage;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private RoleRepository roleRepository;

        @Autowired
        private CourtRepository courtRepository;

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
                                vendor.getCoverImage());
        }

        @Transactional
        public AddVendorResponse addNewVendor(AddVendorRequest addVendorRequest) {
                boolean exists = userRepository.existsByEmail(addVendorRequest.getEmail());
                if (exists) {
                        throw new ConflictException("Vendor", "email", addVendorRequest.getEmail());
                }

                Role vendorRole = roleRepository.findByName("VENDOR")
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Default role VENDOR not found. Please seed roles table."));

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
                                savedVendor.getCoverImage());
        }

        @Override
        @Transactional
        public LoginVendorResponse updateVendor(Long id, UpdateVendorRequest request) {
                User vendor = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

                // Update only non-null fields
                if (request.getName() != null) {
                        vendor.setName(request.getName());
                }
                if (request.getLocation() != null) {
                        vendor.setLocation(request.getLocation());
                }
                if (request.getProfileImage() != null) {
                        vendor.setCoverImage(request.getProfileImage());
                }

                User updatedVendor = userRepository.save(vendor);

                return new LoginVendorResponse(
                                updatedVendor.getId(),
                                updatedVendor.getName(),
                                updatedVendor.getEmail(),
                                updatedVendor.getCreated(),
                                updatedVendor.getLocation(),
                                updatedVendor.getCoverImage());
        }

        @Override
        @Transactional
        public void deleteVendor(Long id) {
                User vendor = userRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Vendor", "id", id));

                // Check for foreign key constraints
                List<String> dependencies = new ArrayList<>();

                if (courtRepository.existsByVendor_Id(id)) {
                        dependencies.add("Courts");
                }

                if (!dependencies.isEmpty()) {
                        throw new ForeignKeyConstraintException("Vendor", id, dependencies);
                }

                userRepository.delete(vendor);
        }
}
