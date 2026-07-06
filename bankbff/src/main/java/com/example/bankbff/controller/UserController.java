package com.example.bankbff.controller;

import com.example.bankbff.dto.UserInfo;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @GetMapping("/api/me")
    public UserInfo me(@AuthenticationPrincipal OidcUser principal) {
        String subject = principal.getSubject();
        String fullName = principal.getFullName();
        String preferredUsername = principal.getPreferredUsername();
        if (isBlank(preferredUsername)) {
            preferredUsername = isBlank(fullName) ? subject : fullName;
        }
        if (isBlank(fullName)) {
            fullName = preferredUsername;
        }
        List<String> roles = principal.getClaimAsStringList("roles");
        if (roles == null) {
            roles = List.of();
        }
        return new UserInfo(
                subject,
                preferredUsername,
                fullName,
                roles);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    @GetMapping(value = "/logged-out", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> loggedOut() {
        String html = "<!doctype html><html><head><meta charset=\"utf-8\"><title>Signed out</title></head><body>"
                + "<h1>Signed out</h1>"
                + "<p>You have signed out of the BFF. Your identity provider session remains so future sign-ins may SSO.</p>"
                + "<p><a href=\"/oauth2/authorization/bank-auth\">Sign in</a></p>"
                + "</body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }



}