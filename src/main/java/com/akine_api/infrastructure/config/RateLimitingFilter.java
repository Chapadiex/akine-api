package com.akine_api.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate limiter básico en memoria para el endpoint de login.
 * Límite: 5 intentos por IP en una ventana de 60 segundos.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60_000L;
    private static final String LOGIN_PATH = "/api/v1/auth/login";

    private record Window(AtomicInteger count, Instant resetAt) {}

    private final Map<String, Window> attempts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain chain)
            throws ServletException, IOException {

        if (!LOGIN_PATH.equals(request.getRequestURI()) || !"POST".equals(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        String ip = getClientIp(request);
        Instant now = Instant.now();

        attempts.compute(ip, (k, w) -> {
            if (w == null || now.isAfter(w.resetAt())) {
                return new Window(new AtomicInteger(1), now.plusMillis(WINDOW_MS));
            }
            w.count().incrementAndGet();
            return w;
        });

        Window window = attempts.get(ip);
        if (window.count().get() > MAX_ATTEMPTS) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"RATE_LIMIT_EXCEEDED\",\"message\":\"Demasiados intentos. Intentá en 60 segundos.\"}");
            return;
        }

        chain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
