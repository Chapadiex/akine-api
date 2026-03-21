package com.akine_api.infrastructure.security;

import java.util.UUID;

/**
 * Holds the active consultorio (tenant) for the current request thread.
 * Set by TenantResolutionFilter after validating the X-Consultorio-Id header.
 * Always cleared in a finally block to avoid leaking between requests.
 */
public final class TenantContext {

    private static final ThreadLocal<UUID> CURRENT = new ThreadLocal<>();

    private TenantContext() {}

    public static void set(UUID consultorioId) {
        CURRENT.set(consultorioId);
    }

    public static UUID get() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
