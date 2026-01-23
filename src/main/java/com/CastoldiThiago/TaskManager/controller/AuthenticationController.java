package com.CastoldiThiago.TaskManager.controller;
import com.CastoldiThiago.TaskManager.dto.JwtResponse;
import com.CastoldiThiago.TaskManager.dto.LoginRequest;
import com.CastoldiThiago.TaskManager.dto.ResendCodeRequest;
import com.CastoldiThiago.TaskManager.dto.VerificationRequest;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.security.JwtTokenProvider;
import com.CastoldiThiago.TaskManager.security.TokenType;
import com.CastoldiThiago.TaskManager.service.AuthService;
import com.CastoldiThiago.TaskManager.service.UserService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthenticationController(AuthService authService, UserService userService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Autenticación y generación de tokens
            List<String> tokens = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
            String accessToken = tokens.get(0);
            String refreshToken = tokens.get(1);

            // Crear cookie HttpOnly con el refresh token
            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true) // Hacer true en producción con HTTPS
                    .path("/api/auth/refresh")
                    .maxAge(Duration.ofDays(7))
                    .build();

            // Devolver accessToken como JSON, y refreshToken como cookie
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new JwtResponse(accessToken));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Expira la cookie: maxAge = 0
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/auth/refresh")
                .maxAge(0) // <- elimina la cookie
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .body("Sesión cerrada correctamente.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            authService.register(user);
            return ResponseEntity.ok(Map.of("message", "Usuario registrado. Revisa tu correo para verificar tu cuenta."));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", Objects.requireNonNull(e.getReason())));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "Error al registrar usuario: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyCode(@RequestBody VerificationRequest request) {
        if (authService.verifyCode(request.getEmail(), request.getCode())) {
            return ResponseEntity.ok("Correo verificado exitosamente.");
        } else {
            return ResponseEntity.badRequest().body("El código de verificación es incorrecto.");
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        try {
            if (refreshToken == null) {
                throw new JwtException("Refresh token ausente");
            }

            jwtTokenProvider.validateToken(refreshToken);

            if (jwtTokenProvider.getTokenType(refreshToken) != TokenType.REFRESH) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("Token no es refresh");
            }

            String email = jwtTokenProvider.getEmail(refreshToken);
            String name = jwtTokenProvider.getName(refreshToken);

            String newAccessToken =
                    jwtTokenProvider.generateToken(email, name, TokenType.ACCESS);

            return ResponseEntity.ok(new JwtResponse(newAccessToken));

        } catch (JwtException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Refresh token inválido o expirado");
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<String> resendCode(@RequestBody ResendCodeRequest resendCodeRequest){
       userService.resendVerificationCode(resendCodeRequest.getEmail());
       return ResponseEntity.ok("Correo enviado exitosamente.");
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}

