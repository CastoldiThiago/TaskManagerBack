package com.CastoldiThiago.TaskManager.util;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class KeyGenerator {

    public static void main(String[] args) {
        // Generar una clave segura para el algoritmo HMAC-SHA512
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        String base64EncodedKey = Base64.getEncoder().encodeToString(key.getEncoded());

        // Imprime la clave generada en base64
        System.out.println("Generated Key: " + base64EncodedKey);
    }
}

