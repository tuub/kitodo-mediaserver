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

import com.mortennobel.imagescaling.MultiStepRescaleOp;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang.StringUtils;
import org.kitodo.mediaserver.core.api.IPage;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A abstract page for image conversion with image manipulation and watermarking.
 */
public abstract class AbstractPage implements IPage {

    /**
     * Path to the master image.
     */
    protected String imagePath;

    /**
     * Master image object.
     */
    protected Dimension imageSize;

    /**
     * Graphics context of the resized page.
     */
    protected Graphics2D graphics;

    /**
     * Scale factor of the image.
     */
    protected float imageScaling = 1f;

    /**
     * Maximum width and height of the resulting page.
     */
    protected int size = 0;

    /**
     * Calculated page dimensions including extensions.
     */
    protected Dimension pageSize = new Dimension();

    /**
     * Resized image position and size in the new page.
     */
    protected Rectangle imageRect = new Rectangle();

    /**
     * Calculated watermark position and size in the new page.
     */
    protected Rectangle watermarkRect = new Rectangle();

    @Autowired
    protected ConversionProperties.Watermark watermarkProp;

    @Autowired
    protected ConversionProperties.Watermark.CanvasExtension canvasExtensionProp;

    @Autowired
    protected ConversionProperties.Watermark.TextMode textModeProp;

    @Autowired
    protected ConversionProperties.Watermark.ImageMode imageModeProp;

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void setImagePath(@NotNull String path) {
        this.imagePath = path;
    }

    public int getSize() {
        return size;
    }

