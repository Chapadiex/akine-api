package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class V22_1__BackfillHistoriaClinicaLegajos extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String selectSql = """
                SELECT consultorio_id, paciente_id
                FROM (
                    SELECT consultorio_id, paciente_id
                    FROM historia_clinica_sesiones
                    UNION
                    SELECT consultorio_id, paciente_id
                    FROM historia_clinica_diagnosticos
                ) clinical_pairs
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM historia_clinica_legajos l
                    WHERE l.consultorio_id = clinical_pairs.consultorio_id
                      AND l.paciente_id = clinical_pairs.paciente_id
                )
                """;
        String insertSql = """
                INSERT INTO historia_clinica_legajos (
                    id,
                    consultorio_id,
                    paciente_id,
                    created_by_user_id,
                    updated_by_user_id,
                    created_at,
                    updated_at
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement select = context.getConnection().prepareStatement(selectSql);
             ResultSet rs = select.executeQuery();
             PreparedStatement insert = context.getConnection().prepareStatement(insertSql)) {

            Set<String> seen = new HashSet<>();
            Timestamp now = Timestamp.from(Instant.now());

            while (rs.next()) {
                UUID consultorioId = rs.getObject("consultorio_id", UUID.class);
                UUID pacienteId = rs.getObject("paciente_id", UUID.class);
                String key = consultorioId + "|" + pacienteId;
                if (!seen.add(key)) {
                    continue;
                }
                insert.setObject(1, UUID.randomUUID());
                insert.setObject(2, consultorioId);
                insert.setObject(3, pacienteId);
                insert.setObject(4, null);
                insert.setObject(5, null);
                insert.setTimestamp(6, now);
                insert.setTimestamp(7, now);
                insert.addBatch();
            }

            insert.executeBatch();
        }
    }
}
