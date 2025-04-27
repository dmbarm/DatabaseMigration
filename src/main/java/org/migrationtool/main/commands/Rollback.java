package org.migrationtool.main.commands;

import org.migrationtool.main.MigrationExecutor;
import org.migrationtool.main.MigrationTableInitializer;
import picocli.CommandLine;

@CommandLine.Command(name = "rollback", description = "Rollback last N migrations")
public class Rollback implements Runnable{
    @CommandLine.Option(names = {"-n", "--number"},
    description = "Amount of migration to be rolled back",
    required = true)
    private int amount;

    @Override
    public void run() {
        MigrationTableInitializer.initialize();
        new MigrationExecutor().rollbackMigrations(amount);
    }
}
