package com.CastoldiThiago.TaskManager.service;

import com.CastoldiThiago.TaskManager.model.PasswordResetToken;
import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.repository.PasswordResetTokenRepository;
import com.CastoldiThiago.TaskManager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    public void generateResetToken(String email) {
        User user = userService.findByEmail(email);
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpirationDate(LocalDateTime.now().plusHours(1));

        tokenRepository.save(resetToken);

        String resetLink = "https://taskmanagerfront-absm.onrender.com/reset-password?token=" + token;
        emailService.sendEmail(user.getEmail(), "Restablecimiento de contraseña", "Haz clic en el enlace para restablecer tu contraseña: " + resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "No existe el token"));
        if  (resetToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Token expirado.");
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        tokenRepository.delete(resetToken);
    }
}
