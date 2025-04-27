package org.migrationtool.main;

import org.migrationtool.actions.MigrationAction;
import org.migrationtool.database.DatabasePool;
import org.migrationtool.exceptions.MigrationExecutionException;
import org.migrationtool.exceptions.MigrationProcessingException;
import org.migrationtool.exceptions.MigrationRollbackException;
import org.migrationtool.parsers.MigrationParser;
import org.migrationtool.utils.ConfigLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class MigrationExecutor {
    private static final Logger logger = LoggerFactory.getLogger(MigrationExecutor.class);

    public void executeMigrations() {
        List<Migration> migrations = loadMigrations();
        if (migrations.isEmpty()) {
            logger.warn("No migrations were found.");
            return;
        }

        try (Connection connection = DatabasePool.getDataSource().getConnection()) {
            processMigrations(
                    migrations,
                    connection,
                    new MigrationProcessor() {
                        @Override
                        public void process(Migration migration, Connection connection, MigrationHistory migrationHistory) {
                            for (MigrationAction migrationAction : migration.getMigrationActions()) {
                                migrationAction.execute(connection);
                            }
                            migrationHistory.storeSuccessfulMigration(migration, connection);
                        }

                        @Override
                        public boolean shouldProcess(Migration migration, Connection connection, MigrationHistory migrationHistory) {
                            return !migrationHistory.alreadyExecuted(migration, connection);
                        }
                    },
                    false
            );
        } catch (SQLException e) {
            throw new MigrationProcessingException("Database error during migration execution", e);
        } catch (Exception e) {
            throw new MigrationExecutionException("Unexpected error during migration execution", e);
        }
    }

    public void rollbackMigrations(int rollbackAmount) {
        List<Migration> migrations = loadMigrations();
        if (migrations.isEmpty()) {
            logger.warn("No migrations were found.");
            return;
        }

        int start = Math.max(0, migrations.size() - rollbackAmount);
        List<Migration> rollBackedMigrations = migrations.subList(start, migrations.size());

        try (Connection connection = DatabasePool.getDataSource().getConnection()) {
            processMigrations(
                    rollBackedMigrations,
                    connection,
                    new MigrationProcessor() {

                        @Override
                        public void process(Migration migration, Connection connection, MigrationHistory migrationHistory) {
                            for (MigrationAction rollbackAction : migration.getRollbackActions()) {
                                rollbackAction.execute(connection);
                            }
                            migrationHistory.deleteRolledBackMigration(migration, connection);
                        }

                        @Override
                        public boolean shouldProcess(Migration migration, Connection connection, MigrationHistory migrationHistory) {
                            return migrationHistory.alreadyExecuted(migration, connection);
                        }
                    },
                    true
            );
        } catch (SQLException e) {
            throw new MigrationProcessingException("Database error during migration execution", e);
        } catch (Exception e) {
            throw new MigrationRollbackException("Unexpected error during migration rollback", e);
        }
    }

    private List<Migration> loadMigrations() {
        MigrationParser migrationParser = new MigrationParser();
        return migrationParser.parseMigrations(ConfigLoader.getProperty("migration.file"));
    }

    private void processMigrations(
            List<Migration> migrations,
            Connection connection,
            MigrationProcessor migrationProcessor,
            boolean reverseOrder) throws MigrationProcessingException, SQLException {
        MigrationHistory migrationHistory = new MigrationHistory();
        if (reverseOrder) Collections.reverse(migrations);

        for (Migration migration : migrations) {
            if (!migrationProcessor.shouldProcess(migration, connection, migrationHistory)) {
                logger.info("Skipping migration: ID={}, Author={}", migration.getId(), migration.getAuthor());
                continue;
            }

            try {
                migrationProcessor.process(migration, connection, migrationHistory);
                connection.commit();
            } catch (MigrationProcessingException e) {
                connection.rollback();
                throw new MigrationProcessingException("Migration processing failed for ID=" + migration.getId() + ", Author=" + migration.getAuthor(), e);
            } catch (SQLException e) {
                throw new MigrationProcessingException("Database error during migration execution", e);
            } catch (Exception e) {
                throw new MigrationExecutionException("Unexpected error during migration execution", e);
            }
        }
    }
  
    public void printStatus() {
        List<Migration> migrations = loadMigrations();
        if (migrations.isEmpty()) {
            logger.warn("No migrations were found.");
            return;
        }

        try (Connection connection = DatabasePool.getDataSource().getConnection()) {
            MigrationHistory migrationHistory = new MigrationHistory();

            logger.info("Applied migrations:");
            for (Migration migration : migrations) {
                if (migrationHistory.alreadyExecuted(migration, connection)) {
                    logger.info(" - ID={}, Author={}", migration.getId(), migration.getAuthor());
                }
            }

            logger.info("");
            logger.info("Pending migrations:");
            for (Migration migration : migrations) {
                if (!migrationHistory.alreadyExecuted(migration, connection)) {
                    logger.info(" - ID={}, Author={}", migration.getId(), migration.getAuthor());
                }
            }
        } catch (SQLException e) {
            logger.error("Database connection error: {}", e.getMessage());
            throw new RuntimeException("Database error during migration execution", e);
        }
    }
}
