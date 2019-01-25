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

import java.util.List;
import java.util.concurrent.Callable;
import org.kitodo.mediaserver.core.config.CliProperties;
import org.kitodo.mediaserver.core.db.entities.ActionData;
import org.kitodo.mediaserver.core.services.ActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;
import picocli.CommandLine;

/**
 * Perform requested actions.
 */
@CommandLine.Command(
    description = "Perform requested actions."
    )
@Component
public class PerformRequestedCommand implements Callable, Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformRequestedCommand.class);

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help.")
    private Boolean help;

    @CommandLine.Option(
        names = {"-s", "--scheduler"},
        description = "Keep process running and automatically run scheduled cache cleaning."
    )
    private Boolean isScheduler = false;

    @CommandLine.Option(
        names = {"--continue-on-error"},
        description = "Run further actions if an error occurs."
    )
    private Boolean continueOnError;

    @CommandLine.Option(
        names = {"--cron"},
        description = "Cron trigger to use for scheduling."
    )
    private String cron;

    private TaskScheduler taskScheduler;
    private ActionService actionService;
    private CliProperties cliProperties;

    @Autowired
    public void setTaskScheduler(TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    @Autowired
    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    @Autowired
    public void setCliProperties(CliProperties cliProperties) {
        this.cliProperties = cliProperties;
    }

    /**
     * Performs all requested actions.
     */
    private void performActions() throws Exception {

        LOGGER.debug(
            "performActions options: continueOnError='" + cliProperties.getPerformActions().getContinueOnError() + "'"
                + " cron='" + cliProperties.getPerformActions().getCron() + "'"
        );

        List<ActionData> actions = actionService.getUnperformedActions();
        String message;
        for (ActionData action : actions) {
            LOGGER.info("Performing " + action.getActionName() + " requested at " + action.getRequestTime() + "...");
            try {
                actionService.performRequested(action);
            } catch (Exception e) {
                message = "Error while performing requested action ("
                    + "actionName='" + action.getActionName() + "', "
                    + "requestedTime='" + action.getRequestTime() + "', "
                    + "parameters='" + action.getParameter() + "'): " + e;
                if (continueOnError != null && continueOnError) {
                    LOGGER.error(message, e);
                } else {
                    throw new Exception(message, e);
                }
            }
        }
    }

    /**
     * Callable for on-demand CLI execution.
     * @return always null
     * @throws Exception on errors in performed IActions
     */
    @Override
    public Object call() throws Exception {

        // override settings if provided
        if (continueOnError != null) {
            cliProperties.getPerformActions().setContinueOnError(continueOnError);
        }
        if (cron != null) {
            cliProperties.getPerformActions().setCron(cron);
        }

        if (isScheduler) {
            // start scheduling
            isScheduler = false;
            taskScheduler.schedule(this, new CronTrigger(cliProperties.getPerformActions().getCron()));
        } else {
            // run command immediately
            performActions();
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
            LOGGER.error("A severe error occurred during performance of requested actions: " + e, e);
            //TODO Notify
        }
    }
}
