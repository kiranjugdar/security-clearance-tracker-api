package com.clearance.tracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.issuer}")
    private String issuer;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractSubjectPersonaObjectId(String token) {
        return extractClaim(token, claims -> claims.get("subjectPersonaObjectId", String.class));
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> (List<String>) claims.get("roles"));
    }

    public String extractRole(String token) {
        List<String> roles = extractRoles(token);
        return roles != null && !roles.isEmpty() ? roles.get(0) : null;
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            logger.warn("JWT token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            logger.warn("JWT token is malformed: {}", e.getMessage());
            throw e;
        } catch (SecurityException e) {
            logger.warn("JWT signature does not match: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT token compact of handler are invalid: {}", e.getMessage());
            throw e;
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public Boolean validateToken(String token, String username) {
        try {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
        } catch (JwtException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public Boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            
            // Validate issuer
            if (!issuer.equals(claims.getIssuer())) {
                logger.warn("JWT token issuer validation failed. Expected: {}, Actual: {}", issuer, claims.getIssuer());
                return false;
            }

            // Check if token is expired
            if (isTokenExpired(token)) {
                logger.warn("JWT token is expired");
                return false;
            }

            // Validate required claims
            String subjectPersonaObjectId = claims.get("subjectPersonaObjectId", String.class);
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles");

            if (subjectPersonaObjectId == null || subjectPersonaObjectId.trim().isEmpty()) {
                logger.warn("JWT token missing required claim: subjectPersonaObjectId");
                return false;
            }

            if (roles == null || roles.isEmpty()) {
                logger.warn("JWT token missing required claim: roles");
                return false;
            }

            return true;
        } catch (JwtException e) {
            logger.warn("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }
}