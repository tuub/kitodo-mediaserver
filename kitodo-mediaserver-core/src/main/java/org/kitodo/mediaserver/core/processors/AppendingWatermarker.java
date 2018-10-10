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

package org.kitodo.mediaserver.core.processors;

import java.awt.Color;
import java.io.File;
import org.apache.commons.lang.StringUtils;
import org.im4java.core.IMOperation;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An implementation of IWatermarker simply using append to add a logo to the image file.
 */
public class AppendingWatermarker extends AbstractWatermarker {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppendingWatermarker.class);

    private ConversionProperties.Watermark conversionPropertiesWatermark;
    private ConversionProperties.Watermark.ImageMode conversionPropertiesWatermarkImageMode;
    private ConversionProperties.Watermark.CanvasExtension conversionPropertiesCanvasExtension;

    @Autowired
    public void setConversionPropertiesWatermark(ConversionProperties.Watermark conversionPropertiesWatermark) {
        this.conversionPropertiesWatermark = conversionPropertiesWatermark;
    }

    @Autowired
    public void setConversionPropertiesWatermarkImageMode(ConversionProperties.Watermark.ImageMode conversionPropertiesWatermarkImageMode) {
        this.conversionPropertiesWatermarkImageMode = conversionPropertiesWatermarkImageMode;
    }

    @Autowired
    public void setConversionPropertiesCanvasExtension(ConversionProperties.Watermark.CanvasExtension conversionPropertiesCanvasExtension) {
        this.conversionPropertiesCanvasExtension = conversionPropertiesCanvasExtension;
    }

    /**
     * Takes an IMOperation object and adds watermark operation commands.
     *
     * @param operation  An existing IMOperation object that the method works on.
     * @param masterFile A master file object, needed for calculating x and y values.
     * @param size       The requested x size in pixels
     */
    @Override
    public void perform(IMOperation operation, File masterFile, Integer size) throws Exception {

        String logoPath = conversionPropertiesWatermarkImageMode.getPath();
        if (StringUtils.isBlank(logoPath)) {
            LOGGER.error("Watermarking is set but the watermark image path is empty.");
            return;
        }
        File logo = new File(logoPath);
        if (logo == null || !logo.isFile() || !logo.canRead()) {
            LOGGER.error("Watermarking is set but the watermark file '" + logoPath + "' cannot be read.");
            return;
        }

        // Get the background color RGB value from configuration
        Color color = getRGBColor(conversionPropertiesCanvasExtension.getBackgroundColorRGB());
        String rgbColor = "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";

        operation.addImage(logoPath);
        operation.background(rgbColor);
        operation.opaque("none");
        operation.gravity(conversionPropertiesWatermark.getGravity());
        operation.append();
        operation.flatten();
        operation.colorspace("RGB"); // Needed for firefox

    }
}
