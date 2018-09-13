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
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Initialize or update database tables.
 */
@Command(
    description = "Initialize or update database tables."
    )
@Component
public class UpdateDbCommand implements Callable {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateDbCommand.class);

    @Option(
        names = {"-d", "--drop"},
        description = "Delete existing tables first. THIS WILL DESTROY EXISTING DATA!"
    )
    private Boolean drop = false;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help.")
    private Boolean help;

    private Flyway flyway;

    @Autowired
    public void setFlyway(Flyway flyway) {
        this.flyway = flyway;
    }

    /**
     * Initialize DB tables.
     */
    public void updateDb() {

        try {
            LOGGER.info("Initializing or updating database...");
            LOGGER.debug("Database options: dataSource='" + flyway.getDataSource() + "'");

            // drop tables
            if (drop) {
                LOGGER.info("Dropping existing tables...");
                flyway.clean();
            }

            // create or update tables
            LOGGER.info("Migrating DB tables...");
            flyway.migrate();

            LOGGER.info("Database updated.");

        } catch (RuntimeException ex) {
            LOGGER.error("Could not initialize database.", ex);
            throw ex;
        }

    }

    /**
     * Callable for on-demand CLI execution.
     * @return always null
     */
    @Override
    public Object call() {

        // run command immediately
        updateDb();

        return null;
    }
}
