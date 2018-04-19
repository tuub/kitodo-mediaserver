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

package org.kitodo.mediaserver.core.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Properties class for the conversion configuration.
 */
@Configuration
@ConfigurationProperties(prefix = "conversion")
public class ConversionProperties {

    private List<String> pathExtractionPatterns;
    private boolean useGraphicsMagick;

    public void setPathExtractionPatterns(List<String> pathExtractionPatterns) {
        this.pathExtractionPatterns = pathExtractionPatterns;
    }

    public List<String> getPathExtractionPatterns() {
        return pathExtractionPatterns;
    }

    public boolean isUseGraphicsMagick() {
        return useGraphicsMagick;
    }

    public void setUseGraphicsMagick(boolean useGraphicsMagick) {
        this.useGraphicsMagick = useGraphicsMagick;
    }

    @Configuration
    public static class Jpeg {
        private int defaultSize;

        public int getDefaultSize() {
            return defaultSize;
        }

        public void setDefaultSize(int defaultSize) {
            this.defaultSize = defaultSize;
        }
    }
}
