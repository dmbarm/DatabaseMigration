package org.migrationtool.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerHelper {
    private static final Logger logger = LoggerFactory.getLogger(LoggerHelper.class);

    private LoggerHelper() {}

    public static final String SEPARATOR = "────────────────────────────────────────────────────────────────────────────────────";
    public static final String INDENT = "└── ";
    public static final String CONNECT_INDENT = "├── ";
    public static final String REVERSE_INDENT = "┌── ";
    public static final String SECTION_START = "===========================================";

    public static void logSQLQuery(String query) {
        logger.debug("         " + INDENT + " SQL Query: {}", query);
    }

    public static void logSQLError(String error) {
        logger.error("SQL Exception: {}", error);
    }
}
