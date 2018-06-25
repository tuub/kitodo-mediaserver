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

package org.kitodo.mediaserver.cli.config;

import org.kitodo.mediaserver.cli.commands.CacheClearCommand;
import org.kitodo.mediaserver.cli.commands.ImportCommand;
import org.kitodo.mediaserver.cli.commands.InitDbCommand;
import org.kitodo.mediaserver.cli.commands.MainCommand;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import picocli.CommandLine;

@Configuration
@Import({FileserverProperties.class}) // CacheClearCommand
@ComponentScan(basePackages = {"org.kitodo.mediaserver.importer"}) // ImportCommand
public class CliConfiguration {

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        return threadPoolTaskScheduler;
    }

    @Bean
    public CommandLine commandLine(MainCommand mainCommand,
                                   CacheClearCommand cacheClearCommand,
                                   ImportCommand importCommand,
                                   InitDbCommand initDbCommand) {

        CommandLine commandLine = new CommandLine(mainCommand);
        commandLine.addSubcommand("cacheclear", cacheClearCommand);
        commandLine.addSubcommand("import", importCommand);
        commandLine.addSubcommand("initdb", initDbCommand);
        return commandLine;
    }
}
