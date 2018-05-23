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
     * @return an object with a result (optionally)
     * @throws ValidationException if the validation fails
     */
    Object validate(File mets, Map<String, String> parameter) throws ValidationException;
}
