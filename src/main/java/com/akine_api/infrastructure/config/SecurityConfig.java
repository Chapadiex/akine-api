package com.akine_api.infrastructure.config;

import com.akine_api.infrastructure.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin()) // H2 console en dev
            )
            .authorizeHttpRequests(auth -> auth
                // Auth endpoints públicos
                .requestMatchers(HttpMethod.POST,
                        "/api/v1/subscriptions",
                        "/api/v1/subscriptions/draft",
                        "/api/v1/subscriptions/*/submit-approval",
                        "/api/v1/auth/activate",
                        "/api/v1/auth/resend-activation",
                        "/api/v1/auth/login",
                        "/api/v1/auth/refresh"
                ).permitAll()
                .requestMatchers(HttpMethod.PATCH,
                        "/api/v1/subscriptions/*/owner",
                        "/api/v1/subscriptions/*/company",
                        "/api/v1/subscriptions/*/clinic",
                        "/api/v1/subscriptions/*/payment-simulate"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/subscriptions/status/*").permitAll()
                // OpenAPI / Swagger (solo interno — source of truth del contrato)
                .requestMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                ).permitAll()
                // H2 console solo en dev
                .requestMatchers("/h2-console/**").permitAll()
                // Admin
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/consultorios/*/historia-clinica/**").hasAnyRole("ADMIN", "PROFESIONAL_ADMIN", "PROFESIONAL")
                .requestMatchers(
                        "/api/v1/consultorios/*/pacientes/*/360/historia-clinica",
                        "/api/v1/consultorios/*/pacientes/*/360/diagnosticos",
                        "/api/v1/consultorios/*/pacientes/*/360/atenciones"
                ).hasAnyRole("ADMIN", "PROFESIONAL_ADMIN", "PROFESIONAL")
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((req, res, e) -> {
                    res.setStatus(401);
                    res.setContentType("application/json");
                    res.getWriter().write(
                            "{\"code\":\"UNAUTHORIZED\",\"message\":\"Autenticaci\\u00f3n requerida\"}");
                })
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:4200", "https://*.akine.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
