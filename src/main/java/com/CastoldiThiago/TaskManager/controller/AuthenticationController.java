package com.CastoldiThiago.TaskManager.controller;
import com.CastoldiThiago.TaskManager.dto.LoginRequest;
import com.CastoldiThiago.TaskManager.dto.ResendCodeRequest;
import com.CastoldiThiago.TaskManager.dto.VerificationRequest;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.service.AuthService;
import com.CastoldiThiago.TaskManager.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthService authService;
    private final UserService userService;

    public AuthenticationController(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Lógica de autenticación
            String token = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.ok(token);
        } catch (BadCredentialsException e) {
            // Devolver un 401 Unauthorized si las credenciales son incorrectas
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario o contraseña incorrectos.");
        } catch (RuntimeException e) {
            // Manejar otros errores como solicitudes inválidas (400 Bad Request)
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        try {
            // Llamar al servicio para registrar un nuevo usuario
            authService.register(user);
            return ResponseEntity.ok("Usuario registrado. Revisa tu correo para verificar tu cuenta.");
        } catch (ResponseStatusException e) {
            // Devolver solo el mensaje de error, sin incluir el código de estado
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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

    @PostMapping("/resend")
    public ResponseEntity<String> resendCode(@RequestBody ResendCodeRequest resendCodeRequest){
       userService.resendVerificationCode(resendCodeRequest.getEmail());
       return ResponseEntity.ok("Correo enviado exitosamente.");
    };
}

