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
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.lang.StringUtils;
import org.kitodo.mediaserver.core.api.IConverter;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Abstract converter offering a synchronized method for creating cache file.
 */
public abstract class AbstractConverter implements IConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractConverter.class);

    protected boolean saveConvertedFile;

    protected String conversionTargetPath;

    @Autowired
    protected ConversionProperties conversionProperties;

    @Autowired
    protected ConversionProperties.Jpeg conversionPropertiesJpeg;

    @Autowired
    protected ConversionProperties.Pdf conversionPropertiesPdf;

    @Autowired
    protected ConversionProperties.Watermark conversionPropertiesWatermark;

    @Autowired
    protected MediaServerUtils mediaServerUtils;

    public void setSaveConvertedFile(boolean saveConvertedFile) {
        this.saveConvertedFile = saveConvertedFile;
    }

    public void setConversionTargetPath(String conversionTargetPath) {
        this.conversionTargetPath = conversionTargetPath;
    }

    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    /**
     * Checks that all required parameter are present.
     *
     * @param pages          a map (key=sorting order of files) of maps (key={master,fulltext,...}) with work files
     * @param parameter      the parameter map
     * @param requiredParams required parameter
     * @throws Exception by fatal errors
     */
    protected void checkParams(TreeMap<Integer, Map<String, FileEntry>> pages, Map<String, Object> parameter, String... requiredParams) {

        if (pages == null) {
            throw new IllegalArgumentException("'pages' must not be null.");
        }
        if (pages.size() == 0) {
            throw new IllegalArgumentException("'pages' must not be empty.");
        }

        pages.values().forEach(page -> {
            if (page == null || page.get("master") == null) {
                throw new IllegalArgumentException("There is no master file defined.");
            }
            File file = page.get("master").getFile();
            if (file == null || !file.canRead() || !file.isFile()) {
                throw new IllegalArgumentException("The master file " + file + " does not exist or cannot be read.");
            }
        });

        mediaServerUtils.checkForRequiredParameter(parameter, requiredParams);
    }

    /**
     * Gets the conversion size from the parameter map. If no parameter is found, it delivers the default size.
     *
     * @param parameter the parameter map
     * @return a size
     */
    protected int getConversionSize(Map<String, Object> parameter) {
        try {
            return (Integer)parameter.get("size");
        } catch (NumberFormatException | NullPointerException e) {
            LOGGER.warn("The requested size " + parameter.get("size") + " is not a number. Using default size.");
            if (StringUtils.equals((String)parameter.get("target_mime"), "application/pdf")) {
                return conversionPropertiesPdf.getDefaultSize();
            }
            return conversionPropertiesJpeg.getDefaultSize();
        }
    }

    /**
     * Checks if derivative file not exists and creates it.
     *
     * @param derivativePath relative path to the file
     * @return Map with key=file and value=alreadyExists
     * @throws IOException if the file could not be created
     */
    protected synchronized Map.Entry<File, Boolean> createDerivativeFile(String derivativePath) throws IOException {
        File file;
        boolean exists = false;
        if (saveConvertedFile) {
            file = new File(conversionTargetPath, derivativePath);
            file.getParentFile().mkdirs();
            exists = !file.createNewFile();
        } else {
            file = File.createTempFile("derivative_", null);
        }
        return new AbstractMap.SimpleEntry<>(file, exists);
    }

    /**
     * Delete the derivative if needed.
     *
     * @param file derivative file
     */
    protected synchronized void cleanDerivativeFile(File file) {
        if (!saveConvertedFile) {
            if (file == null) {
                throw new IllegalArgumentException("Argument file must not be null.");
            }
            LOGGER.info("Deleting file " + file.getAbsolutePath());
            file.delete();
        }
    }
}
