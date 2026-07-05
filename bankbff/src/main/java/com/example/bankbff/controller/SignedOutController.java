package com.example.bankbff.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignedOutController {

    @GetMapping(value = "/signed-out", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> signedOut() {
        String html = "<!doctype html><html><head><meta charset=\"utf-8\"><title>Signed out</title></head><body>"
                + "<h1>Signed out</h1>"
                + "<p>You have signed out of the BFF. Your identity provider session remains so future sign-ins may SSO.</p>"
                + "<p><a href=\"/oauth2/authorization/bank-auth\">Sign in</a></p>"
                + "</body></html>";
        return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(html);
    }
}
