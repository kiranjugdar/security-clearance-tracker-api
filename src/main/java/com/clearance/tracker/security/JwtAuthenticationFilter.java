package com.clearance.tracker.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Extract JWT token from Authorization header
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                logger.warn("Unable to extract username from JWT token: {}", e.getMessage());
            }
        }

        // Validate token and set authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            if (jwtUtil.validateToken(jwt)) {
                
                // Extract user details from token
                String subjectPersonaObjectId = jwtUtil.extractSubjectPersonaObjectId(jwt);
                List<String> roles = jwtUtil.extractRoles(jwt);
                
                logger.debug("JWT token validated for user: {}, subjectPersonaObjectId: {}, roles: {}", 
                           username, subjectPersonaObjectId, roles);

                // Create authorities from roles
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                        .collect(Collectors.toList());

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
                
                // Add custom details including subjectPersonaObjectId
                JwtAuthenticationDetails details = new JwtAuthenticationDetails(request, subjectPersonaObjectId, roles);
                authToken.setDetails(details);

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authToken);
                
                logger.debug("Successfully authenticated user: {} with roles: {}", username, roles);
            } else {
                logger.warn("JWT token validation failed for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        
        logger.debug("JwtAuthenticationFilter checking path: {}", path);
        
        // Skip JWT validation for these paths
        boolean shouldSkip = path.startsWith("/api/swagger-ui") ||
               path.startsWith("/api/api-docs") ||
               path.startsWith("/api/v3/api-docs") ||
               path.equals("/api/health") ||
               path.equals("/api/actuator/health") ||
               path.startsWith("/api/auth-test");
               
        logger.debug("JwtAuthenticationFilter shouldNotFilter for path {}: {}", path, shouldSkip);
        return shouldSkip;
    }
}