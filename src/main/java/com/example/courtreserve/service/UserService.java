package com.example.courtreserve.service;

import com.example.courtreserve.database.models.User;
import com.example.courtreserve.dto.AddUserRequest;
import com.example.courtreserve.dto.AddUserResponse;
import com.example.courtreserve.dto.LoginUserResponse;
import com.example.courtreserve.dto.TeamAssociation;

public interface UserService {

    LoginUserResponse getUser(String email);

    AddUserResponse addNewUser(AddUserRequest addUserRequest);

    TeamAssociation findTeamAssociation(Long Id);

    User findById(Long id);

    User findByEmail(String email);
}