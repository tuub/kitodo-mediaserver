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

import java.util.EnumSet;
import java.util.concurrent.Callable;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.boot.Metadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.kitodo.mediaserver.cli.database.HibernateInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Initialize database tables.
 */
@Command(
    description = "Initialize database tables."
    )
@Component
public class InitDbCommand implements Callable {

    private static final Logger LOGGER = LoggerFactory.getLogger(InitDbCommand.class);

    @Option(
        names = {"-d", "--drop"},
        description = "Delete existing tables first. THIS WILL DESTROY EXISTING DATA!"
    )
    private Boolean drop = false;

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help.")
    private Boolean help;

    private EntityManager entityManager;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Initialize DB tables.
     */
    public void initDb() {

        try {
            LOGGER.info("Initializing database...");

            SchemaExport export = new SchemaExport();
            EnumSet<TargetType> targetTypes = EnumSet.of(TargetType.DATABASE);
            Metadata metadata = HibernateInfo.getMetadata();

            LOGGER.debug("Database options: database='" + metadata.getDatabase() + "'");

            // drop tables if existing
            if (drop) {
                LOGGER.info("Dropping existing tables...");
                export.drop(targetTypes, metadata);
            }

            // create tables
            LOGGER.info("Creating DB tables...");
            export.execute(targetTypes, SchemaExport.Action.CREATE, metadata);

            LOGGER.info("Importing initial DB data...");

            // import initial data
            entityManager = entityManager.getEntityManagerFactory().createEntityManager();
            Session session = entityManager.unwrap(Session.class);
            session.doWork(connection -> ScriptUtils.executeSqlScript(connection, new ClassPathResource("initial-data.sql")));

            LOGGER.info("Database initialized.");

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
        initDb();

        return null;
    }
}
