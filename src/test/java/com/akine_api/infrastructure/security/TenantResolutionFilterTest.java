package com.akine_api.infrastructure.security;

import com.akine_api.application.port.output.ConsultorioRepositoryPort;
import com.akine_api.application.port.output.UserRepositoryPort;
import com.akine_api.domain.model.User;
import com.akine_api.domain.model.UserStatus;
import com.akine_api.infrastructure.security.TenantContext;
import com.akine_api.infrastructure.security.TenantResolutionFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantResolutionFilterTest {

    @Mock ConsultorioRepositoryPort consultorioRepo;
    @Mock UserRepositoryPort userRepo;
    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock FilterChain filterChain;

    TenantResolutionFilter filter;

    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID CONSULTORIO_ID = UUID.randomUUID();
    private static final String EMAIL = "prof@test.com";

    @BeforeEach
    void setUp() {
        filter = new TenantResolutionFilter(userRepo, consultorioRepo);
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        TenantContext.clear();
    }

    private User activeUser() {
        return new User(USER_ID, EMAIL, "hash", "Prof", "User",
                null, UserStatus.ACTIVE, Instant.now());
    }

    private void authenticateAs(String email, String role) {
        var auth = new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority(role)));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    // ---- No header ----

    @Test
    void noHeader_passesThrough_noTenantSet() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(TenantContext.get()).isNull();
    }

    @Test
    void blankHeader_passesThrough() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn("   ");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    // ---- Unauthenticated ----

    @Test
    void headerPresent_unauthenticated_passesThrough() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn(CONSULTORIO_ID.toString());
        // SecurityContext has no authentication

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(TenantContext.get()).isNull();
    }

    // ---- ADMIN bypass ----

    @Test
    void headerPresent_adminUser_skipsValidation_noTenantSet() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn(CONSULTORIO_ID.toString());
        authenticateAs("admin@test.com", "ROLE_ADMIN");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(consultorioRepo, never()).findConsultorioIdsByUserId(any());
        assertThat(TenantContext.get()).isNull();
    }

    // ---- Valid tenant ----

    @Test
    void headerPresent_validMembership_setsTenantContext() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn(CONSULTORIO_ID.toString());
        authenticateAs(EMAIL, "ROLE_PROFESIONAL");
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        // TenantContext is cleared after the filter chain, so we can't assert get() here.
        // The point is that filterChain was invoked and no 403 was sent.
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void tenantContext_clearedAfterFilterChain() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn(CONSULTORIO_ID.toString());
        authenticateAs(EMAIL, "ROLE_PROFESIONAL_ADMIN");
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));

        // Capture context inside the chain
        UUID[] capturedInsideChain = new UUID[1];
        doAnswer(inv -> {
            capturedInsideChain[0] = TenantContext.get();
            return null;
        }).when(filterChain).doFilter(request, response);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(capturedInsideChain[0]).isEqualTo(CONSULTORIO_ID);
        assertThat(TenantContext.get()).isNull(); // cleared after
    }

    // ---- Invalid / unauthorized ----

    @Test
    void headerPresent_invalidUuid_returns403() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn("not-a-uuid");
        authenticateAs(EMAIL, "ROLE_PROFESIONAL");

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void headerPresent_notMember_returns403() throws Exception {
        UUID otherConsultorio = UUID.randomUUID();
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn(otherConsultorio.toString());
        authenticateAs(EMAIL, "ROLE_PROFESIONAL");
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void headerPresent_userNotFoundInDB_returns403() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn(CONSULTORIO_ID.toString());
        authenticateAs(EMAIL, "ROLE_PROFESIONAL");
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.empty());

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void tenantContext_clearedEvenIfChainThrows() throws Exception {
        when(request.getHeader(TenantResolutionFilter.HEADER)).thenReturn(CONSULTORIO_ID.toString());
        authenticateAs(EMAIL, "ROLE_PROFESIONAL");
        when(userRepo.findByEmail(EMAIL)).thenReturn(Optional.of(activeUser()));
        when(consultorioRepo.findConsultorioIdsByUserId(USER_ID)).thenReturn(List.of(CONSULTORIO_ID));
        doThrow(new RuntimeException("downstream failure")).when(filterChain).doFilter(any(), any());

        try {
            filter.doFilterInternal(request, response, filterChain);
        } catch (RuntimeException ignored) {
        }

        assertThat(TenantContext.get()).isNull();
    }
}
