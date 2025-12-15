package com.example.keycloak.config;

import org.keycloak.authorization.client.AuthzClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${keycloak.auth-server-url}")
    private String authServerUrl;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.resource}")
    private String resource;
    @Value("${keycloak.credentials.secret}")
    private String secret;

    @Bean
    public AuthzClient authzClient() {
        String config = "{" +
                "  \"realm\": \"" + realm + "\"," +
                "  \"auth-server-url\": \"" + authServerUrl + "\"," +
                "  \"resource\": \"" + resource + "\"," +
                "  \"credentials\": {" +
                "    \"secret\": \"" + secret + "\"" +
                "  }" +
                "}";

        InputStream configStream = new ByteArrayInputStream(config.getBytes());
        return AuthzClient.create(configStream);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/products/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> {})
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}