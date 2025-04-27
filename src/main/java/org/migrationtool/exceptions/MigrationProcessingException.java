package org.migrationtool.exceptions;

public class MigrationProcessingException extends RuntimeException {
  public MigrationProcessingException(String message, Throwable cause) {
    super(message, cause);
  }
}
