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
import java.util.Map;
import org.kitodo.mediaserver.core.exceptions.ValidationException;

/**
 * Interface for validations of mets files and data.
 */
public interface IMetsValidation {

    /**
     * Validates the contents or structure of a METS/MODS file.
     *
     * @param mets the METS/MODS file
     * @throws ValidationException if the validation fails
     */
    void validate(File mets, Map<String, String> parameter) throws ValidationException;
}
