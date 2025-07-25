package com.CastoldiThiago.TaskManager.controller;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TokenValidationController {

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String token = authorizationHeader.replace("Bearer ", "");

            RestTemplate restTemplate = new RestTemplate();
            String validationUrl = "https://www.googleapis.com/oauth2/v3/tokeninfo?access_token=" + token;

            ResponseEntity<Map> response = restTemplate.exchange(validationUrl, HttpMethod.GET, null, Map.class);

            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Token inválido");
        }
    }
}