    @Override
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Resize image and draw watermark onto it.
     *
     * @return the processed image
     * @throws IOException on image file errors
     */
    public BufferedImage renderImage() throws IOException {

        BufferedImage image = ImageIO.read(new File(imagePath));
        imageSize = new Dimension(image.getWidth(), image.getHeight());

        calcDimensions();

        // Resize image and convert to RGB (JPG can not save RGBA)
        // Use bilinear resize: quality OK and very fast
        MultiStepRescaleOp rescaleOp = new MultiStepRescaleOp(imageRect.width, imageRect.height,
            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        BufferedImage resizedImage = rescaleOp.filter(image,
            new BufferedImage(imageRect.width, imageRect.height, BufferedImage.TYPE_INT_RGB));
        BufferedImage pageImage = new BufferedImage(pageSize.width, pageSize.height, BufferedImage.TYPE_INT_RGB);
        graphics = pageImage.createGraphics();

        if (isWatermarkEnabled() && canvasExtensionProp.isEnabled()) {

            // Fill with background color (only needed if image is extended)
            int[] color = Arrays.stream(canvasExtensionProp.getBackgroundColorRGB().split(","))
                .mapToInt(Integer::parseInt).toArray();
            graphics.setColor(new Color(color[0], color[1], color[2]));
            graphics.fill(new Rectangle(pageSize.width, pageSize.height));

            // Move image according to extend direction
            if (StringUtils.containsIgnoreCase(watermarkProp.getGravity(), "west")) {
                imageRect.x = canvasExtensionProp.getAddX();
            }
            if (StringUtils.containsIgnoreCase(watermarkProp.getGravity(), "north")) {
                imageRect.y = canvasExtensionProp.getAddY();
            }
        }

        // Draw the resized image on canvas

        graphics.drawImage(resizedImage, imageRect.x, imageRect.y, null);

        // Draw Watermark
        if (isWatermarkEnabled()) {
            addWatermark();
        }

        graphics.dispose();

        return pageImage;
    }

    /**
     * Calculate image and page dimensions.
     */
    protected void calcDimensions() {
        int width = imageSize.width;
        int height = imageSize.height;
        int widthOffset = 0;
        int heightOffset = 0;
        if (isWatermarkEnabled() && canvasExtensionProp.isEnabled()) {
            widthOffset = canvasExtensionProp.getAddX();
            heightOffset = canvasExtensionProp.getAddY();
        }
        pageSize.width = width + widthOffset;
        pageSize.height = height + heightOffset;
        imageRect.width = width;
        imageRect.height = height;
        int maxWidth = size;
        int maxHeight = size;
        if (pageSize.width > maxWidth) {
            pageSize.width = maxWidth;
            imageRect.width = pageSize.width - widthOffset;
            imageScaling = (float)imageRect.width / (float)width;
            imageRect.height = (int)((float)height * imageScaling);
            pageSize.height = imageRect.height + heightOffset;
        }
        if (pageSize.height > maxHeight) {
            pageSize.height = maxHeight;
            imageRect.height = pageSize.height - heightOffset;
            imageScaling = (float)imageRect.height / (float)height;
            imageRect.width = (int)((float)width * imageScaling);
            pageSize.width = imageRect.width + widthOffset;
        }
    }

    /**
     * Determine if watermarking is enabled for this page.
     */
    protected boolean isWatermarkEnabled() {
        return watermarkProp.isEnabled() && size >= watermarkProp.getMinSize();
    }

    /**
     * Add a watermark to the page.
     *
     * @throws IOException on watermark file errors
     */
    protected void addWatermark() throws IOException {

        watermarkRect = new Rectangle();
        Font font = new Font(textModeProp.getFont(), Font.PLAIN, textModeProp.getSize());
        BufferedImage watermarkImage = null;

        // Get watermark size
        if ("text".equalsIgnoreCase(watermarkProp.getRenderMode())) {
            // Text watermark
            graphics.setFont(font);
            GlyphVector glyphVector = font.createGlyphVector(graphics.getFontRenderContext(), textModeProp.getContent());
            Rectangle2D rect = glyphVector.getOutline().getBounds2D();
            watermarkRect.width = (int)rect.getWidth();
            watermarkRect.height = (int)rect.getHeight();

        } else if ("image".equalsIgnoreCase(watermarkProp.getRenderMode())) {
            // Image watermark
            watermarkImage = ImageIO.read(new File(imageModeProp.getPath()));
            watermarkRect.width = watermarkImage.getWidth();
            watermarkRect.height = watermarkImage.getHeight();
        }

        // Get watermark position (default: text mode)
        if (StringUtils.containsIgnoreCase(watermarkProp.getGravity(), "west")) {
            watermarkRect.x = watermarkProp.getOffsetX();
        } else if (StringUtils.containsIgnoreCase(watermarkProp.getGravity(), "east")) {
            watermarkRect.x = pageSize.width - watermarkRect.width - watermarkProp.getOffsetX() - 1;
        } else {
            watermarkRect.x = pageSize.width / 2 - watermarkRect.width / 2 + watermarkProp.getOffsetX();
        }
        if (StringUtils.containsIgnoreCase(watermarkProp.getGravity(), "north")) {
            watermarkRect.y = watermarkProp.getOffsetY() + watermarkRect.height;
        } else if (StringUtils.containsIgnoreCase(watermarkProp.getGravity(), "south")) {
            watermarkRect.y = pageSize.height - watermarkProp.getOffsetY() - 1;
        } else {
            watermarkRect.y = pageSize.height / 2 - watermarkRect.height / 2 + watermarkProp.getOffsetY();
        }
        // ... or for image mode
        if ("image".equalsIgnoreCase(watermarkProp.getRenderMode())) {
            watermarkRect.y -= watermarkRect.height;
        }

        // Draw watermark
        if ("text".equalsIgnoreCase(watermarkProp.getRenderMode())) {
            // Text watermark
            int[] color = Arrays.stream(textModeProp.getColorRGB().split(","))
                .mapToInt(Integer::parseInt).toArray();
            graphics.setPaint(new Color(color[0], color[1], color[2]));
            graphics.setFont(font);
            graphics.drawString(textModeProp.getContent(), watermarkRect.x, watermarkRect.y);

        } else if ("image".equalsIgnoreCase(watermarkProp.getRenderMode())) {
            // Image watermark
            AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)imageModeProp.getOpacity() / 100);
            graphics.setComposite(composite);
            graphics.drawImage(watermarkImage, watermarkRect.x, watermarkRect.y, null);
        }
    }
}
