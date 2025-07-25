package com.CastoldiThiago.TaskManager.controller;

import com.CastoldiThiago.TaskManager.dto.GoogleLoginRequest;
import com.CastoldiThiago.TaskManager.dto.JwtResponse;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.repository.UserRepository;
import com.CastoldiThiago.TaskManager.security.JwtTokenProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;


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

                User usuario = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            User nuevo = new User();
                            nuevo.setEmail(email);
                            nuevo.setName((String) payload.get("name"));
                            nuevo.setEnabled(true); // ✔️ Importante si tenés lógica de validación por mail
                            return userRepository.save(nuevo);
                        });

                // Generar tokens con tipo
                String accessToken = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getName(), "access");
                String refreshToken = jwtTokenProvider.generateToken(usuario.getEmail(), usuario.getName(), "refresh");

                // Guardar refresh token en una cookie HttpOnly
                ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                        .httpOnly(true)
                        .path("/api/auth/refresh")
                        .maxAge(Duration.ofDays(7))
                        .build();

                // Devolver accessToken como JSON
                return ResponseEntity.ok()
                        .header(HttpHeaders.SET_COOKIE, cookie.toString())
                        .body(new JwtResponse(accessToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de Google inválido");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error validando token de Google");
        }
    }
}