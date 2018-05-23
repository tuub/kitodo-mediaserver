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

package org.kitodo.mediaserver.importer.control;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FilenameUtils;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.ValidationException;
import org.kitodo.mediaserver.importer.api.IImportValidation;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.kitodo.mediaserver.importer.config.ImporterProperties;
import org.kitodo.mediaserver.importer.exceptions.ImporterException;
import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.importer.util.ImporterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


/**
 * Flow control class for the importer.
 */
@Component
public class ImporterFlowControl implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImporterFlowControl.class);

    private ImporterUtils importerUtils;
    private ImporterProperties importerProperties;
    private IImportValidation importValidation;

    private IDataReader workDataReader;

    @Autowired
    public void setImporterUtils(ImporterUtils importerUtils) {
        this.importerUtils = importerUtils;
    }

    @Autowired
    public void setImporterProperties(ImporterProperties importerProperties) {
        this.importerProperties = importerProperties;
    }

    @Autowired
    public void setImportValidation(IImportValidation importValidation) {
        this.importValidation = importValidation;
    }

    public void setWorkDataReader(IDataReader workDataReader) {
        this.workDataReader = workDataReader;
    }

    /**
     * Runs the importer algorithm.
     *
     * @param args cli arguments
     * @throws Exception if a severe error occurs
     */
    @Override
    public void run(String... args) throws Exception {

        importScheduled();

    }

    /**
     * Controls the importer algorithm.
     * TODO this is just a stub - work in progress
     *
     * @throws Exception if a severe error occurs
     */
    /* @Scheduled(fixedRate = 1000) */
    private void importScheduled() throws Exception {

        // Get a work from the hotfolder and move it to the import-in-progress-folder. Make sure, this set of files is
        // in a subdirectory named as the XML file with the mets-mods-data.

        File workDir;

        System.out.println("Starting ...");

        while ((workDir = importerUtils.getWorkPackage()) != null) {

            // Get the mets file
            File mets = new File(workDir, workDir.getName() + ".xml");
            if (!mets.exists()) {
                throw new ImporterException("Mets file not found, expected at " + mets.getAbsolutePath());
            }

            Work workResult = workDataReader.read(workDir);

            // * Validate the data and the set of files (see below).
            //
            // * Read the work data from the mets-mods file.
            // TODO replace with work reader
            Work work = new Work("id", "title");

            // Validate import
            try {
                importValidation.validate(work, mets);

            } catch (ValidationException vex) {
                LOGGER.error("Validation of " + workDir.getName() + " was unsuccesful, "
                        + "moving to error folder. Error: " + vex);
                importerUtils.moveDir(workDir, new File(importerProperties.getErrorFolderPath()));
                continue;
            }

            //
            // * Check in the database if this work is already present.
            //
            // * If the work is already present, it should be replaced.
            //
            // * Files created and cached by the fileserver must be deleted.
            //
            // * If an identifier is already present and associated with another work, the import job should be exited
            //   with an error.
            //
            // * If the work is already present, check if it is enabled (save the result).
            //
            // * If applicable, move old work files to a temporary folder under the import folder.
            //
            // * If applicable, delete old cached files.
            //
            // * Move work files to the production root.
            //
            // * Perform all defined direct import actions (doi registration…).
            //
            // * Order all defined asynchronous import actions (creation of additional files…).
            //
            // * Insert the work data into the database, updating if old data present. If the work already was present
            //   and disabled, set enabled to false.
            //
            // * Delete temporary files.
            //
            // * Trigger indexing in presentation system (configurable).

        }


        // If a severe error occures during the process, the following steps must be executed:
        //
        // * Move work folder to the error folder.
        //
        // * If there were old data for this work present:
        //
        // * Move old work files from the temporary folder back to the original folder.
        //
        // * If the error occured writing to the database, rollback (of course).
    }
}
