package com.CastoldiThiago.TaskManager.controller;

import com.CastoldiThiago.TaskManager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account") // O la ruta que prefieras
public class AccountController {

    @Autowired
    private UserService userService;

    @DeleteMapping
    public ResponseEntity<String> deleteMyAccount(Authentication authentication) {
        // Obtenemos el email del token JWT o sesión actual
        String email = authentication.getName();

        try {
            userService.deleteAccountByEmail(email);
            return ResponseEntity.ok("Tu cuenta y todos tus datos han sido eliminados correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar la cuenta: " + e.getMessage());
        }
    }
}
