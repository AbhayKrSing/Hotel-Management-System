package com.airbnb.user.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.airbnb.user.dto.UserDTO;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;
import com.airbnb.user.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	public UserServiceImpl(UserRepository userRepository) {
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
		userDto.setEmail(user.getEmail());
		userDto.setFullName(user.getName());
		userDto.setRole(user.getRoles());
		return userDto;
	}

}
