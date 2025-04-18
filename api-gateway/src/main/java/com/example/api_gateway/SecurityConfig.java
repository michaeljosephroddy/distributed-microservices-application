package com.example.api_gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/", "/login**", "/error").permitAll() // Allow unauthenticated access to login
                                                                             // and home
                        .pathMatchers("/api/customers/**").authenticated() // Protect /api/customers endpoint
                        .pathMatchers("/api/orders/**").authenticated() // Protect /api/customers endpoint
                        .anyExchange().authenticated())
                .oauth2Login(Customizer.withDefaults()); // Using Customizer with defaults for OAuth2 login

        return http.build();
    }
}
