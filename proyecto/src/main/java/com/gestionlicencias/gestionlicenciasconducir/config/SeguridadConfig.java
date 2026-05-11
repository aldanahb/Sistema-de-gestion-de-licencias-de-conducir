package com.gestionlicencias.gestionlicenciasconducir.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import io.jsonwebtoken.SignatureAlgorithm;

@Configuration
public class SeguridadConfig {

    // En algún lugar centralizado (por ejemplo, en tu clase |de configuración):
    private static final SecretKey JWT_SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Bean
    public SecretKey getJwtSecretKey() {
        return JWT_SECRET_KEY;
    }

    // Bean para codificar contraseñas con BCrypt
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configuración de seguridad HTTP
    @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                // Permitir todas las solicitudes sin autenticación
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                // Deshabilitar protección CSRF
                .csrf().disable()
                // Deshabilitar formulario de login (sin autenticación)
                .formLogin().disable();

            return http.build();
        }
}


