package com.CastoldiThiago.TaskManager.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_NAME = "name";
    private static final String CLAIM_ROLES = "roles";

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long accessExpiration
    ) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        if (decodedKey.length < 32) {
            throw new IllegalArgumentException(
                    "jwt.secret debe tener al menos 256 bits"
            );
        }

        this.secretKey = Keys.hmacShaKeyFor(decodedKey);
        this.accessTokenExpiration = accessExpiration;
        this.refreshTokenExpiration = 7 * 24 * 60 * 60 * 1000L;
    }

    /* ===================== TOKEN GENERATION ===================== */

    public String generateToken(String email, String name, TokenType type) {
        Date now = new Date();
        Date expiry = new Date(
                now.getTime() + (
                        type == TokenType.ACCESS
                                ? accessTokenExpiration
                                : refreshTokenExpiration
                )
        );

        return Jwts.builder()
                .setSubject(email)
                .claim(CLAIM_NAME, name)
                .claim(CLAIM_TYPE, type.name())
                .claim(CLAIM_ROLES, List.of("ROLE_USER"))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ===================== VALIDATION ===================== */

    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void validateToken(String token) throws JwtException {
        parseClaims(token); // si falla → excepción
    }

    /* ===================== EXTRACTION ===================== */

    public String getEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String getName(String token) {
        return parseClaims(token).get(CLAIM_NAME, String.class);
    }

    public TokenType getTokenType(String token) {
        return TokenType.valueOf(
                parseClaims(token).get(CLAIM_TYPE, String.class)
        );
    }

    public List<GrantedAuthority> getAuthorities(String token) {
        List<String> roles = parseClaims(token).get("roles", List.class);

        return roles.stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                .toList();
    }
}

