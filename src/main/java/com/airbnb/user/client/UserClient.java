package com.airbnb.user.client;

import java.util.UUID;

import com.airbnb.user.dto.UserDTO;

public interface UserClient {

	UserDTO getUserByEmail(String email);

	boolean userExists(UUID id);

	UserDTO getUserById(UUID id);
}
