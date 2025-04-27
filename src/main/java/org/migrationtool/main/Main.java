package org.migrationtool.main;

import org.migrationtool.database.DatabasePool;
import org.migrationtool.main.commands.Migrate;
import org.migrationtool.main.commands.Rollback;
import org.migrationtool.main.commands.Status;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.HelpCommand;
import picocli.CommandLine.Model.CommandSpec;

@Command(
    name = "migration-tool",
    mixinStandardHelpOptions = true,
    version = "1.0",
    description = "Database Migration Tool",
    subcommands = {
        Migrate.class,
        Rollback.class,
        Status.class,
        HelpCommand.class
    }
)
public class Main implements Runnable {
    @CommandLine.Spec
    private CommandSpec spec;

    public static void main(String[] args) {
        int exitCode = new CommandLine(Main.class).execute(args);
        DatabasePool.close();
        System.exit(exitCode);
    }

    @Override
    public void run() {
        spec.commandLine().usage(System.out);
    }
}