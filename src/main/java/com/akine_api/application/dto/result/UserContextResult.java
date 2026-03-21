package com.akine_api.application.dto.result;

import java.util.List;

/**
 * Contexto profesional o de empleado del usuario autenticado.
 * Solo incluye los campos propios del rol — no duplica nombre/email ya presentes en UserProfileResult.
 */
public record UserContextResult(
        /** "PROFESIONAL", "EMPLEADO" o "NONE" */
        String tipo,

        // ── Profesional ──────────────────────────────────────────────────────
        String matricula,
        List<String> especialidades,
        String nroDocumento,
        String domicilio,

        // ── Empleado ─────────────────────────────────────────────────────────
        String cargo,
        String dni
) {
    public static UserContextResult none() {
        return new UserContextResult("NONE", null, null, null, null, null, null);
    }
}
