package com.CastoldiThiago.TaskManager.service;

import com.CastoldiThiago.TaskManager.model.User;
import com.CastoldiThiago.TaskManager.repository.UserRepository;
import com.CastoldiThiago.TaskManager.util.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder; // Para hashear contraseñas
    @Autowired
    private EmailService emailService;

    // Constructor para inyectar las dependencias
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Instanciamos el codificador
    }


    /**
     * Registrar un nuevo usuario.
     *
     * @param user Un nuevo usuario a registrar.
     * @return El usuario guardado.
     * @throws IllegalArgumentException si el username o email ya existen.
     */
    public void registerUser(User user) {

        if (userRepository.existsByEmail(user.getEmail())) {
            // Devolver un 409 Conflict si el correo electrónico ya está en uso
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El correo electrónico ya existe.");
        }

        if (!EmailValidator.isValidEmail(user.getEmail())) {
            // Devolver un 400 Bad Request si el formato del correo electrónico es inválido
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El formato del correo electrónico es inválido.");
        }

        // Hashear la contraseña antes de guardarla en la base de datos
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generar un código de verificación
        String verificationCode = String.format("%06d", new Random().nextInt(999999));

        // Guardar el usuario con el código de verificación
        user.setVerificationCode(verificationCode);
        user.setEnabled(false); // El usuario no está habilitado hasta que verifique su correo
        userRepository.save(user);

        // Enviar el correo de verificación
        emailService.sendVerificationEmail(user.getEmail(), verificationCode);
    }

    /**
     * Validar credenciales para login.
     *
     * @param email El nombre de usuario.
     * @param password La contraseña ingresada.
     * @return true si las credenciales son correctas, false si no lo son.
     */
    public boolean validateCredentials(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return false;
        }

        User user = userOptional.get();
        // Comparar la contraseña hasheada con la ingresada usando BCrypt
        return passwordEncoder.matches(password, user.getPassword());
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "El correo no existe"));
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Usuario no encontrado."));

        if (user.getVerificationCode().equals(code)) {
            user.setEnabled(true); // Habilitar al usuario
            user.setVerificationCode(null); // Limpiar el código de verificación
            userRepository.save(user);
            return true;
        } else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El codigo de verificacion es incorrecto");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuario no guardado.");
        }
        User user = userOptional.get();
        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationCode());
    };

    public void modifyPassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Usuario no encontrado.");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "contraseña incorrecta");
        }else if (!passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La contraseña no puede ser igual a la anterior");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
    };
}
