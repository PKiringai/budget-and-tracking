package coop.bank.budget_tracking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Security Configuration for Budget & Expense Tracker API
 *
 * NOTE: This is a basic configuration for development.
 * In production, you should:
 * 1. Enable JWT/OAuth2 authentication
 * 2. Implement proper role-based access control
 * 3. Use HTTPS only
 */
//@Configuration
//@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//                .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless API
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                )
//                .authorizeHttpRequests(auth -> auth
//                        // Public endpoints
//                        .requestMatchers(
//                                "/api-docs/**",
//                                "/swagger-ui/**",
//                                "/swagger-ui.html",
//                                "/actuator/health",
//                                "/actuator/info"
//                        ).permitAll()
//
//                        // All other API endpoints require authentication
//                        .requestMatchers("/api/v1/**").authenticated()
//
//                        .anyRequest().authenticated()
//                );
//
//        // TODO: Add JWT authentication filter in production
//        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//
//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // TODO: Configure from application.yml in production
//        configuration.setAllowedOrigins(Arrays.asList(
//                "http://localhost:3000",
//                "http://localhost:4200",
//                "http://localhost:8080"
//        ));
//
//        configuration.setAllowedMethods(Arrays.asList(
//                "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
//        ));
//
//        configuration.setAllowedHeaders(Arrays.asList(
//                "Authorization",
//                "Content-Type",
//                "X-Requested-With",
//                "Accept",
//                "Origin"
//        ));
//
//        configuration.setExposedHeaders(Arrays.asList(
//                "X-Total-Count",
//                "X-Page-Number",
//                "X-Page-Size"
//        ));
//
//        configuration.setAllowCredentials(true);
//        configuration.setMaxAge(3600L);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
}
