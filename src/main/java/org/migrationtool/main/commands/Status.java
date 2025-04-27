package org.migrationtool.main.commands;

import org.migrationtool.main.MigrationExecutor;
import picocli.CommandLine;

@CommandLine.Command(name = "status", description = "Show migrations status")
public class Status implements Runnable {
    @Override
    public void run() {
        new MigrationExecutor().printStatus();
    }
}
