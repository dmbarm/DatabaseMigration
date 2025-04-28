package org.migrationtool.actions;

import org.migrationtool.models.Index;
import org.migrationtool.utils.ChecksumGenerator;
import org.migrationtool.utils.SQLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DropIndexAction implements MigrationAction {
    private static final Logger logger = LoggerFactory.getLogger(DropIndexAction.class);
    private final Index index;

    public DropIndexAction(Index index) {
        this.index = index;
    }


    @Override
    public void execute(Connection connection) {
        logger.debug("       Executing DropIndexAction on table {} with index {}", index.getTableName(), index.getName());
        executeSQL(connection, buildDropIndexQuery());
    }

    private String buildDropIndexQuery() {
        return SQLConstants.DROP_INDEX + index.getName() + SQLConstants.SEMICOLON;
    }

    @Override
    public String generateChecksum() {
        String string = "DropIndex: " + index.getTableName() + "|" + index;
        return ChecksumGenerator.generateWithSHA256(string);
    }
}
