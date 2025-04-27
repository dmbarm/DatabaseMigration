package org.migrationtool.actions;

import org.migrationtool.models.Index;
import org.migrationtool.utils.ChecksumGenerator;
import org.migrationtool.utils.SQLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class AddIndexAction implements MigrationAction {
    private static final Logger logger = LoggerFactory.getLogger(AddIndexAction.class);
    private final Index index;

    public AddIndexAction(Index index) {
        this.index = index;
    }

    @Override
    public void execute(Connection connection) {
        logger.debug("       Executing AddIndexAction on table {}", index.getTableName());
        executeSQL(connection, buildAddIndexQuery());
    }

    private String buildAddIndexQuery() {
        String uniqueClause = index.isUnique() ? SQLConstants.UNIQUE : "";
        String columns = String.join(SQLConstants.COMMA, index.getColumns());

        return SQLConstants.CREATE_INDEX + uniqueClause + index.getName()
                + SQLConstants.INDEX_ON + index.getTableName()
                + SQLConstants.OPEN_BRACKET + columns + SQLConstants.CLOSE_BRACKET
                + SQLConstants.SEMICOLON;
    }

    @Override
    public String generateChecksum() {
        String string = "AddIndex: " + index.getTableName() + "|" + index;
        return ChecksumGenerator.generateWithSHA256(string);
    }
}
