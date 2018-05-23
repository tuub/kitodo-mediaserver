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

package org.kitodo.mediaserver.importer.validators;

import java.io.File;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.exceptions.ValidationException;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.kitodo.mediaserver.importer.config.ImporterProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A validator for imported files.
 */
public class FileOccurrenceValidation implements IMetsValidation {


    private static final Logger LOGGER = LoggerFactory.getLogger(FileOccurrenceValidation.class);

    private IMetsReader metsUrlReader;
    private MediaServerUtils mediaServerUtils;
    private ImporterProperties importerProperties;

    public void setMetsUrlReader(IMetsReader metsUrlReader) {
        this.metsUrlReader = metsUrlReader;
    }

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    @Autowired
    public void setImporterProperties(ImporterProperties importerProperties) {
        this.importerProperties = importerProperties;
    }

    /**
     * Checks that all files of a certain file group in a mets file are present at import.
     *
     * @param mets the mets file
     * @throws ValidationException if the validation fails
     */
    @Override
    public void validate(File mets, Map<String, String> parameter) throws ValidationException {

        mediaServerUtils.checkForRequiredParameter(parameter, "fileGrpId", "workId");

        List<String> urls;
        List<String> missingFiles = new ArrayList<>();

        try {
            urls = metsUrlReader.read(mets, new AbstractMap.SimpleEntry<>("fileGrpId", parameter.get("fileGrpId")));
        } catch (Exception e) {
            throw new ValidationException("Validation failed due to unexpected error: " + e, e);
        }

        // For each file, check if it is in the import, if not, add it to a list of missing files.
        for (String url : urls) {
            Path internalPath = mediaServerUtils.getInternalPathFromUrl(
                    url,
                    importerProperties.getImportingFolderPath(),
                    parameter.get("workId")
            );

            if (!internalPath.toFile().exists()) {
                missingFiles.add(internalPath.toString());
            }
        }

        if (!missingFiles.isEmpty()) {
            throw new ValidationException("The following files are missing: " + missingFiles);
        }
    }
}
