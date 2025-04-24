package org.migrationtool.main;

import org.migrationtool.utils.ConfigLoader;
import org.migrationtool.utils.LoggerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MigrationHistory {
    private static final Logger logger = LoggerFactory.getLogger(MigrationHistory.class);

    private static final String SELECT_MIGRATION_CHECKSUM = "SELECT COUNT(*) FROM Migration_Table WHERE Checksum = '%s'";
    private static final String DELETE_MIGRATION_CHECKSUM = "DELETE FROM Migration_Table WHERE Checksum = '%s'";

    public boolean alreadyExecuted(Migration migration, Connection connection) {
        String query = String.format(SELECT_MIGRATION_CHECKSUM, migration.getChecksum());

        logger.debug("Checking if migration ID={} has already been executed.", migration.getId());

        try {
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            if (resultSet.next()) {
                boolean result =  resultSet.getInt(1) > 0;
                logger.debug("Migration ID={} execution status: {}", migration.getId(), result ? "ALREADY EXECUTED" : "NOT EXECUTED");
                return result;
            }

        } catch (SQLException e) {
            logger.error("Failed to check if migration history for ID={}: {}", migration.getId(), e.getMessage());
            throw new RuntimeException("Checking migration failed: " + e.getMessage(), e);
        }

        return false;
    }

    public void storeSuccessfulMigration(Migration migration, Connection connection) {
        String sql = "INSERT INTO Migration_Table (migrationID, author, filename, checksum) VALUES (?, ?, ?, ?)";

        logger.info("Storing migration ID={}, Author={}", migration.getId(), migration.getAuthor());

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, String.valueOf(migration.getId()));
            statement.setString(2, migration.getAuthor());
            statement.setString(3, ConfigLoader.getProperty("migration.file"));
            statement.setString(4, migration.getChecksum());

            logger.debug(LoggerHelper.INDENT + "   Executing PreparedStatement for migration ID={}", migration.getId());

            statement.executeUpdate();

            logger.info("Migration ID={} stored.", migration.getId());
        } catch (SQLException e) {
            logger.error("Failed to store migration ID={}: {}", migration.getId(), e.getMessage());
            throw new RuntimeException("Storing migration in history failed: " + e.getMessage(), e);
        }
    }

    public void deleteRolledBackMigration(Migration migration, Connection connection) {
        String query = String.format(DELETE_MIGRATION_CHECKSUM, migration.getChecksum());

        logger.info("Rolling back migration ID={}", migration.getId());

        try {
            logger.debug(LoggerHelper.INDENT + "  SQL Query: {}", query);
            connection.createStatement().execute(query);

            logger.info("Migration ID={} removed from history.", migration.getId());
        } catch (SQLException e) {
            logger.error("Failed to delete migration rolled-back migration ID={}: {}", migration.getId(), e.getMessage());
            throw new RuntimeException("Deleting migration from history failed: " + e.getMessage(), e);
        }
    }
}
