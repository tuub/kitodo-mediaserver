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
import java.util.TreeMap;
import org.im4java.core.ConvertCmd;
import org.im4java.core.IMOperation;
import org.im4java.core.ImageCommand;
import org.kitodo.mediaserver.core.api.IWatermarker;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.util.Notifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * A simple single file converter using imagemagick.
 * Implemented as proof of concept.
 */
public class SimpleIMSingleFileConverter extends AbstractConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleIMSingleFileConverter.class);

    private IWatermarker watermarker;

    public void setWatermarker(IWatermarker watermarker) {
        this.watermarker = watermarker;
    }

    @Autowired
    private FileserverProperties fileserverProperties;

    @Autowired
    private ConversionProperties conversionProperties;

    @Autowired
    protected ConversionProperties.Watermark conversionPropertiesWatermark;


    @Autowired
    private ObjectFactory<Notifier> notifierFactory;

    /**
     * Converts a given file. Returns an input stream with the result.
     *
     * @param pages Map of pages containing files
     * @param parameter a map of parameter
     * @return an output stream of the converted file
     * @throws Exception by fatal errors
     */
    @Override
    public InputStream convert(TreeMap<Integer, Map<String, FileEntry>> pages, Map<String, Object> parameter) throws Exception {

        Notifier notifier = notifierFactory.getObject();
        String message;

        checkParams(pages, parameter, "derivativePath", "target_mime");

        int size = getConversionSize(parameter);

        boolean addWatermark = conversionPropertiesWatermark.isEnabled()
                                && size >= conversionPropertiesWatermark.getMinSize();

        // if the cache file already exists, there is another thread already performing the conversion.
        Map.Entry<File, Boolean> convertedFile = createDerivativeFile((String)parameter.get("derivativePath"));

        if (!convertedFile.getValue()) {
            try {
                IMOperation operation = new IMOperation();
                operation.addImage(pages.get(0).get("master").getFile().getAbsolutePath());
                operation.resize(size);

                if (addWatermark) {
                    try {
                        watermarker.perform(operation, pages.get(0).get("master").getFile(), size);
                    } catch (Exception e) {
                        message = "Error creating watermark on file " + pages.get(0).get("master").getFile().getAbsolutePath() + ": " + e;
                        LOGGER.error(message, e);
                        notifier.addAndSend(message, "Conversion Error", fileserverProperties.getErrorNotificationEmail());
                    }
                }
                operation.colorspace("RGB"); // Needed for firefox
                operation.addImage(convertedFile.getKey().getAbsolutePath());

                ImageCommand convertCmd = new ConvertCmd(conversionProperties.isUseGraphicsMagick());

                convertCmd.run(operation);

                LOGGER.info("Executed IM Operation: " + operation.toString());

            } catch (Exception e) {
                convertedFile.getKey().delete();
                throw e;
            }
        }

        InputStream convertedInputStream = new FileInputStream(convertedFile.getKey());

        cleanDerivativeFile(convertedFile.getKey());

        return convertedInputStream;
    }
}
