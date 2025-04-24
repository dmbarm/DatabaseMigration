package org.migrationtool.main;

import java.sql.Connection;

@FunctionalInterface
public interface MigrationProcessor {
    void process(Migration migration, Connection connection, MigrationHistory migrationHistory) throws Exception;
    default boolean shouldProcess(Migration migration, Connection connection, MigrationHistory migrationHistory) throws Exception {
        return true;
    }
}
