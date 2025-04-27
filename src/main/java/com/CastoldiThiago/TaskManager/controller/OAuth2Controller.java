package com.CastoldiThiago.TaskManager.controller;

import com.CastoldiThiago.TaskManager.config.GoogleOAuth2Properties;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    private final GoogleOAuth2Properties googleOAuth2Properties;

    public OAuth2Controller(GoogleOAuth2Properties googleOAuth2Properties) {
        this.googleOAuth2Properties = googleOAuth2Properties;
    }
    @GetMapping("/callback/google")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        try {
            // Intercambiar el código de autorización por un token de acceso
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(code, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://oauth2.googleapis.com/token", request, Map.class);

            // Obtener el token de acceso
            String accessToken = (String) response.getBody().get("access_token");

            // Redirigir al frontend con el token
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "http://localhost:5173/oauth-success?token=" + accessToken)
                    .build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar la autenticación");
        }
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(String code, HttpHeaders headers) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleOAuth2Properties.getClientId());
        params.add("client_secret", googleOAuth2Properties.getClientSecret());
        params.add("redirect_uri", googleOAuth2Properties.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        return request;
    }
}