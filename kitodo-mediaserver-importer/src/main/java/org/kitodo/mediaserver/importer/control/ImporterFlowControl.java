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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import org.apache.commons.lang.StringUtils;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.services.WorkService;
import org.kitodo.mediaserver.core.util.FileDeleter;
import org.kitodo.mediaserver.importer.api.IImportValidation;
import org.kitodo.mediaserver.importer.api.IWorkChecker;
import org.kitodo.mediaserver.importer.config.ImporterProperties;
import org.kitodo.mediaserver.importer.exceptions.ImporterException;
import org.kitodo.mediaserver.importer.util.ImporterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Flow control class for the importer.
 */
@Component
public class ImporterFlowControl implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImporterFlowControl.class);

    private ImporterUtils importerUtils;
    private ImporterProperties importerProperties;
    private IImportValidation importValidation;
    private IDataReader workDataReader;
    private IWorkChecker workChecker;
    private WorkService workService;
    private FileDeleter fileDeleter;
    private IAction cacheDeleteAction;

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

    @Autowired
    public void setWorkDataReader(IDataReader workDataReader) {
        this.workDataReader = workDataReader;
    }

    @Autowired
    public void setWorkChecker(IWorkChecker workChecker) {
        this.workChecker = workChecker;
    }

    @Autowired
    public void setWorkService(WorkService workService) {
        this.workService = workService;
    }

    @Autowired
    public void setFileDeleter(FileDeleter fileDeleter) {
        this.fileDeleter = fileDeleter;
    }

    @Autowired
    public void setCacheDeleteAction(IAction cacheDeleteAction) {
        this.cacheDeleteAction = cacheDeleteAction;
    }

    /**
     * Runs the importer algorithm.
     *
     * @param args cli arguments
     * @throws Exception if a severe error occurs
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        //TODO schedule

        try {
            importWorks();
        } catch (Exception e) {
            LOGGER.error("A severe error occurred during import: " + e, e);
            //TODO Notify
        }
    }

    /**
     * Controls the importer algorithm.
     *
     * @throws Exception if a severe error occurs
     */
    private void importWorks() throws Exception {

        File workDir;

        LOGGER.info("Looking for works to import in folder " + importerProperties.getHotfolderPath());

        // Get a work from the hotfolder and move it to the import-in-progress-folder. Make sure that this set
        // of files is in a subdirectory named as the XML file with the mets-mods-data.
        while ((workDir = importerUtils.getWorkPackage()) != null) {

            LOGGER.info("Starting import of work " + workDir.getName());

            Path tempOldWorkFiles = null;
            Work presentWork = null;
            Work newWork = null;
            boolean importSuccessful = true;

            try {
                // Get the mets file
                File mets = new File(workDir, workDir.getName() + ".xml");
                if (!mets.exists()) {
                    throw new ImporterException("Mets file not found, expected at " + mets.getAbsolutePath());
                }

                // Read the work data from the mets/mods file.
                newWork = workDataReader.read(mets);

                // Validate import data
                importValidation.validate(newWork, mets);

                newWork.setPath(Paths.get(importerProperties.getWorkFilesPath(), newWork.getId()).toString());

                //check that naming of folder and mets.xml concedes with workId, otherwise rename
                if (!StringUtils.equals(newWork.getId(), workDir.getName())) {
                    LOGGER.info("Id of work to import: " + newWork.getId() + " is different from the mets file name "
                            + workDir.getName() + ", renaming.");

                    Files.move(mets.toPath(), Paths.get(mets.getParent(), newWork.getId() + ".xml"));
                    Path newPath = Paths.get(workDir.getParent(), newWork.getId());
                    Files.move(workDir.toPath(), newPath);
                    workDir = newPath.toFile();
                }

                // Check in the database if this work is already present
                // and if there are identifiers associated to another work.
                presentWork = workChecker.check(newWork);

                // If the work is already present, it should be replaced.
                if (presentWork != null) {

                    LOGGER.info("Work " + newWork.getId() + " already present, replacing");

                    newWork.setEnabled(presentWork.isEnabled());

                    // Files created and cached by the fileserver must be deleted.
                    cacheDeleteAction.perform(presentWork, null);

                    // Move old work files to a temporary folder.
                    if (new File(presentWork.getPath()).isDirectory()) {
                        tempOldWorkFiles = Paths.get(importerProperties.getTempWorkFolderPath(), presentWork.getId());
                        Files.move(
                                Paths.get(presentWork.getPath()),
                                tempOldWorkFiles
                        );
                    } else {
                        LOGGER.warn("The alleged root path " + presentWork.getPath() + " of the already present work "
                                + presentWork.getId() + " is not a directory. Old files cannot be moved "
                                + "to temporary folder");
                    }
                }

                // Move work files to the production root.
                Files.move(
                        workDir.toPath(),
                        Paths.get(newWork.getPath())
                );
                workDir = new File(newWork.getPath());

                // Insert the work data into the database, updating if old data present.
                workService.updateWork(newWork);

                LOGGER.info("Finished import of work " + workDir.getName());

            } catch (Exception e) {

                importSuccessful = false;
                boolean rollbackSuccessful = true;

                LOGGER.error("An error occured importing work " + workDir.getName()
                        + ", Error: " + e, e);

                if (newWork != null && newWork.getId() != null) {
                    try {
                        LOGGER.info("Rollback: deleting database entry for work " + workDir.getName());
                        workService.deleteWork(newWork);
                    } catch (Exception rollbackExc) {
                        LOGGER.error("An error occurred during rollback of work " + workDir.getName() + ": " + rollbackExc, rollbackExc);
                        rollbackSuccessful = false;
                    }
                }

                if (presentWork != null) {
                    try {
                        // restore old work in database
                        LOGGER.info("Rollback: restoring old work data for work " + presentWork.getId());
                        workService.updateWork(presentWork);

                        // restore old work files
                        if (tempOldWorkFiles != null) {
                            LOGGER.info("Rollback: restoring old files for work " + presentWork.getId());
                            Files.move(
                                    tempOldWorkFiles,
                                    Paths.get(presentWork.getPath())
                            );
                        }
                    } catch (Exception rollbackExc) {
                        LOGGER.error("An error occurred during rollback of work " + workDir.getName() + ": " + rollbackExc, rollbackExc);
                        rollbackSuccessful = false;
                    }
                }

                // move import files to error folder
                try {
                    LOGGER.info("Rollback: moving all files for work " + workDir.getName() + " to error folder.");
                    Files.move(
                            workDir.toPath(),
                            Paths.get(importerProperties.getErrorFolderPath(), workDir.getName() + "_" + LocalDateTime.now())
                    );
                } catch (Exception rollbackExc) {
                    LOGGER.error("An error occurred during rollback of work " + workDir.getName() + ": " + rollbackExc, rollbackExc);
                    rollbackSuccessful = false;
                }

                if (!rollbackSuccessful) {
                    throw new ImporterException("The rollback during import of work " + workDir.getName() + " failed. "
                            + "Interrupting import process.");
                }
            }

            // Actions to be performed after a successful import, that are not critical for the import as such and doesn't need a rollback.
            if (importSuccessful) {
                LOGGER.info("Performing subsequent actions for work " + workDir.getName());
                try {
                    // Delete temporary files.
                    if (tempOldWorkFiles != null) {
                        fileDeleter.delete(tempOldWorkFiles);
                    }

                    // TODO Perform all defined direct import actions (doi registration…).

                    // TODO Order all defined asynchronous import actions (creation of additional files…).

                    LOGGER.info("Triggering indexing of work " + workDir.getName());
                    // TODO Trigger indexing in presentation system (configurable).


                } catch (Exception e) {
                    String message = "An error occured after import of work " + workDir.getName()
                            + ". The import itself was succuessfully performed not all the subsequent actions."
                            + " The work has possibly not been indexed. Error: " + e;
                    LOGGER.error(message, e);
                    // TODO notify
                }
            }
        }
        LOGGER.info("Nothing (more) to import.");
    }

}
