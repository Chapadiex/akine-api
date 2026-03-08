package com.akine_api.infrastructure.flyway;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;

import java.util.Arrays;
import java.util.Objects;

public final class FlywayLocalValidationRunner {

    private static final String DEFAULT_URL =
            "jdbc:h2:mem:akine_flyway;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDERING=HIGH;DB_CLOSE_DELAY=-1";

    private FlywayLocalValidationRunner() {
    }

    public static void main(String[] args) {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        System.getProperty("akine.flyway.local.url", DEFAULT_URL),
                        System.getProperty("akine.flyway.local.user", "sa"),
                        System.getProperty("akine.flyway.local.password", "")
                )
                .locations("classpath:db/migration")
                .load();

        var migrateResult = flyway.migrate();
        var versions = Arrays.stream(flyway.info().all())
                .map(MigrationInfo::getVersion)
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .toList();
        var currentVersion = String.valueOf(flyway.info().current().getVersion());

        if (migrateResult.migrationsExecuted != 23) {
            throw new IllegalStateException("Expected 23 migrations but executed " + migrateResult.migrationsExecuted);
        }
        if (!versions.contains("5.1") || !versions.contains("21.1")) {
            throw new IllegalStateException("Java migrations 5.1 and 21.1 must be visible in the local validation chain");
        }
        if (!"21.1".equals(currentVersion)) {
            throw new IllegalStateException("Expected current version 21.1 but was " + currentVersion);
        }

        flyway.validate();
        System.out.println("Validated local Flyway chain through version " + currentVersion);
    }
}
