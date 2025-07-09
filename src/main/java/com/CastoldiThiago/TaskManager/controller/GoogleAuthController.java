package com.CastoldiThiago.TaskManager.controller;

import com.CastoldiThiago.TaskManager.dto.GoogleLoginRequest;
import com.CastoldiThiago.TaskManager.dto.JwtResponse;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.repository.UserRepository;
import com.CastoldiThiago.TaskManager.security.JwtTokenProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    @Value("${google.clientId}")
    private String googleClientId;

    public GoogleAuthController(
            JwtTokenProvider jwtTokenProvider,
            UserRepository userRepository,
            GoogleIdTokenVerifier googleIdTokenVerifier) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.googleIdTokenVerifier = googleIdTokenVerifier;
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginConGoogle(@RequestBody GoogleLoginRequest request) {
        if (request.getIdToken() == null || request.getIdToken().isBlank()) {
            return ResponseEntity.badRequest().body("El idToken es obligatorio");
        }
        try {
            GoogleIdToken idToken = googleIdTokenVerifier.verify(request.getIdToken());

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                System.out.println("Token válido. Email extraído: " + email);

                User usuario = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            User nuevo = new User();
                            nuevo.setEmail(email);
                            nuevo.setName((String) payload.get("name"));
                            return userRepository.save(nuevo);
                        });

                String jwt = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getName());

                return ResponseEntity.ok(new JwtResponse(jwt));
            } else {
                System.out.println("Token inválido para el clientId: " + googleClientId);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error validando token de Google");
        }
    }
}