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
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileExistsException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IDataReader;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.services.ActionService;
import org.kitodo.mediaserver.core.services.WorkService;
import org.kitodo.mediaserver.core.util.FileDeleter;
import org.kitodo.mediaserver.core.util.Notifier;
import org.kitodo.mediaserver.importer.api.IImportValidation;
import org.kitodo.mediaserver.importer.api.IWorkChecker;
import org.kitodo.mediaserver.importer.config.ImporterProperties;
import org.kitodo.mediaserver.importer.exceptions.ImporterException;
import org.kitodo.mediaserver.importer.util.ImporterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Flow control class for the importer.
 */
@Component
public class ImporterFlowControl {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImporterFlowControl.class);

    @Autowired
    private ObjectFactory<Notifier> notifierFactory;

    private ImporterUtils importerUtils;
    private ImporterProperties importerProperties;
    private IImportValidation importValidation;
    private IDataReader workDataReader;
    private IWorkChecker workChecker;
    private WorkService workService;
    private ActionService actionService;
    private FileDeleter fileDeleter;
    private IAction cacheDeleteAction;
    private IAction viewerIndexingAction;

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
    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    @Autowired
    public void setFileDeleter(FileDeleter fileDeleter) {
        this.fileDeleter = fileDeleter;
    }

    @Autowired
    public void setCacheDeleteAction(IAction cacheDeleteAction) {
        this.cacheDeleteAction = cacheDeleteAction;
    }

    @Autowired
    public void setViewerIndexingAction(IAction viewerIndexingAction) {
        this.viewerIndexingAction = viewerIndexingAction;
    }


    /**
     * Controls the importer algorithm.
     *
     * @throws Exception if a severe error occurs
     */
    public void importWorks() throws Exception {

        File workDir;
        File mets = null;
        Notifier errorNotifier = notifierFactory.getObject();
        Notifier reportNotifier = notifierFactory.getObject();

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
                mets = new File(workDir, workDir.getName() + ".xml");
                reportNotifier.add("Import: " + mets.getAbsolutePath());
                if (!mets.exists()) {
                    throw new ImporterException("Mets file not found, expected at " + mets.getAbsolutePath());
                }

                // Read the work data from the mets/mods file.
                newWork = workDataReader.read(mets);

                //check that naming of folder and mets.xml concedes with workId, otherwise rename
                if (!StringUtils.equals(newWork.getId(), workDir.getName())) {
                    LOGGER.info("Id of work to import: " + newWork.getId() + " is different from the mets file name "
                            + workDir.getName() + ", renaming.");
                    reportNotifier.add("Renamed METS file from " + mets.toPath() + " to " + newWork.getId() + ".xml");

                    String newMetsName = newWork.getId() + ".xml";
                    Files.move(mets.toPath(), Paths.get(mets.getParent(), newMetsName));
                    Path newPath = Paths.get(workDir.getParent(), newWork.getId());
                    Files.move(workDir.toPath(), newPath);
                    workDir = newPath.toFile();
                    mets = new File(workDir, newMetsName);
                }

                // Validate import data
                importValidation.validate(newWork, mets);

                newWork.setPath(Paths.get(importerProperties.getWorkFilesPath(), newWork.getId()).toString());

                // Check in the database if this work is already present
                // and if there are identifiers associated to another work.
                presentWork = workChecker.check(newWork);

                // If the work is already present, it should be replaced.
                if (presentWork != null) {

                    LOGGER.info("Work " + newWork.getId() + " already present, replacing");
                    reportNotifier.add("Replaced already present work.");

                    newWork.setAllowedNetwork(presentWork.getAllowedNetwork());

                    // Files created and cached by the fileserver must be deleted.
                    cacheDeleteAction.perform(presentWork, null);

                    // Move old work files to a temporary folder.
                    if (new File(presentWork.getPath()).isDirectory()) {
                        tempOldWorkFiles = Paths.get(importerProperties.getTempWorkFolderPath(), presentWork.getId());
                        LOGGER.debug("Move from='" + presentWork.getPath() + "' to='" + tempOldWorkFiles + "'");
                        FileUtils.moveDirectory(
                            new File(presentWork.getPath()),
                            tempOldWorkFiles.toFile()
                        );
                    } else {
                        LOGGER.warn("The alleged root path " + presentWork.getPath() + " of the already present work "
                                + presentWork.getId() + " is not a directory. Old files cannot be moved "
                                + "to temporary folder");
                    }
                }

                // Move work files to the production root.
                try {
                    LOGGER.debug("Move from='" + workDir + "' to='" + newWork.getPath() + "'");
                    FileUtils.moveDirectory(
                        workDir,
                        new File(newWork.getPath())
                    );
                    reportNotifier.add("Moved work files to production on " + newWork.getPath());
                } catch (FileExistsException ex) {
                    String message = "Work directory '" + newWork.getPath() + "' already exists but there is no DB entry for workId='"
                        + newWork.getId() + "'. Not importing work from '" + workDir + "'";
                    errorNotifier.add(message);
                    throw new ImporterException(message, ex);
                }
                workDir = new File(newWork.getPath());

                // Insert the work data into the database, updating if old data present.
                workService.updateWork(newWork);

                LOGGER.info("Finished import of work " + workDir.getName());
                reportNotifier.add("Imported: " + mets.getAbsolutePath());

            } catch (Exception e) {

                importSuccessful = false;
                boolean rollbackSuccessful = true;

                LOGGER.error("An error occurred importing work " + workDir.getName()
                        + ", performing rollback. Error: " + e, e);
                errorNotifier.add(workDir.getName() + "Error: " + e);

                if (newWork != null && newWork.getId() != null) {
                    try {
                        LOGGER.info("Rollback: deleting database entry for work " + workDir.getName());
                        workService.deleteWork(newWork);
                        errorNotifier.add("Deleted work from database.");
                    } catch (Exception rollbackExc) {
                        LOGGER.error("An error occurred during rollback of work " + workDir.getName() + ": " + rollbackExc, rollbackExc);
                        errorNotifier.add("Could not delete work from database.");
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
                            LOGGER.debug("Move from='" + tempOldWorkFiles + "' to='" + presentWork.getPath() + "'");
                            FileUtils.moveDirectory(
                                tempOldWorkFiles.toFile(),
                                new File(presentWork.getPath())
                            );
                            errorNotifier.add("Restored old work data and files for " + presentWork.getId());
                        }
                    } catch (Exception rollbackExc) {
                        LOGGER.error("An error occurred during rollback of work " + workDir.getName() + ": " + rollbackExc, rollbackExc);
                        errorNotifier.add("Failed restore of old work data and files for " + presentWork.getId());
                        rollbackSuccessful = false;
                    }
                }

                try {
                    // move import files to error folder
                    LOGGER.info("Rollback: moving all files for work " + workDir.getName() + " to error folder.");
                    LOGGER.debug("Move from='" + workDir + "' to='" + new File(importerProperties.getErrorFolderPath(),
                        workDir.getName() + "_" + LocalDateTime.now()) + "'");
                    FileUtils.moveDirectory(
                        workDir,
                        new File(importerProperties.getErrorFolderPath(), workDir.getName() + "_" + LocalDateTime.now())
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

                try {
                    // Delete temporary files.
                    if (tempOldWorkFiles != null) {
                        fileDeleter.delete(tempOldWorkFiles);
                    }

                    LOGGER.info("Performing actions before indexing work " + workDir.getName());
                    performActions(importerProperties.getActionsBeforeIndexing(), newWork, false);

                    // Perform indexing of the work
                    if (importerProperties.isIndexWorkAfterImport()) {
                        LOGGER.info("Triggering indexing of work " + newWork.getId());

                        try {
                            viewerIndexingAction.perform(newWork, null);

                            LOGGER.info("Performing actions after indexing work " + workDir.getName());
                            performActions(importerProperties.getActionsAfterSuccessfulIndexing(), newWork, false);

                        } catch (Exception e) {
                            LOGGER.error("Error indexing " + newWork.getId() + ": " + e + ". Actions after indexing not performed", e);
                            errorNotifier.add("No indexing of work " + newWork.getId());
                        }
                    }

                    LOGGER.info("Requesting actions after indexing work " + workDir.getName());
                    performActions(importerProperties.getActionsToRequestAsynchronously(), newWork, true);

                } catch (Exception e) {
                    String message = "An error occured after import of work " + workDir.getName()
                            + ". The import itself was successfully performed not all the subsequent actions."
                            + " The work has probably not been indexed. Error: " + e;
                    LOGGER.error(message, e);
                    errorNotifier.add(message);
                }
            }
        }
        LOGGER.info("Nothing (more) to import.");

        if (errorNotifier.getCollectedNotification().length() > 0) {
            errorNotifier.send("Error: Indexing Action", importerProperties.getErrorNotificationEmail());
        }

        reportNotifier.send("Report: Import Action", importerProperties.getReportNotificationEmail());
    }

    private void performActions(List<Map<String, Map<String, String>>> actionList, Work work, boolean request) throws Exception {
        for (Map<String, Map<String, String>> actionMap : actionList) {
            if (actionMap.keySet() == null || actionMap.keySet().size() != 1) {
                LOGGER.error("The list of actions must be an unambiguous list with single action names as keys."
                        + " Please check your configuration.");
            } else {
                for (String action : actionMap.keySet()) {
                    String message = "action " + action + " with parameter map " + actionMap.get(action);

                    if (request) {
                        LOGGER.info("Requesting " + message);
                        actionService.request(work, action, actionMap.get(action));
                    } else {
                        LOGGER.info("Performing " + message);
                        actionService.performImmediately(work, action, actionMap.get(action));
                    }
                }
            }
        }
    }

}
