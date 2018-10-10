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
import org.kitodo.mediaserver.core.api.IWatermarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of IWatermarker.
 */
public abstract class AbstractWatermarker implements IWatermarker {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWatermarker.class);

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
            if (colorArr.length < 3) {
                return new Color(32, 32, 32); // Default by non valid configuration
            }
            return new Color(
                    Integer.parseInt(colorArr[0].trim()),
                    Integer.parseInt(colorArr[1].trim()),
                    Integer.parseInt(colorArr[2].trim())
            );
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("Invalid rgb color value : " + ex.toString(), ex);
            return null;
        }
    }
}
