package com.CastoldiThiago.TaskManager.config;

import com.CastoldiThiago.TaskManager.exception.JwtAuthenticationEntryPoint;
import com.CastoldiThiago.TaskManager.filter.UnifiedAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final UnifiedAuthenticationFilter unifiedAuthenticationFilter; // Nuevo filtro unificado
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    public SecurityConfig(UnifiedAuthenticationFilter unifiedAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          CustomOAuth2SuccessHandler customOAuth2SuccessHandler) {
        this.unifiedAuthenticationFilter = unifiedAuthenticationFilter; // Inyectamos el nuevo filtro
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.customOAuth2SuccessHandler = customOAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Habilitar CORS
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF para APIs REST
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/**", "/", "/oauth2/**").permitAll() // Permitir rutas públicas
                        .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll() // Permitir solicitudes OPTIONS para CORS
                        .anyRequest().authenticated() // Proteger todas las demás rutas
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint) // Manejo de errores de autenticación
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No usar sesiones
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customOAuth2SuccessHandler) // Manejo de éxito en OAuth2
                );

        // Reemplazar el filtro anterior con el filtro unificado
        http.addFilterBefore(unifiedAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); // 1 hora de caché para solicitudes preflight

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
