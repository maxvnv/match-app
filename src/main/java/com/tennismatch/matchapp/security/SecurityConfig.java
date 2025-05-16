package com.tennismatch.matchapp.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for now, common for stateless APIs
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless sessions
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/h2-console/**").permitAll() // Allow H2 console
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // Allow static resources
                .requestMatchers("/actuator/**").permitAll() // Allow actuator endpoints
                .anyRequest().permitAll() // Allow all other requests for now
            )
            // For H2 console to work with Spring Security, headers need to be configured
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin())); 

        return http.build();
    }
} 