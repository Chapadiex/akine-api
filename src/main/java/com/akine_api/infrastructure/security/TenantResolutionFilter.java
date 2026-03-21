package com.akine_api.infrastructure.security;

import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Reads the X-Consultorio-Id header and validates that the authenticated user
 * has an active membership in that consultorio.
 *
 * Permissive behaviour:
 * - Header absent → no tenant set, request continues normally.
 * - ROLE_ADMIN users → no validation, request continues with no tenant restriction.
 * - Unauthenticated requests → skipped (JWT filter / security rules handle 401).
 *
 * If the header is present and validation fails → 403 Forbidden.
 */
@Component
public class TenantResolutionFilter extends OncePerRequestFilter {

    public static final String HEADER = "X-Consultorio-Id";

    private final UserRepositoryPort userRepo;
    private final ConsultorioRepositoryPort consultorioRepo;

    public TenantResolutionFilter(UserRepositoryPort userRepo,
                                  ConsultorioRepositoryPort consultorioRepo) {
        this.userRepo = userRepo;
        this.consultorioRepo = consultorioRepo;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String rawHeader = request.getHeader(HEADER);

        if (rawHeader == null || rawHeader.isBlank()) {
            // No tenant header — permissive, continue as-is
            filterChain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            // Not authenticated yet — let security rules handle it
            filterChain.doFilter(request, response);
            return;
        }

        // ADMIN users bypass tenant validation
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        if (isAdmin) {
            filterChain.doFilter(request, response);
            return;
        }

        UUID consultorioId;
        try {
            consultorioId = UUID.fromString(rawHeader.trim());
        } catch (IllegalArgumentException e) {
            sendForbidden(response, "X-Consultorio-Id inválido");
            return;
        }

        // Resolve user from SecurityContext principal (email)
        String email = auth.getName();
        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            sendForbidden(response, "Usuario no encontrado");
            return;
        }

        UUID userId = userOpt.get().getId();
        List<UUID> allowedIds = consultorioRepo.findConsultorioIdsByUserId(userId);

        if (!allowedIds.contains(consultorioId)) {
            sendForbidden(response, "Sin acceso al consultorio indicado");
            return;
        }

        TenantContext.set(consultorioId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    private void sendForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                "{\"code\":\"TENANT_ACCESS_DENIED\",\"message\":\"" + message + "\"}");
    }
}
