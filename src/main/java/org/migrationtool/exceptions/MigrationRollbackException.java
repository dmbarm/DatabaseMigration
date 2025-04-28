package org.migrationtool.exceptions;

public class MigrationRollbackException extends RuntimeException {
    public MigrationRollbackException(String message, Throwable cause) {
        super(message, cause);
    }
}
