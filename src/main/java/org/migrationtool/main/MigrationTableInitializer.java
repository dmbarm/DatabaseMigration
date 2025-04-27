package org.migrationtool.main;

import org.migrationtool.database.DatabasePool;
import org.migrationtool.exceptions.MigrationHistoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MigrationTableInitializer {
    private static final Logger logger = LoggerFactory.getLogger(MigrationTableInitializer.class);

    private MigrationTableInitializer() {}

    public static void initialize() {
        try (Connection connection = DatabasePool.getDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS migration_table (" +
                    "id INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "migrationID INTEGER NOT NULL, " +
                    "author VARCHAR(255) NOT NULL, " +
                    "filename VARCHAR(255) NOT NULL, " +
                    "executionDate TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "checksum VARCHAR(255) NOT NULL);";

            statement.execute(query);
            logger.info("Migration history table is ready.");
        } catch (SQLException e) {
            throw new MigrationHistoryException("Error initializing migration history table", e);
        }
    }
}
