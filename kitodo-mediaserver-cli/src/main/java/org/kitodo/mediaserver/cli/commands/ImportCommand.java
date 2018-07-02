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

import java.util.concurrent.Callable;
import org.kitodo.mediaserver.importer.config.ImporterProperties;
import org.kitodo.mediaserver.importer.control.ImporterFlowControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Import works.
 */
@Command(
    description = "Import works."
    )
@Component
public class ImportCommand implements Callable, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportCommand.class);

    @Option(
        names = "--hot-folder",
        description = "Folder to search for new works."
    )
    private String hotfolderPath;

    @Option(
        names = "--importing-folder",
        description = "Folder to temporarily copy work files while importing them."
    )
    private String importingFolderPath;

    @Option(
        names = "--temp-work-folder",
        description = "Folder to move old work files if an existing work needs to be replaced."
    )
    private String tempWorkFolderPath;

    @Option(
        names = "--error-folder",
        description = "Folder to copy work files after failed import."
    )
    private String errorFolderPath;

    @Option(
        names = "--work-folder",
        description = "Folder to copy work files after import succeeded."
    )
    private String workFilesPath;

    @Option(
        names = "--work-id-regex",
        description = "Work folder name must match this regex pattern."
    )
    private String workIdRegex;

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

    private ImporterProperties importerProperties;

    private ImporterFlowControl importerFlowControl;

    private TaskScheduler taskScheduler;

    @Autowired
    public void setImporterProperties(ImporterProperties importerProperties) {
        this.importerProperties = importerProperties;
    }

    @Autowired
    public void setImporterFlowControl(ImporterFlowControl importerFlowControl) {
        this.importerFlowControl = importerFlowControl;
    }

    @Autowired
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * Clear files cache.
     */
    public void importWorks() throws Exception {

        LOGGER.info("Importing works...");
        LOGGER.debug(
            "Importing options: hotFolder='" + importerProperties.getHotfolderPath() + "'"
            + " workFilesPath='" + importerProperties.getWorkFilesPath() + "'"
            + " importingFolderPath='" + importerProperties.getImportingFolderPath() + "'"
            + " tempWorkFolderPath='" + importerProperties.getTempWorkFolderPath() + "'"
            + " errorFolderPath='" + importerProperties.getErrorFolderPath() + "'"
            + " workIdRegex='" + importerProperties.getWorkIdRegex() + "'"
            + " cron='" + importerProperties.getCron() + "'"
        );

        // run the importer
        importerFlowControl.importWorks();

        LOGGER.info("Finished importing works.");
    }

    /**
     * Callable for on-demand CLI execution.
     * @return always null
     * @throws Exception on file access errors by FileDeleter
     */
    @Override
    public Object call() throws Exception {

        // override settings if provided
        if (hotfolderPath != null) {
            importerProperties.setHotfolderPath(hotfolderPath);
        }
        if (importingFolderPath != null) {
            importerProperties.setImportingFolderPath(importingFolderPath);
        }
        if (tempWorkFolderPath != null) {
            importerProperties.setTempWorkFolderPath(tempWorkFolderPath);
        }
        if (errorFolderPath != null) {
            importerProperties.setErrorFolderPath(errorFolderPath);
        }
        if (workFilesPath != null) {
            importerProperties.setWorkFilesPath(workFilesPath);
        }
        if (workIdRegex != null) {
            importerProperties.setWorkIdRegex(workIdRegex);
        }
        if (cron != null) {
            importerProperties.setCron(cron);
        }

        if (isScheduler) {
            // start scheduling
            isScheduler = false;
            taskScheduler.schedule(this, new CronTrigger(importerProperties.getCron()));
        } else {
            // run command immediately
            importWorks();
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
            LOGGER.error("A severe error occurred during import: " + e, e);
            //TODO Notify
        }
    }
}
