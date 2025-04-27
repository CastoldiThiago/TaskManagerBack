package com.CastoldiThiago.TaskManager.repository;

import com.CastoldiThiago.TaskManager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Buscar un usuario por username
    Optional<User> findByUsername(String username);
    // Buscar usuario por email
    Optional<User> findByEmail(String email);
    // Validar si un username o email ya existen
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}
