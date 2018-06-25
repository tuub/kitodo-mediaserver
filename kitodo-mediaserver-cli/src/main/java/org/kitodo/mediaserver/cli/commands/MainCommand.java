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

package org.kitodo.mediaserver.cli.commands;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import java.util.concurrent.Callable;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Main command.
 */
@Command(
    name = "kitodo-mediaserver-cli",
    description = "Kitodo Mediaserver CLI"
    )
@Component
public class MainCommand implements Callable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display usage help.")
    private Boolean help = false;

    @Option(names = "-v", description = "Display debug information. Use -vv, -vvv to get even more debug output.")
    private Boolean[] verbose;

    @Option(names = {"-q", "--quiet"}, description = "Don't print anything to standard output.")
    private Boolean quiet = false;

    /**
     * Set console log format and verbosity by modifying existing Logger.
     */
    private void setLogger() {

        // RootLogger to set log levels
        Logger rootLogger = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        LoggerContext loggerContext = rootLogger.getLoggerContext();

        // PatternLayoutEncoder to set log format
        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setContext(loggerContext);

        if (verbose != null && verbose.length > 0) {

            patternLayoutEncoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} %-5level %logger - %msg%n");

            String[] packages = {
                "org.springframework",
                "com.zaxxer.hikari",
                "org.hibernate",
                "org.kitodo"
            };

            // use multiple -v (-vv, -vvv) to get even more debug output
            Level level;
            if (verbose.length == 1) {
                level = Level.DEBUG;
            } else if (verbose.length == 2) {
                level = Level.TRACE;
            } else if (verbose.length > 2) {
                level = Level.ALL;
            } else {
                level = Level.INFO;
            }

            for (String pkg : packages) {
                Logger packageLogger = (Logger)LoggerFactory.getLogger(pkg);
                packageLogger.setLevel(level);
            }

        } else if (quiet) {
            // not output to console
            patternLayoutEncoder.setPattern("");
        } else {
            // default output format
            patternLayoutEncoder.setPattern("%msg%n");
        }

        patternLayoutEncoder.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setContext(loggerContext);
        consoleAppender.setEncoder(patternLayoutEncoder);
        consoleAppender.start();

        rootLogger.addAppender(consoleAppender);
    }

    @Override
    public Object call() {

        setLogger();

        return null;
    }
}
