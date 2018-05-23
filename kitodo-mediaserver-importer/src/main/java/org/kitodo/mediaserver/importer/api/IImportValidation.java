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

package org.kitodo.mediaserver.importer.api;

import java.io.File;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.ValidationException;

/**
 * Interface for the validation of an imported work.
 */
public interface IImportValidation {

    /**
     * Validates work data and the accompanying METS/MODS file.
     *
     * @param work the work entity
     * @param mets the mets file
     * @throws ValidationException if the validation fails
     */
    void validate(Work work, File mets) throws ValidationException;
}
