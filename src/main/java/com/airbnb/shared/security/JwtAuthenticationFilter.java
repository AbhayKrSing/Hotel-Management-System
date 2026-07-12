package com.airbnb.shared.security;


import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.airbnb.user.client.UserClient;
import com.airbnb.user.dto.UserDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	 @Value("${api.version}")
	private String apiVersion;

    private final JwtUtil jwtUtil;
    private final UserClient userClient;
    
    JwtAuthenticationFilter(JwtUtil jwtUtil,UserClient userClient){
    	this.jwtUtil=jwtUtil;
    	this.userClient=userClient;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip OPTIONS requests (preflight)
        if (request.getMethod().equals("OPTIONS")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip public endpoints (optimization)
        String path = request.getRequestURI();
        if (path.startsWith("/api/" + apiVersion + "/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Extract Authorization header
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract token
            final String jwtToken = authHeader.substring(7);

            // Validate token
            if (!jwtUtil.isTokenValid(jwtToken)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Extract email
            String userEmail = jwtUtil.extractEmail(jwtToken);

            // Check if already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Load user from database (via cross-module interface)
                UserDTO userDTO = userClient.getUserByEmail(userEmail);

                if (userDTO == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // Check if user is active
                if (!userDTO.isActive()) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("User account is disabled");
                    return;
                }

                // Create Authentication object
                List<SimpleGrantedAuthority> roles = userDTO.getRole().stream().map((role)->
                    new SimpleGrantedAuthority("ROLE_"+role.getDisplayName())).collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userEmail,
                                null,
                                roles
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
        }

        filterChain.doFilter(request, response);
    }
}