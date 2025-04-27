package org.migrationtool.main.commands;

import org.migrationtool.main.MigrationExecutor;
import org.migrationtool.main.MigrationTableInitializer;
import picocli.CommandLine;

@CommandLine.Command(name = "migrate", description = "Apply all pending migrations")
public class Migrate implements Runnable {
    @Override
    public void run() {
        MigrationTableInitializer.initialize();
        new MigrationExecutor().executeMigrations();
    }
}
