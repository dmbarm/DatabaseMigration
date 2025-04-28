package org.migrationtool.main;

import org.migrationtool.exceptions.MigrationProcessingException;

import java.sql.Connection;

@FunctionalInterface
public interface MigrationProcessor {
    void process(Migration migration, Connection connection, MigrationHistory migrationHistory) throws MigrationProcessingException;
    default boolean shouldProcess(Migration migration, Connection connection, MigrationHistory migrationHistory) throws MigrationProcessingException {
        return true;
    }
}
