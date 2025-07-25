package com.CastoldiThiago.TaskManager.filter;

import com.CastoldiThiago.TaskManager.security.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class UnifiedAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public UnifiedAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        // Verifica que el header esté presente y tenga el formato correcto
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // Remover "Bearer "

        try {
            if (isJwtToken(token)) {
                // Validar token JWT
                handleJwtToken(token, request);
            }
        } catch (ExpiredJwtException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expirado. Realiza login nuevamente.");
            return;
        } catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Token no válido.");
            return;
        }

        // Continúa con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    private boolean isJwtToken(String token) {
        // Lógica para determinar si el token es un JWT
        // Por ejemplo, los tokens JWT suelen tener tres partes separadas por puntos (header.payload.signature)
        return token.split("\\.").length == 3;
    }

    private void handleJwtToken(String token, HttpServletRequest request) {
        String email = null;

        if (jwtTokenProvider.validateToken(token)) {
            email = jwtTokenProvider.getEmailFromToken(token);
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    email, null, Collections.emptyList());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}

