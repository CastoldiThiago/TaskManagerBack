package com.CastoldiThiago.TaskManager.service;

import com.CastoldiThiago.TaskManager.dto.TaskListDTO;
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
    @Autowired
    private TaskListService taskListService;

    // Constructor para inyectar las dependencias
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder(); // Instanciamos el codificador
    }


    /**
     * Registrar un nuevo usuario.
     *
     * @param user Un nuevo usuario a registrar.
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


    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found"));
    }


    public boolean verifyCode(String email, String code) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Usuario no encontrado."));

        if (user.getVerificationCode().equals(code)) {
            user.setEnabled(true); // Habilitar al usuario
            user.setVerificationCode(null); // Limpiar el código de verificación
            userRepository.save(user);
            // crearle una lista inicial
            TaskListDTO firstTaskList = new TaskListDTO(1,"First List", "This is your first tasks list");
            taskListService.createList(firstTaskList, user);
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
    }

}
