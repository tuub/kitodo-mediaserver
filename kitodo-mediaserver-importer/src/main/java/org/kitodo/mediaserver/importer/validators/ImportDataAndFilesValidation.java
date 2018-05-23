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
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.ValidationException;
import org.kitodo.mediaserver.importer.api.IImportValidation;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.kitodo.mediaserver.importer.config.ImporterProperties;
import org.kitodo.mediaserver.importer.control.ImporterFlowControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A validator for the work data and required files in the mets document.
 */
public class ImportDataAndFilesValidation implements IImportValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImporterFlowControl.class);

    private IMetsValidation fileOccurenceValidation;

    private ImporterProperties importerProperties;

    @Autowired
    public void setFileOccurenceValidation(IMetsValidation fileOccurenceValidation) {
        this.fileOccurenceValidation = fileOccurenceValidation;
    }

    @Autowired
    public void setImporterProperties(ImporterProperties importerProperties) {
        this.importerProperties = importerProperties;
    }

    /**
     * Validates work data and the accompanying METS/MODS file.
     *
     * @param work the work entity
     * @param mets the mets file
     * @throws ValidationException if the validation fails
     */
    @Override
    public void validate(Work work, File mets) throws ValidationException {

        // Validate work entity
        if (work.getId() == null) {
            throw new ValidationException("No id found for imported work");
        }

        if (!work.getId().matches(importerProperties.getWorkIdRegex())) {
            throw new ValidationException("The work id '" + work.getId() + "' does not match the regex "
                    + importerProperties.getWorkIdRegex());
        }

        if (StringUtils.isEmpty(work.getTitle())) {
            throw new ValidationException("No title found for the imported work " + work.getId());
        }

        // Validate required files
        for (String fileGrp : importerProperties.getValidationFileGrps()) {
            LOGGER.info("Checking all files with fileGrp " + fileGrp);

            Map<String, String> fileValidatorParams = new HashMap<>();
            fileValidatorParams.put("workId", work.getId());
            fileValidatorParams.put("fileGrpId", fileGrp);

            fileOccurenceValidation.validate(mets, fileValidatorParams);
        }

    }
}
