package org.kitodo.mediaserver.importer.control;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Flow control class for the importer.
 */
@Component
public class ImporterFlowControl implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImporterFlowControl.class);

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

        /*
            A. Create a list of works to import.

            B. For each item in this list:

            * Make an (atomic) move of this set into the import-in-progress-folder. Make sure, this set of files is in a subdirectory named as the XML file with the mets-mods-data.

            * Validate the data and the set of files (see below).

            * Read the work data from the mets-mods file.

            * Check in the database if this work of any of its identifiers is already present.

            * If the work is already present, it should be replaced.

            * Files created and cached by the fileserver must be deleted.

            * If an identifier is already present and associated with another work, the import job should be exited with an error.

            * If the work is already present, check if it is enabled (save the result).

            * If applicable, move old work files to a temporary folder under the import folder.

            * If applicable, delete old cached files.

            * Move work files to the production root.

            * Perform all defined direct import actions (doi registration…).

            * Order all defined asynchronous import actions (creation of additional files…).

            * Insert the work data into the database, updating if old data present. If the work already was present and disabled, set enabled to false.

            * Delete temporary files.

            * Trigger indexing in presentation system (configurable).

         */


        /*
            If a severe error occures during the process, the following steps must be executed:

            * Move work folder to the error folder.

            * If there were old data for this work present:

            * Move old work files from the temporary folder back to the original folder.

            * If the error occured writing to the database, rollback (of course).
         */
    }
}
