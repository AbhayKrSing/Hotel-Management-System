package com.airbnb.user.client.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.airbnb.shared.client.UserClient;
import com.airbnb.user.dto.UserDTO;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserClientImpl implements UserClient {

	private final UserRepository userRepository;

	public UserClientImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDTO getUserById(UUID id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
		return convertToDTO(user);
	}

	@Override
	public UserDTO getUserByEmail(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
		return convertToDTO(user);
	}

	@Override
	public boolean userExists(UUID id) {
		return userRepository.existsById(id);
	}

	private UserDTO convertToDTO(User user) {
		UserDTO userDto= new UserDTO();
		userDto.setId(user.getId());
		userDto.setEmail(user.getEmail());
		userDto.setFullName(user.getName());
		userDto.setRole(user.getRoles());
		userDto.setActive(true);
		return userDto;
	}

}
