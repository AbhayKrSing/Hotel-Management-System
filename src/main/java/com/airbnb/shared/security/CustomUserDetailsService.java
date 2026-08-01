package com.airbnb.shared.security;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.airbnb.user.model.User;
import com.airbnb.user.repository.UserRepository;


@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
		 this.userRepository=userRepository;
	}
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        List<SimpleGrantedAuthority> allRoles= userEntity.getRoles().stream().map((role)->new SimpleGrantedAuthority("ROLE_"+ role.getDisplayName())).collect(Collectors.toList());
        return new org.springframework.security.core.userdetails.User(
                userEntity.getEmail(),
                userEntity.getPassword(),
                allRoles
        );
    }
}