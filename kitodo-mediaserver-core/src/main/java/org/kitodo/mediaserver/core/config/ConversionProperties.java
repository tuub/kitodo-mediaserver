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
    @ConfigurationProperties(prefix = "conversion.jpeg")
    public static class Jpeg {
        private int defaultSize;

        public int getDefaultSize() {
            return defaultSize;
        }

        public void setDefaultSize(int defaultSize) {
            this.defaultSize = defaultSize;
        }
    }

    @Configuration
    @ConfigurationProperties(prefix = "conversion.watermark")
    public static class Watermark {

        private boolean enabled;
        private Integer minSize;
        private String renderMode;
        private String background;
        private String gravity;
        private Integer offsetX;
        private Integer offsetY;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Integer getMinSize() {
            return minSize;
        }

        public void setMinSize(Integer minSize) {
            this.minSize = minSize;
        }

        public String getRenderMode() {
            return renderMode;
        }

        public void setRenderMode(String renderMode) {
            this.renderMode = renderMode;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public String getGravity() {
            return gravity;
        }

        public void setGravity(String gravity) {
            this.gravity = gravity;
        }

        public Integer getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(Integer offsetX) {
            this.offsetX = offsetX;
        }

        public Integer getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(Integer offsetY) {
            this.offsetY = offsetY;
        }

        @Configuration
        @ConfigurationProperties(prefix = "conversion.watermark.textmode")
        public static class TextMode {
            private String content;
            private String font;
            private String colorRGB;
            private int size;

            public String getContent() {
                return content;
            }

            public void setContent(String content) {
                this.content = content;
            }

            public String getFont() {
                return font;
            }

            public void setFont(String font) {
                this.font = font;
            }

            public String getColorRGB() {
                return colorRGB;
            }

            public void setColorRGB(String colorRGB) {
                this.colorRGB = colorRGB;
            }

            public int getSize() {
                return size;
            }

            public void setSize(int size) {
                this.size = size;
            }
        }

        @Configuration
        @ConfigurationProperties(prefix = "conversion.watermark.imagemode")
        public static class ImageMode {
            private String path;
            private Integer opacity;

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public Integer getOpacity() {
                return opacity;
            }

            public void setOpacity(Integer opacity) {
                this.opacity = opacity;
            }
        }

        @Configuration
        @ConfigurationProperties(prefix = "conversion.watermark.extendcanvas")
        public static class CanvasExtension {
            private boolean enabled;
            private String backgroundColorRGB;
            private Integer addX;
            private Integer addY;

            public boolean isEnabled() {
                return enabled;
            }

            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }

            public String getBackgroundColorRGB() {
                return backgroundColorRGB;
            }

            public void setBackgroundColorRGB(String backgroundColorRGB) {
                this.backgroundColorRGB = backgroundColorRGB;
            }

            public Integer getAddX() {
                return addX;
            }

            public void setAddX(Integer addX) {
                this.addX = addX;
            }

            public Integer getAddY() {
                return addY;
            }

            public void setAddY(Integer addY) {
                this.addY = addY;
            }
        }
    }
}
