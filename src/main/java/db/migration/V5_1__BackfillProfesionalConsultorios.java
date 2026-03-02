package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class V5_1__BackfillProfesionalConsultorios extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String selectSql = "SELECT id, consultorio_id FROM profesionales WHERE consultorio_id IS NOT NULL";
        String insertSql = """
                INSERT INTO profesional_consultorios (id, profesional_id, consultorio_id, activo, created_at)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement select = context.getConnection().prepareStatement(selectSql);
             ResultSet rs = select.executeQuery();
             PreparedStatement insert = context.getConnection().prepareStatement(insertSql)) {

            while (rs.next()) {
                UUID profesionalId = rs.getObject("id", UUID.class);
                UUID consultorioId = rs.getObject("consultorio_id", UUID.class);
                insert.setObject(1, UUID.randomUUID());
                insert.setObject(2, profesionalId);
                insert.setObject(3, consultorioId);
                insert.setBoolean(4, true);
                insert.setTimestamp(5, Timestamp.from(Instant.now()));
                insert.addBatch();
            }
            insert.executeBatch();
        }
    }
}
