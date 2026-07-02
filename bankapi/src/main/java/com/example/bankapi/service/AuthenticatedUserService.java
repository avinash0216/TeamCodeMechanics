package com.example.bankapi.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthenticatedUserService {

    private final CustomerService customerService;

    public AuthenticatedUserService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public Jwt getCurrentJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT principal found");
        }
        return jwt;
    }

    public String getCurrentSubject() {
        return getCurrentJwt().getSubject();
    }

    public Long getCurrentCustomerId() {
        return customerService.resolveCustomerId(getCurrentSubject());
    }

    public List<String> getCurrentRoles() {
        List<String> roles = getCurrentJwt().getClaimAsStringList("roles");
        return roles != null ? roles : List.of();
    }

    public boolean hasRole(String role) {
        return getCurrentRoles().stream().anyMatch(currentRole -> currentRole.equalsIgnoreCase(role));
    }

    public Map<String, Object> extractUserDetails(Jwt jwt) {
        Map<String, Object> info = new HashMap<>();
        info.put("subject", jwt.getSubject());
        info.put("issuer", jwt.getIssuer().toString());
        info.put("scopes", jwt.getClaimAsString("scope"));
        info.put("tokenExpiry", jwt.getExpiresAt() != null ? jwt.getExpiresAt().toString() : null);

        List<String> roles = jwt.getClaimAsStringList("roles");
        info.put("roles", roles != null ? roles : List.of());

        String preferredUsername = jwt.getClaimAsString("preferred_username");
        info.put("preferredUsername", preferredUsername != null ? preferredUsername : "not present");

        String fullName = jwt.getClaimAsString("name");
        info.put("fullName", fullName != null ? fullName : "not present");
        return info;
    }
}
