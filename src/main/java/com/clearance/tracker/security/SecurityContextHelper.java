package com.clearance.tracker.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecurityContextHelper {

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    public String getCurrentSubjectPersonaObjectId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationDetails) {
            JwtAuthenticationDetails details = (JwtAuthenticationDetails) authentication.getDetails();
            return details.getSubjectPersonaObjectId();
        }
        return null;
    }

    public List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationDetails) {
            JwtAuthenticationDetails details = (JwtAuthenticationDetails) authentication.getDetails();
            return details.getRoles();
        }
        return null;
    }

    public String getCurrentUserPrimaryRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getDetails() instanceof JwtAuthenticationDetails) {
            JwtAuthenticationDetails details = (JwtAuthenticationDetails) authentication.getDetails();
            return details.getPrimaryRole();
        }
        return null;
    }

    public boolean hasRole(String role) {
        List<String> roles = getCurrentUserRoles();
        return roles != null && roles.contains(role);
    }

    public boolean hasAnyRole(String... roles) {
        List<String> userRoles = getCurrentUserRoles();
        if (userRoles != null) {
            for (String role : roles) {
                if (userRoles.contains(role)) {
                    return true;
                }
            }
        }
        return false;
    }
}