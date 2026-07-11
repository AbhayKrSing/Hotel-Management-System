package com.airbnb.user.controller;

import com.airbnb.shared.security.JwtUtil;
import com.airbnb.user.dto.LoginRequest;
import com.airbnb.user.dto.LoginResponse;
import com.airbnb.user.dto.RegisterRequest;
import com.airbnb.user.enums.Roles;
import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/${api.version}/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    public AuthController(AuthenticationManager authenticationManager
    		,UserRepository userRepository,PasswordEncoder passwordEncoder,JwtUtil jwtUtil) {
		this.authenticationManager=authenticationManager;
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
		this.jwtUtil=jwtUtil;
	}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        // Validate role
        Set<Roles> role;
        try {
        	 role = request.getRoles();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role. Allowed: GUEST, HOST, ADMIN");
        }

        // Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getFullName());
        user.setRoles(role);

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // Get user details
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate JWT
            Set<String> userRoles=user.getRoles().stream().map((role)->role.getDisplayName()).collect(Collectors.toSet());
            String token = jwtUtil.generateToken(user.getEmail(), userRoles);
            LoginResponse reslogin= new LoginResponse();
            reslogin.setEmail(user.getEmail());
            reslogin.setToken(token);
            reslogin.setRole(user.getRoles());
            return ResponseEntity.ok(reslogin);

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }
    }
}