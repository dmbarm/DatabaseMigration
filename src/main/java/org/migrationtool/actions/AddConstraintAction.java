package org.migrationtool.actions;

import org.migrationtool.models.Constraint;
import org.migrationtool.utils.ChecksumGenerator;
import org.migrationtool.utils.SQLConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class AddConstraintAction implements MigrationAction {
    private static final Logger logger = LoggerFactory.getLogger(AddConstraintAction.class);
    private final Constraint constraint;

    public AddConstraintAction(Constraint constraint) {
        this.constraint = constraint;
    }

    @Override
    public void execute(Connection connection) {
        logger.debug("       Executing AddConstraint on table {} with constraint {}", constraint.getTableName(), constraint);
        executeSQL(connection, buildAddConstraintQuery());
    }

    private String buildAddConstraintQuery() {
        if (constraint.isNamed()) {
            return SQLConstants.ALTER_TABLE + constraint.getTableName()
                    + SQLConstants.ADD_CONSTRAINT + constraint + SQLConstants.SEMICOLON;
        } else {
            return SQLConstants.ALTER_TABLE + constraint.getTableName()
                    + SQLConstants.ALTER_COLUMN + constraint.getColumnName()
                    + " SET " + constraint + SQLConstants.SEMICOLON;
        }
    }

    @Override
    public String generateChecksum() {
        // Creating special signature
        String string = "AddConstraint:" + constraint.getTableName() + "|" + constraint;
        return ChecksumGenerator.generateWithSHA256(string);
    }
}
