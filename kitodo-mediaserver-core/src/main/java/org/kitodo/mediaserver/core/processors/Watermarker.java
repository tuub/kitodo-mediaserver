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
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import org.im4java.core.IMOperation;
import org.kitodo.mediaserver.core.api.IWatermarker;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An implementation of IWatermarker.
 */
public class Watermarker implements IWatermarker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Watermarker.class);

    private ConversionProperties.Jpeg conversionPropertiesJpeg;
    private ConversionProperties.Watermark conversionPropertiesWatermark;
    private ConversionProperties.Watermark.TextMode conversionPropertiesWatermarkTextMode;
    private ConversionProperties.Watermark.ImageMode conversionPropertiesWatermarkImageMode;
    private ConversionProperties.Watermark.CanvasExtension conversionPropertiesCanvasExtension;

    @Autowired
    public void setConversionPropertiesJpeg(ConversionProperties.Jpeg conversionPropertiesJpeg) {
        this.conversionPropertiesJpeg = conversionPropertiesJpeg;
    }

    @Autowired
    public void setConversionPropertiesWatermark(ConversionProperties.Watermark conversionPropertiesWatermark) {
        this.conversionPropertiesWatermark = conversionPropertiesWatermark;
    }

    @Autowired
    public void setConversionPropertiesWatermarkTextMode(ConversionProperties.Watermark.TextMode conversionPropertiesWatermarkTextMode) {
        this.conversionPropertiesWatermarkTextMode = conversionPropertiesWatermarkTextMode;
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
     * Adds watermarking parameters to the given IMOperation string.
     *
     * @param operation The existing operation string
     * @param masterFile The masterfile object
     * @param srcFileX The x size of the file to produce
     * @throws Exception If something goes wrong
     */
    @Override
    public void perform(IMOperation operation, File masterFile, Integer srcFileX) throws Exception {

        // Only perform if configured minimum width matches
        if (srcFileX >= conversionPropertiesWatermark.getMinSize()) {
            try {
                LOGGER.info("Adding watermark to file, using " + conversionPropertiesWatermark.getRenderMode() + " mode");

                final Float scale = ((float) srcFileX / conversionPropertiesJpeg.getDefaultSize()) * 100;

                // Calculating the aspect ratio
                BufferedImage masterFileImage = ImageIO.read(masterFile.getAbsoluteFile());
                Integer masterFileX = masterFileImage.getWidth();
                Integer masterFileY = masterFileImage.getHeight();
                final Float aspectRatio = ((float) srcFileX * 100) / masterFileX;
                final Float srcFileY = (aspectRatio / 100) * masterFileY;

                // Start adding to given IMOperation
                operation.colorspace("RGB");
                operation.quality(100.00);

                if (conversionPropertiesCanvasExtension.isEnabled()) {

                    // Scale the configured canvas extension with the current resolution
                    Integer extendX = srcFileX + getScaledValue(conversionPropertiesCanvasExtension.getAddX(), scale);
                    Integer extendY = srcFileY.intValue() + getScaledValue(conversionPropertiesCanvasExtension.getAddY(), scale);

                    // Get the background color RGB value from configuration
                    Color color = getRGBColor(conversionPropertiesCanvasExtension.getBackgroundColorRGB());
                    String rgbColor = "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";

                    operation.background(rgbColor);
                    operation.extent(extendX, extendY);
                }

                if (conversionPropertiesWatermark.getRenderMode().equals("image")) {

                    // Get the size of the watermark image ...
                    String watermarkImageFilePath = conversionPropertiesWatermarkImageMode.getPath();
                    Image watermarkImage = ImageIO.read(new File(watermarkImageFilePath));

                    // ... for calculating the scaled dimensions
                    Integer watermarkX = getScaledValue(watermarkImage.getWidth(null), scale);
                    Integer watermarkY = getScaledValue(watermarkImage.getHeight(null), scale);
                    Integer offsetX = getScaledValue(conversionPropertiesWatermark.getOffsetX(), scale);
                    Integer offsetY = getScaledValue(conversionPropertiesWatermark.getOffsetY(), scale);

                    operation.addImage(watermarkImageFilePath);
                    operation.gravity(conversionPropertiesWatermark.getGravity());
                    operation.geometry(watermarkX, watermarkY, offsetX, offsetY);
                    operation.composite();

                } else if (conversionPropertiesWatermark.getRenderMode().equals("text")) {

                    // Building the text overlay in IM specific syntax
                    String drawCmd = "text "
                            + getScaledValue(conversionPropertiesWatermark.getOffsetX(), scale)
                            + ","
                            + getScaledValue(conversionPropertiesWatermark.getOffsetY(), scale)
                            + " \'"
                            + conversionPropertiesWatermarkTextMode.getContent()
                            + "\'";

                    // Scale the font size after dimensions
                    Integer fontSize = getScaledValue(conversionPropertiesWatermarkTextMode.getSize(), scale);

                    // Get the font color RGB value from configuration
                    Color color = getRGBColor(conversionPropertiesWatermarkTextMode.getColorRGB());
                    String rgbColor = "rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")";

                    operation.font(conversionPropertiesWatermarkTextMode.getFont());
                    operation.gravity(conversionPropertiesWatermark.getGravity());
                    operation.pointsize(fontSize);
                    operation.fill(rgbColor);
                    operation.draw(drawCmd);
                }
            } catch (Exception ex) {
                LOGGER.error("Could not apply watermark to file : " + ex.toString());
                throw ex;
            }
        }
    }

    /**
     * Calculates a value, based on a given percentage value.
     *
     * @param value The integer value to scale
     * @param scale The float percentage value
     * @return The scaled value or 0
     * @throws NumberFormatException If the given numbers have the wrong type
     */
    protected Integer getScaledValue(Integer value, Float scale) throws NumberFormatException {
        try {
            Float scaledValue = (float)value / 100 * scale;
            return scaledValue.intValue();
        } catch (NumberFormatException ex) {
            LOGGER.info("Wrong number type : " + ex.toString(), ex);
            return 0;
        }
    }

    /**
     * Returns Color object from RGB color value string.
     *
     * @param colorStr The string containing RGB values, e.g. "33,44,55"
     * @return A Color object or NULL.
     */
    protected Color getRGBColor(String colorStr) throws IllegalArgumentException {
        try {
            String[] colorArr = colorStr.split(",");
            return new Color(
                    Integer.parseInt(colorArr[0].trim()),
                    Integer.parseInt(colorArr[1].trim()),
                    Integer.parseInt(colorArr[2].trim())
            );
        } catch (IllegalArgumentException ex) {
            LOGGER.error("Invalid rgb color value : " + ex.toString(), ex);
            return null;
        }
    }
}
