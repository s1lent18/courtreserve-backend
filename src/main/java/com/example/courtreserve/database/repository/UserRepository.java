package com.example.courtreserve.database.repository;

import com.example.courtreserve.database.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}