package org.MigrationTool.Actions;

import org.MigrationTool.Utils.ChecksumGenerator;
import org.MigrationTool.Utils.DatabasePool;

import java.sql.Connection;
import java.sql.SQLException;

public class DropTableAction implements MigrationAction {
    private String tableName;

    public DropTableAction(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void execute() {
        String query = "DROP TABLE " + tableName + ";";

        try (Connection connection = DatabasePool.getDataSource().getConnection()) {
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            throw new RuntimeException("Migration execution failed: " + e.getMessage(), e);
        }
    }

    @Override
    public String generateChecksum() {
        //making specific signature
        return ChecksumGenerator.generateWithSHA256("DropTable:" + tableName + "|");
    }
}
