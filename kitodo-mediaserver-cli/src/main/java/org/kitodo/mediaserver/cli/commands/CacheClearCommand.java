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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import org.kitodo.mediaserver.cli.converter.TimespanConverter;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.util.FileDeleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Clears derivative files cache.
 */
@Command(
    description = "Clear derivative files cache."
    )
@Component
public class CacheClearCommand implements Callable, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheClearCommand.class);

    @Option(
        names = "--not-touched-since",
        description = "Delete cached files last accessed since X. "
            + "X may be a number of seconds or Xs, Xm, Xh, Xd (seconds, minutes, hours, days)..",
        converter = TimespanConverter.class
    )
    private Long notTouchedSince;

    @Option(
        names = "--cron",
        description = "Cron trigger to use for scheduling."
    )
    private String cron;

    @Option(
        names = {"-s", "--scheduler"},
        description = "Keep process running and automatically run scheduled cache cleaning"
    )
    private Boolean isScheduler = false;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help.")
    private Boolean help;

    private FileserverProperties fileserverProperties;

    private TaskScheduler taskScheduler;

    @Autowired
    public void setFileserverProperties(FileserverProperties fileserverProperties) {
        this.fileserverProperties = fileserverProperties;
    }

    @Autowired
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * Clear files cache.
     */
    public void clear() throws IOException {

        LOGGER.info("Clearing files cache...");
        LOGGER.debug("Cache clearing options: path='" + fileserverProperties.getCachePath() + "'"
            + " notTouchedSince='" + fileserverProperties.getCacheClearSince() + "'");

        Path path = Paths.get(fileserverProperties.getCachePath());
        FileDeleter fileDeleter = new FileDeleter();
        fileDeleter.delete(path, fileserverProperties.getCacheClearSince(), true);

        LOGGER.info("Finished cache clearing.");
    }

    /**
     * Callable for on-demand CLI execution.
     * @return always null
     * @throws IOException on file access errors by FileDeleter
     */
    @Override
    public Object call() throws IOException {

        // override settings if provided
        if (notTouchedSince != null) {
            fileserverProperties.setCacheClearSince(notTouchedSince);
        }
        if (cron != null) {
            fileserverProperties.setCacheClearCron(cron);
        }

        if (isScheduler) {
            // start scheduling
            isScheduler = false;
            taskScheduler.schedule(this, new CronTrigger(fileserverProperties.getCacheClearCron()));
        } else {
            // run command immediately
            clear();
        }

        return null;
    }

    /**
     * Runnable for execution in scheduled task.
     */
    @Override
    public void run() {
        try {
            call();
        } catch (Exception e) {
            LOGGER.error("Cache clearing failed: " + e, e);
        }
    }
}
