package org.migrationtool.actions;

import org.migrationtool.models.Column;
import org.migrationtool.utils.ChecksumGenerator;
import org.migrationtool.utils.SQLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class ModifyColumnTypeAction implements MigrationAction {
    private static final Logger logger = LoggerFactory.getLogger(ModifyColumnTypeAction.class);
    private final Column column;

    public ModifyColumnTypeAction(Column column) {
        this.column = column;
    }

    @Override
    public void execute(Connection connection) {
        logger.debug("       Executing ModifyColumnTypeAction on table: {}, column: {}, new data type: {}", column.getTableName(), column.getName(), column.getNewDataType());
        executeSQL(connection, buildModifyColumnTypeQuery());
    }

    private String buildModifyColumnTypeQuery() {
        return SQLConstants.ALTER_TABLE + column.getTableName()
                + SQLConstants.ALTER_COLUMN + column.getName() + " "
                + column.getNewDataType() + SQLConstants.SEMICOLON;
    }

    @Override
    public String generateChecksum() {
        // Making specific signature
        return ChecksumGenerator.generateWithSHA256("ModifyColumn:" + column.getTableName() + "|" + column);
    }
}
