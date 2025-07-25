package com.CastoldiThiago.TaskManager.exception;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Clase que maneja los intentos de acceso no autorizados (error 401).
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException)
            throws IOException, ServletException {
        // Enviar un error 401 Unauthorized cuando falla la autenticación
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No estás autorizado para acceder a este recurso.");
    }
}

