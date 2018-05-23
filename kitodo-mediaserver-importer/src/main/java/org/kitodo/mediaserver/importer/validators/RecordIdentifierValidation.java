package org.kitodo.mediaserver.importer.validators;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.kitodo.mediaserver.core.api.IMetsReader;
import org.kitodo.mediaserver.core.exceptions.ValidationException;
import org.kitodo.mediaserver.importer.api.IMetsValidation;
import org.springframework.util.CollectionUtils;

/**
 *
 */
public class RecordIdentifierValidation implements IMetsValidation {

    private IMetsReader recordIdentifierReader;

    public void setRecordIdentifierReader(IMetsReader recordIdentifierReader) {
        this.recordIdentifierReader = recordIdentifierReader;
    }

    /**
     * Checks that there is exactly one record identifier in the METS/MODS file and returns it.
     *
     * @param mets the METS/MODS file
     * @return the identifier
     * @throws ValidationException if the validation fails
     */
    @Override
    public String validate(File mets, Map<String, String> parameter) throws ValidationException {

        try {
            List<String> recordIdentifiers = recordIdentifierReader.read(mets);

            if (CollectionUtils.isEmpty(recordIdentifiers)) {
                throw new ValidationException("No record identifier found.");
            }

            // remove duplicates
            recordIdentifiers = recordIdentifiers.stream()
                    .distinct()
                    .collect(Collectors.toList());

            if (recordIdentifiers.size() > 1) {
                throw new ValidationException("Multiple record identifiers found: " + recordIdentifiers);
            }

            return recordIdentifiers.get(0);

        } catch (Exception e) {
            throw new ValidationException("Validation failed due to unexpected error: " + e, e);
        }
    }
}
