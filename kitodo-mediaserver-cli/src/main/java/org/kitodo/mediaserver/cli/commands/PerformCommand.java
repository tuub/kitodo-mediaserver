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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import org.kitodo.mediaserver.core.config.CliProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.core.services.ActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import picocli.CommandLine;

/**
 * Run an action bean on one or more works.
 */
@CommandLine.Command(
    description = "Perform requested actions."
    )
@Component
public class PerformCommand implements Callable {

    private static final Logger LOGGER = LoggerFactory.getLogger(PerformCommand.class);

    @CommandLine.Option(
        names = {"-h", "--help"},
        usageHelp = true,
        description = "Show this help.")
    private Boolean help;

    @CommandLine.Option(
        names = {"--continue-on-error"},
        description = "Run further actions if an error occurs."
    )
    private Boolean continueOnError;

    @CommandLine.Parameters(
        index = "0",
        arity = "1",
        paramLabel = "action",
        description = "Action bean name to perform.")
    private String actionName;

    @CommandLine.Parameters(
        index = "1..*",
        arity = "1..*",
        paramLabel = "workIDpattern",
        description = "Work ID pattern to perform the action on.")
    private String[] workIds;

    @CommandLine.Option(
        names = {"-p", "--param"},
        description = "An action parameter.")
    private Map<String, String> params = new HashMap<>();

    private WorkRepository workRepository;
    private ActionService actionService;
    private CliProperties cliProperties;

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
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
     * Perform action on all matching works.
     */
    private void performAction() throws Exception {
        LOGGER.debug("Performing action: actioName='" + actionName + "' workIds='" + workIds + "' params='" + params + "'");

        if (!StringUtils.hasText(actionName)) {
            throw new IllegalArgumentException("actionName must be set.");
        }
        if (workIds == null || Arrays.stream(workIds).noneMatch(StringUtils::hasText)) {
            throw new IllegalArgumentException("workIds must be set.");
        }

        final Set<Work> allWorks = new HashSet<>();

        // Get all Works by workId patterns
        Arrays.stream(workIds)
            .filter(StringUtils::hasText)
            .distinct()
            .map(id -> id
                // escape SQL wildcard chars
                .replace("_", "\\_")
                .replace("%", "\\%")
                // replace shell wildcard chars with SQL ones
                .replaceAll("[*]+", "%")
                .replaceAll("\\?", "_"))
            .forEach(workIdPattern -> {
                List<Work> works = workRepository.findByIdLike(workIdPattern);
                allWorks.addAll(works);
            });

        if (allWorks.isEmpty()) {
            LOGGER.info("No matching works found.");
        } else {
            // Run action on all works
            for (Work work : allWorks) {
                try {
                    actionService.performImmediately(work, actionName, params);
                } catch (Exception ex) {
                    String message = "Error while performing action ("
                        + "workId='" + work.getId() + "', "
                        + "actionName='" + actionName + "', "
                        + "parameters='" + params + "'): " + ex;
                    if (continueOnError != null && continueOnError) {
                        LOGGER.error(message, ex);
                    } else {
                        throw new Exception(message, ex);
                    }
                }
            }
        }

    }

    /**
     * Callable for on-demand CLI execution.
     *
     * @return always null
     * @throws Exception on errors in performed IActions
     */
    @Override
    public Object call() throws Exception {

        // override settings if provided
        if (continueOnError != null) {
            cliProperties.getPerformActions().setContinueOnError(continueOnError);
        }

        // run command immediately
        performAction();

        return null;
    }

}
