package com.CastoldiThiago.TaskManager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = User.USERS_TABLE)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    public static final String USERS_TABLE = "users";
    private static final int USERNAME_MAX_LENGTH = 50;
    private static final int PASSWORD_MAX_LENGTH = 100;
    private static final int EMAIL_MAX_LENGTH = 100;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = true)
    private String password;

    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "Debe ingresar un email válido")
    @Size(max = EMAIL_MAX_LENGTH, message = "El email debe tener máximo {max} caracteres")
    @Column(nullable = false, unique = true, length = EMAIL_MAX_LENGTH)
    private String email;

    private Boolean enabled;
    private String verificationCode;

    public Boolean isEnabled() {
        return enabled;
    }
}
