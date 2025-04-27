package org.migrationtool.actions;

import org.migrationtool.utils.ChecksumGenerator;
import org.migrationtool.utils.SQLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DropTableAction implements MigrationAction {
    private static final Logger logger = LoggerFactory.getLogger(DropTableAction.class);
    private final String tableName;

    public DropTableAction(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public void execute(Connection connection) {
        logger.debug("      Executing DropTableAction on table: {}", tableName);
        executeSQL(connection, buildDropTableQuery());
    }

    private String buildDropTableQuery() {
        return SQLConstants.DROP_TABLE + tableName + SQLConstants.SEMICOLON;
    }

    @Override
    public String generateChecksum() {
        // Making specific signature
        return ChecksumGenerator.generateWithSHA256("DropTable:" + tableName);
    }
}
