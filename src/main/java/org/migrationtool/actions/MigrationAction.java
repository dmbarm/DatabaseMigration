package org.migrationtool.actions;

import org.migrationtool.exceptions.MigrationExecutionException;
import org.migrationtool.utils.LoggerHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public interface MigrationAction {
    void execute(Connection connection);
    String generateChecksum();

    default void executeSQL(Connection connection, String query) {
        try (Statement statement = connection.createStatement()) {
            LoggerHelper.logSQLQuery(query);
            statement.execute(query);
        } catch (SQLException e) {
            throw new MigrationExecutionException("SQL execution failed: " + query, e);
        }
    }
}
