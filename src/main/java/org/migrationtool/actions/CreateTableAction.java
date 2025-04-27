package org.migrationtool.actions;

import org.migrationtool.models.Column;
import org.migrationtool.models.Constraint;
import org.migrationtool.utils.ChecksumGenerator;
import org.migrationtool.utils.SQLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.List;

public class CreateTableAction implements MigrationAction {
    private static final Logger logger = LoggerFactory.getLogger(CreateTableAction.class);
    private final String tableName;
    private final List<Column> columns;

    public CreateTableAction(List<Column> columns) {
        this.columns = columns;
        this.tableName = columns.getFirst().getTableName();
    }


    @Override
    public void execute(Connection connection) {
        logger.debug("       Executing CreateTableAction on table: {}", tableName);
        executeSQL(connection, buildCreateTableQuery());
        addNamedConstraints(connection);
    }

    private String buildCreateTableQuery() {
        StringBuilder query = new StringBuilder(SQLConstants.CREATE_TABLE)
                .append(tableName).append(SQLConstants.OPEN_BRACKET);

        for (int i = 0; i < columns.size(); i++) {
            Column column = columns.get(i);
            query.append(column.getName()).append(SQLConstants.SPACE).append(column.getType());

            column.getConstraintsList().stream()
                    .filter(constraint -> !constraint.isNamed())
                    .forEach(constraint -> query.append(SQLConstants.SPACE).append(constraint));

            if (i < columns.size() - 1) {
                query.append(SQLConstants.COMMA);
            }
        }

        return query.append(SQLConstants.CLOSE_BRACKET)
                .append(SQLConstants.SEMICOLON)
                .toString();
    }

    private void addNamedConstraints(Connection connection) {
        columns.stream()
                .flatMap(column -> column.getConstraintsList().stream())
                .filter(Constraint::isNamed)
                .map(AddConstraintAction::new)
                .forEach(action -> action.execute(connection));
    }

    @Override
    public String generateChecksum() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CreateTable:").append(tableName).append("|");
        columns.forEach(column -> stringBuilder.append(column).append("|"));
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);


        return ChecksumGenerator.generateWithSHA256(stringBuilder.toString());
    }
}
