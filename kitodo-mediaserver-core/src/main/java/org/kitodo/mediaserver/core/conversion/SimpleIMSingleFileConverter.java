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
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * A simple single file converter using imagemagick.
 * Implemented as proof of concept.
 */
public class SimpleIMSingleFileConverter extends AbstractConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIMSingleFileConverter.class);

    @Autowired
    private ConversionProperties conversionProperties;
    
    /**
     * Converts a given file. Returns an input stream with the result.
     *
     * @param master    the master file
     * @param parameter a map of parameter
     * @return an output stream of the converted file
     * @throws Exception by fatal errors
     */
    @Override
    public InputStream convert(File master, Map<String, String> parameter) throws Exception {

        checkParams(master, parameter, "derivativePath");

        int size = getConversionSize(parameter);

        File convertedFile = new File(conversionTargetPath, parameter.get("derivativePath"));

        // if the cache file already exists, there is another thread already performing the conversion.
        boolean fileAlreadyExists = createCacheFile(convertedFile);

        if (!fileAlreadyExists) {
            IMOperation operation = new IMOperation();
            operation.addImage(master.getAbsolutePath());
            operation.resize(size);
            operation.addImage(convertedFile.getAbsolutePath());

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
