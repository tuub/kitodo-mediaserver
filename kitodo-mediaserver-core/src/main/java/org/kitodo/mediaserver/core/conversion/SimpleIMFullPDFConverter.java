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

package org.kitodo.mediaserver.core.conversion;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.ImageCommand;
import org.kitodo.mediaserver.core.api.IFullConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleIMFullPDFConverter extends AbstractConverter implements IFullConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIMFullPDFConverter.class);

    @Override
    public InputStream convert(Map<Integer, File> pages, Map<String, String> parameter) throws Exception {

        mediaServerUtils.checkForRequiredParameter(parameter, "derivativePath", "target_mime");

        // add output file
        File convertedFile = new File(conversionTargetPath, parameter.get("derivativePath"));
        boolean fileAlreadyExists = createCacheFile(convertedFile);

        if (!fileAlreadyExists) {

            IMOperation operation = new IMOperation();

            // add all pages
            pages.forEach((pageNumber, file) -> operation.addImage(file.getAbsolutePath()));

            // settings
            int size = getConversionSize(parameter);
            operation.resize(size);
            operation.colorspace("RGB");

            // add output file
            operation.addImage(convertedFile.getAbsolutePath());

            // run conversion
            LOGGER.debug("Executing IM Operation: " + operation.toString());
            ImageCommand convertCmd = new ConvertCmd(conversionProperties.isUseGraphicsMagick());
            convertCmd.run(operation);
        }

        InputStream convertedInputStream = new FileInputStream(convertedFile);

        if (!saveConvertedFile) {
            LOGGER.info("Deleting file " + convertedFile.getAbsolutePath());
            convertedFile.delete();
        }

        return convertedInputStream;
    }
}
