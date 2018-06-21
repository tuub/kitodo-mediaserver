/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

package org.kitodo.mediaserver.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.kitodo.mediaserver.cli.commands.CacheClearCommand;
import org.kitodo.mediaserver.cli.commands.MainCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * Main command line runner.
 */
@Component
public class Terminal implements CommandLineRunner {

    private ApplicationContext applicationContext;

    private MainCommand mainCommand;
    private CacheClearCommand cacheClearCommand;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setMainCommand(MainCommand mainCommand) {
        this.mainCommand = mainCommand;
    }

    @Autowired
    public void setCacheClearCommand(CacheClearCommand cacheClearCommand) {
        this.cacheClearCommand = cacheClearCommand;
    }

    /**
     * Runs the CLI commands.
     *
     * @param args command line arguments
     */
    @Override
    public void run(String... args) {

        CommandLine commandLine = new CommandLine(mainCommand);
        commandLine.addSubcommand("cacheclear", cacheClearCommand);

        // parse command line arguments
        List<CommandLine> parsedCommands = new ArrayList<>();
        try {
            parsedCommands = commandLine.parse(args);
        } catch (CommandLine.ParameterException ex) {
            System.err.println(ex.toString());
            commandLine.usage(System.out);
            exit(1);
        }

        // show help if no arguments given
        if (args.length == 0) {
            commandLine.usage(System.out);
            exit(0);
        }

        // run commands
        for (CommandLine cmdLine : parsedCommands) {

            // usage help is requested
            if (cmdLine.isUsageHelpRequested()) {
                cmdLine.usage(System.out);
                exit(0);
            }

            // run the commands sequence from parent to child
            if (cmdLine.getCommand() instanceof Callable) {
                try {
                    ((Callable)cmdLine.getCommand()).call();
                } catch (Exception ex) {
                    System.err.println("Command '" + cmdLine.getCommandName() + "' failed: " + ex);
                    exit(1);
                }
            }

        }
    }

    /**
     * Nicely exit the program and return an error code.
     *
     * @param code error code on program exit
     */
    private void exit(int code) {
        System.exit(SpringApplication.exit(applicationContext, () -> code));
    }
}
