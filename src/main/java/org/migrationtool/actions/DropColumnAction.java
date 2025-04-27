package org.migrationtool.actions;

import org.migrationtool.models.Column;
import org.migrationtool.utils.ChecksumGenerator;
import org.migrationtool.utils.SQLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DropColumnAction implements MigrationAction {
    private static final Logger logger = LoggerFactory.getLogger(DropColumnAction.class);
    private final Column column;

    public DropColumnAction(Column column) {
        this.column = column;
    }

    @Override
    public void execute(Connection connection) {
        logger.debug("       Executing DropColumnAction on table: {}, column: {}", column.getTableName(), column.getName());
        executeSQL(connection, buildDropColumnQuery());
    }

    private String buildDropColumnQuery() {
        return SQLConstants.ALTER_TABLE + column.getTableName()
                + SQLConstants.DROP_COLUMN + column.getName()
                + SQLConstants.SEMICOLON;
    }

    @Override
    public String generateChecksum() {
        // Making specific signature
        return ChecksumGenerator.generateWithSHA256("DropColumn:" + column.getTableName() + "|" + column);
    }
}
