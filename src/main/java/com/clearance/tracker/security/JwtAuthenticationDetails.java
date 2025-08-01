package com.clearance.tracker.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.List;

public class JwtAuthenticationDetails extends WebAuthenticationDetails {

    private final String subjectPersonaObjectId;
    private final List<String> roles;

    public JwtAuthenticationDetails(HttpServletRequest request, String subjectPersonaObjectId, List<String> roles) {
        super(request);
        this.subjectPersonaObjectId = subjectPersonaObjectId;
        this.roles = roles;
    }

    public String getSubjectPersonaObjectId() {
        return subjectPersonaObjectId;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getPrimaryRole() {
        return roles != null && !roles.isEmpty() ? roles.get(0) : null;
    }
}