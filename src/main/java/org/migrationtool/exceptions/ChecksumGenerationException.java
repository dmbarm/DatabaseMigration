package org.migrationtool.exceptions;

public class ChecksumGenerationException extends RuntimeException {
    public ChecksumGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
