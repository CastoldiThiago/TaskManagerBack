package com.CastoldiThiago.TaskManager.service;

import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public List<String> login(String email, String password) {
        // Buscar el usuario por el mail
        User user = userService.findByEmail(email);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            // Lanzar una excepción con un código 401 si el usuario no existe o la contraseña no es correcta
            throw new BadCredentialsException("invalid email or password");
        }
        if (!user.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is not verified");
        }

        // Generar token JWT
        String accessToken = jwtTokenProvider.generateToken(user.getEmail(), user.getName(), "access");
        String refreshToken = jwtTokenProvider.generateToken(user.getEmail(), user.getPassword(), "refresh");
        List<String> tokens = new ArrayList<>();
        tokens.add(accessToken);
        tokens.add(refreshToken);
        return tokens;
    }

    public void register(User user) {
        userService.registerUser(user);
    }
    public boolean verifyCode(String email, String code) {
        return userService.verifyCode(email, code);
    }
}

