package com.CastoldiThiago.TaskManager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationTime; // Tiempo de expiración en milisegundos

    // Constructor que carga los valores desde application.properties
    public JwtTokenProvider(@Value("${jwt.secret}") String secret,
                            @Value("${jwt.expiration}") long expiration) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        if (decodedKey.length < 32) { // Verifica el tamaño (256 bits = 32 bytes)
            throw new IllegalArgumentException("El valor de jwt.secret no cumple con el tamaño mínimo requerido de 256 bits.");
        }
        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
        this.expirationTime = expiration;
    }

    /**
     * Genera un token JWT para un usuario proporcionado.
     *
     * @param username El nombre de usuario que se incluirá en el token.
     * @return Token JWT generado.
     */
    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(username) // El "subject" es el nombre de usuario
                .setIssuedAt(now) // Fecha actual
                .setExpiration(expiryDate) // Fecha de expiración
                .signWith(secretKey, SignatureAlgorithm.HS512) // Firma con clave y algoritmo
                .compact(); // Generar el token
    }

    /**
     * Valida el token JWT asegurándose de que es correcto y no ha expirado.
     *
     * @param token El token JWT a validar.
     * @return true si el token es válido, de lo contrario lanza una excepción.
     */
    public boolean validateToken(String token) {
        try {
            // Intenta validar el token
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            // Reemplaza el uso de SignatureException con SecurityException
            System.out.println("Error en la firma del JWT: " + ex.getMessage());
        } catch (MalformedJwtException ex) {
            System.out.println("El token JWT está mal formado: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            System.out.println("El token JWT ha expirado: " + ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            System.out.println("El token JWT no es compatible: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.out.println("El token JWT está vacío o es nulo: " + ex.getMessage());
        }
        return false;
    }

    /**
     * Extrae el nombre de usuario (subject) del token JWT.
     *
     * @param token El token JWT.
     * @return El nombre de usuario contenido en el token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject(); // El "subject" contiene el nombre de usuario
    }
}

