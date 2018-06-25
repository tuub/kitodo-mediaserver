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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(Terminal.class);

    private ApplicationContext applicationContext;

    private CommandLine commandLine;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    /**
     * Runs the CLI commands.
     *
     * @param args command line arguments
     */
    @Override
    public void run(String... args) {

        // parse command line arguments
        List<CommandLine> parsedCommands = new ArrayList<>();
        try {
            parsedCommands = commandLine.parse(args);
        } catch (CommandLine.ParameterException ex) {
            LOGGER.error("Exception while parsing command line arguments.", ex);
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
                    LOGGER.error("Exception while calling a CLI command.", ex);
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
