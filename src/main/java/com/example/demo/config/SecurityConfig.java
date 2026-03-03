package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth

                        // ✅ PUBLIC APIs
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login",
                                "/api/users/verify",
                                "/api/users/send-otp")
                        .permitAll()

                        // ✅ Static frontend files
                        .requestMatchers(
                                "/",
                                "/**/*.html",
                                "/css/**",
                                "/js/**")
                        .permitAll()

                        // ✅ ADMIN APIs
                        .requestMatchers(HttpMethod.GET, "/api/users")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/users/**")
                        .hasRole("ADMIN")

                        // ✅ FACTORY can CREATE factory
                        .requestMatchers(HttpMethod.POST, "/api/factories")
                        .hasRole("FACTORY")

                        // ✅ ADMIN & FACTORY & CLIENT can VIEW factories
                        .requestMatchers(HttpMethod.GET, "/api/factories/**", "/api/factories")
                        .hasAnyRole("ADMIN", "FACTORY", "CLIENT")

                        // ✅ Everything else must be authenticated
                        .anyRequest().authenticated())

                // ✅ JWT Filter
                .addFilterBefore(jwtFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*")); // Allow React on any local port
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}