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

package org.kitodo.mediaserver.importer.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.kitodo.mediaserver.importer.config.ImporterProperties;
import org.kitodo.mediaserver.importer.exceptions.ImporterException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Utilities for the importer.
 */
public class ImporterUtils {

    private ImporterProperties importerProperties;

    @Autowired
    public void setImporterProperties(ImporterProperties importerProperties) {
        this.importerProperties = importerProperties;
    }

    /**
     * Searches the import hotfolder for a new work to import. If a work is found, it is moved to a subdirectory of
     * the import-in-progress folder.
     *
     * <p>
     * Checks first if there are xml files directly in the hotfolder. If so, it is assumed that this is a METS/MODS
     * file, and this file and all directories with the xml file name as prefix are moved into a new subfolder in the
     * importing directory.
     *
     * <p>
     * If no xml file is found, the first directory found is copied to the importing directory, assuming this is a
     * process folder for the complete work.
     *
     * @return the directory with the work to import, null if no work is found.
     * @throws Exception if a severe error occurs.
     */
    public File getWorkPackage() throws Exception {
        File hotfolder = new File(importerProperties.getHotfolderPath());
        File importingFolder  = new File(importerProperties.getImportingFolderPath());

        if (hotfolder == null || !hotfolder.isDirectory()) {
            throw new ImporterException("The hotfolder '" + importerProperties.getHotfolderPath()
                    + "' is not a directory, please check your configuration");
        }
        if (importingFolder == null) {
            throw new ImporterException("The import-in-progress folder '" + importerProperties.getImportingFolderPath()
                    + "' is not defined, please check your configuration");
        }
        importingFolder.mkdirs();

        // At first, check for an xml file direct under the hotfolder
        Optional<File> optionalXmlFile = FileUtils
                .listFiles(hotfolder, new String[] {"xml"}, false)
                .stream()
                .findFirst();

        if (optionalXmlFile.isPresent()) {
            File xmlFile = optionalXmlFile.get();
            String workId = FilenameUtils.removeExtension(xmlFile.getName());

            File importWorkDir = new File(importingFolder, workId);
            importWorkDir.mkdirs();

            moveFile(xmlFile, importWorkDir);

            Collection<File> dirs = FileUtils
                    .listFilesAndDirs(
                            hotfolder,
                            FalseFileFilter.FALSE,
                            new WildcardFileFilter(workId + "*"))
                    .stream()
                    .filter(dir -> !dir.equals(hotfolder))
                    .collect(Collectors.toList());

            for (File dir : dirs) {
                moveDir(dir, importWorkDir);
            }

            return importWorkDir;
        }

        // If there is no xml file directly under the hotfolder, check for a directory
        Optional<File> optionalDir = FileUtils
                .listFilesAndDirs(
                        hotfolder,
                        FalseFileFilter.FALSE,
                        TrueFileFilter.TRUE)
                .stream()
                .filter(dir -> !dir.equals(hotfolder))
                .filter(File::isDirectory)
                .findFirst();

        if (optionalDir.isPresent()) {
            moveDir(optionalDir.get(), importingFolder);

            return new File(importingFolder, optionalDir.get().getName());
        }

        return null;
    }

    /**
     * Moves a directory to a target parent directory. If an error occurs, an attempt is made to move the directory
     * to the error folder.
     *
     * @param dir the directory to move
     * @param targetParentDir the target parent
     * @throws ImporterException if an error occurs
     */
    public void moveDir(File dir, File targetParentDir) throws ImporterException {
        try {
            FileUtils.moveDirectory(
                    dir,
                    new File(targetParentDir, dir.getName()));
        } catch (IOException e) {
            // If an error occurs, try to move the directory to the error folder.
            try {
                File errorFolder = new File(importerProperties.getErrorFolderPath());
                FileUtils.moveDirectory(
                        dir,
                        new File(errorFolder, dir.getName()));
            } catch (Exception e1) {
                throw new ImporterException("Import interrupted: " + e.toString() + ", additionally couldn't move dir "
                        + dir.getAbsolutePath() + " to error folder: " + e1.toString(), e1);
            }
            throw new ImporterException("Import interrupted: " + e.toString(), e);
        }
    }

    /**
     * Moves a file to a target parent directory. If an error occurs, an attempt is made to move the file
     * to the error folder.
     *
     * @param file the file to move
     * @param targetParentDir the target parent
     * @throws ImporterException if an error occurs
     */
    public void moveFile(File file, File targetParentDir) throws ImporterException {
        try {
            FileUtils.moveFile(
                    file,
                    new File(targetParentDir, file.getName()));
        } catch (IOException e) {
            // If an error occurs, try to move the file to the error folder.
            try {
                File errorFolder  = new File(importerProperties.getErrorFolderPath());
                FileUtils.moveFile(
                        file,
                        new File(errorFolder, file.getName()));
            } catch (Exception e1) {
                throw new ImporterException("Import interrupted: " + e.toString() + ", additionally couldn't move file "
                        + file.getAbsolutePath() + " to error folder: " + e1.toString(), e1);
            }
            throw new ImporterException("Import interrupted: " + e.toString(), e);
        }
    }

}
