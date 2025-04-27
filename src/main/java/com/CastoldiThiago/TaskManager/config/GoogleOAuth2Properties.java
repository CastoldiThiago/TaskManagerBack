package com.CastoldiThiago.TaskManager.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "google")
public class GoogleOAuth2Properties {

    // Getters y Setters
    private String clientId;
    private String clientSecret;
    private String redirectUri;

}