package com.airbnb.user.service;

import java.util.UUID;

import com.airbnb.user.dto.UserDTO;

public interface UserService {

	UserDTO getUserByEmail(String email);

	boolean userExists(UUID id);

	UserDTO getUserById(UUID id);
}
