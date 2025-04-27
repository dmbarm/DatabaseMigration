package org.migrationtool.main;

import org.migrationtool.database.DatabasePool;
import org.migrationtool.utils.LoggerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting MigrationTool...");

        try {
            logger.info("Initializing migration history table...");
            MigrationTableInitializer.initialize();
            logger.info("Migration history table initialized successfully.");


            // TODO: split these two parts
            logger.info(LoggerHelper.SECTION_START);
            logger.info("     STARTING MIGRATION EXECUTION");
            logger.info(LoggerHelper.SECTION_START);
            logger.info("");
            new MigrationExecutor().executeMigrations();

/*            logger.info(LoggerHelper.SECTION_START);
            logger.info("     STARTING ROLLBACK EXECUTION");
            logger.info(LoggerHelper.SECTION_START);
            logger.info("");
            new MigrationExecutor().rollbackMigrations(12);*/


            logger.info("");
            logger.info(LoggerHelper.SECTION_START);
            logger.info("    EXECUTION COMPLETED SUCCESSFULLY");
            logger.info(LoggerHelper.SECTION_START);
        } catch (Exception e) {
            logger.error("MigrationTool encountered a fatal error: {}", e.getMessage(), e);
            System.exit(1);
        }

        DatabasePool.close();
        logger.info("");
        logger.info("MigrationTool finished execution.");
    }
}