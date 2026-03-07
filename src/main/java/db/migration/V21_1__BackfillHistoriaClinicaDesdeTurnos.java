package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class V21_1__BackfillHistoriaClinicaDesdeTurnos extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String selectSql = """
                SELECT t.id,
                       t.consultorio_id,
                       t.paciente_id,
                       t.profesional_id,
                       t.box_id,
                       t.fecha_hora_inicio,
                       t.motivo_consulta,
                       t.notas,
                       t.creado_por_user_id,
                       t.created_at,
                       t.updated_at
                FROM turnos t
                WHERE t.paciente_id IS NOT NULL
                  AND t.profesional_id IS NOT NULL
                  AND t.estado IN ('COMPLETADO', 'EN_CURSO')
                  AND NOT EXISTS (
                    SELECT 1
                    FROM historia_clinica_sesiones hc
                    WHERE hc.turno_id = t.id
                  )
                """;
        String insertSql = """
                INSERT INTO historia_clinica_sesiones (
                    id,
                    consultorio_id,
                    paciente_id,
                    profesional_id,
                    turno_id,
                    box_id,
                    fecha_atencion,
                    estado,
                    tipo_atencion,
                    motivo_consulta,
                    resumen_clinico,
                    subjetivo,
                    objetivo,
                    evaluacion,
                    plan,
                    origen_registro,
                    created_by_user_id,
                    updated_by_user_id,
                    closed_by_user_id,
                    created_at,
                    updated_at,
                    closed_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement select = context.getConnection().prepareStatement(selectSql);
             ResultSet rs = select.executeQuery();
             PreparedStatement insert = context.getConnection().prepareStatement(insertSql)) {

            while (rs.next()) {
                UUID turnoId = rs.getObject("id", UUID.class);
                UUID consultorioId = rs.getObject("consultorio_id", UUID.class);
                UUID pacienteId = rs.getObject("paciente_id", UUID.class);
                UUID profesionalId = rs.getObject("profesional_id", UUID.class);
                UUID boxId = rs.getObject("box_id", UUID.class);
                Timestamp fechaAtencion = rs.getTimestamp("fecha_hora_inicio");
                String motivoConsulta = rs.getString("motivo_consulta");
                String notas = rs.getString("notas");
                UUID actorUserId = rs.getObject("creado_por_user_id", UUID.class);
                Timestamp createdAt = rs.getTimestamp("created_at");
                Timestamp updatedAt = rs.getTimestamp("updated_at");
                Timestamp closedAt = updatedAt != null ? updatedAt : Timestamp.from(Instant.now());

                insert.setObject(1, UUID.randomUUID());
                insert.setObject(2, consultorioId);
                insert.setObject(3, pacienteId);
                insert.setObject(4, profesionalId);
                insert.setObject(5, turnoId);
                insert.setObject(6, boxId);
                insert.setTimestamp(7, fechaAtencion);
                insert.setString(8, "CERRADA");
                insert.setString(9, "SEGUIMIENTO");
                insert.setString(10, motivoConsulta);
                insert.setString(11, firstNonBlank(motivoConsulta, notas, "Sesion migrada desde turnos"));
                insert.setString(12, notas);
                insert.setString(13, null);
                insert.setString(14, "Registro migrado desde turno historico.");
                insert.setString(15, null);
                insert.setString(16, "BACKFILL_TURNO");
                insert.setObject(17, actorUserId);
                insert.setObject(18, actorUserId);
                insert.setObject(19, actorUserId);
                insert.setTimestamp(20, createdAt != null ? createdAt : Timestamp.from(Instant.now()));
                insert.setTimestamp(21, updatedAt != null ? updatedAt : Timestamp.from(Instant.now()));
                insert.setTimestamp(22, closedAt);
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }

    private String firstNonBlank(String primary, String secondary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        if (secondary != null && !secondary.isBlank()) {
            return secondary;
        }
        return fallback;
    }
}
