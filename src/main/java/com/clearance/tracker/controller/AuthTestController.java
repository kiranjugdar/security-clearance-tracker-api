package com.clearance.tracker.controller;

import com.clearance.tracker.security.JwtTokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Authentication Test Controller - For development and testing only
 * Only available when mock profile is active
 */
@RestController
@RequestMapping("/auth-test")
@ConditionalOnProperty(name = "spring.profiles.active", havingValue = "mock")
public class AuthTestController {

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    @GetMapping("/token/user")
    @PreAuthorize("permitAll()")
    public ResponseEntity<Map<String, String>> generateUserToken() {
        String token = jwtTokenGenerator.generateTestToken();
        return ResponseEntity.ok(Map.of(
            "token", token,
            "type", "Bearer",
            "username", "john.doe@example.com",
            "subjectPersonaObjectId", "272ad768-ea92-4972-a8a5-2c270fdddd33",
            "roles", "USER"
        ));
    }

    @GetMapping("/token/admin")
    public ResponseEntity<Map<String, String>> generateAdminToken() {
        String token = jwtTokenGenerator.generateAdminTestToken();
        return ResponseEntity.ok(Map.of(
            "token", token,
            "type", "Bearer",
            "username", "admin@example.com",
            "subjectPersonaObjectId", "admin-uuid-1234-5678-9012-345678901234",
            "roles", "ADMIN,USER"
        ));
    }

    @PostMapping("/token/custom")
    public ResponseEntity<Map<String, String>> generateCustomToken(
            @RequestParam String username,
            @RequestParam String subjectPersonaObjectId,
            @RequestParam(defaultValue = "USER") String roles) {
        
        List<String> roleList = List.of(roles.split(","));
        String token = jwtTokenGenerator.generateToken(username, subjectPersonaObjectId, roleList);
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "type", "Bearer",
            "username", username,
            "subjectPersonaObjectId", subjectPersonaObjectId,
            "roles", roles
        ));
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getInfo() {
        return ResponseEntity.ok(Map.of(
            "message", "JWT Test Token Generator",
            "endpoints", "/auth-test/token/user, /auth-test/token/admin, /auth-test/token/custom",
            "profile", "mock",
            "usage", "Add 'Authorization: Bearer <token>' header to API requests"
        ));
    }
}