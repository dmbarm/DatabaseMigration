package org.migrationtool.exceptions;

public class MigrationStatusException extends RuntimeException {
    public MigrationStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
