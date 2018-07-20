package org.kitodo.mediaserver.core.processors;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import org.im4java.core.IMOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.config.ConversionProperties;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.ResourceUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Watermarker.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class WatermarkerTest {

    private Watermarker watermarker = new Watermarker();

    private IMOperation operation;
    private File masterFile;
    private File watermarkFile;
    private Integer size;
    private ConversionProperties.Watermark conversionPropertiesWatermark;
    private ConversionProperties.Watermark.ImageMode conversionPropertiesWatermarkImageMode;
    private ConversionProperties.Watermark.TextMode conversionPropertiesWatermarkTextMode;
    private ConversionProperties.Watermark.CanvasExtension conversionPropertiesWatermarkExtendCanvas;

    @Before
    public void init() throws FileNotFoundException {

        ConversionProperties.Jpeg conversionPropertiesJpeg;

        conversionPropertiesWatermark = mock(ConversionProperties.Watermark.class);
        conversionPropertiesWatermarkImageMode = mock(ConversionProperties.Watermark.ImageMode.class);
        conversionPropertiesWatermarkTextMode = mock(ConversionProperties.Watermark.TextMode.class);
        conversionPropertiesWatermarkExtendCanvas = mock(ConversionProperties.Watermark.CanvasExtension.class);
        conversionPropertiesJpeg = mock(ConversionProperties.Jpeg.class);

        watermarker.setConversionPropertiesJpeg(conversionPropertiesJpeg);
        watermarker.setConversionPropertiesWatermark(conversionPropertiesWatermark);
        watermarker.setConversionPropertiesWatermarkImageMode(conversionPropertiesWatermarkImageMode);
        watermarker.setConversionPropertiesWatermarkTextMode(conversionPropertiesWatermarkTextMode);
        watermarker.setConversionPropertiesCanvasExtension(conversionPropertiesWatermarkExtendCanvas);

        masterFile = ResourceUtils.getFile("classpath:watermark/master.tif");
        watermarkFile = ResourceUtils.getFile("classpath:watermark/watermark.png");

        operation = new IMOperation();
        size = 1000;

        when(conversionPropertiesJpeg.getDefaultSize()).thenReturn(1000);
        when(conversionPropertiesWatermark.getMinSize()).thenReturn(200);
        when(conversionPropertiesWatermark.getGravity()).thenReturn("southeast");
        when(conversionPropertiesWatermark.getOffsetX()).thenReturn(50);
        when(conversionPropertiesWatermark.getOffsetY()).thenReturn(10);
    }

    @Test
    public void canGenerateTextWatermark() throws Exception {
        // given
        when(conversionPropertiesWatermark.getRenderMode()).thenReturn("text");
        when(conversionPropertiesWatermarkTextMode.getContent()).thenReturn("Testing out the watermarker");
        when(conversionPropertiesWatermarkTextMode.getColorRGB()).thenReturn("55,66,77");
        when(conversionPropertiesWatermarkTextMode.getFont()).thenReturn("Arial");
        when(conversionPropertiesWatermarkTextMode.getSize()).thenReturn(16);

        // when
        watermarker.perform(operation, masterFile, size);

        // then
        String expected = "-colorspace RGB -quality 100.0 -font Arial -gravity southeast -pointsize 16"
            + " -fill rgb(55,66,77) -draw text 50,10 'Testing out the watermarker' ";

        assertThat(operation.toString()).isEqualTo(expected);
    }

    @Test
    public void canGenerateImageWatermark() throws Exception {
        // given
        when(conversionPropertiesWatermark.getRenderMode()).thenReturn("image");
        when(conversionPropertiesWatermarkImageMode.getPath()).thenReturn(watermarkFile.getAbsolutePath());
        when(conversionPropertiesWatermarkImageMode.getOpacity()).thenReturn(75);

        // when
        watermarker.perform(operation, masterFile, size);

        // then
        String expected = "-colorspace RGB -quality 100.0 "
            + watermarkFile.getAbsolutePath()
            + " -gravity southeast -geometry 298x136+50+10 -composite ";

        assertThat(operation.toString()).isEqualTo(expected);
    }

    @Test
    public void canExtendCanvas() throws Exception {
        // given
        when(conversionPropertiesWatermark.isEnabled()).thenReturn(true);
        when(conversionPropertiesWatermark.getRenderMode()).thenReturn("text");
        when(conversionPropertiesWatermarkTextMode.getContent()).thenReturn("Testing out the canvas extension");
        when(conversionPropertiesWatermarkTextMode.getColorRGB()).thenReturn("55,66,77");
        when(conversionPropertiesWatermarkTextMode.getFont()).thenReturn("Arial");
        when(conversionPropertiesWatermarkTextMode.getSize()).thenReturn(16);
        when(conversionPropertiesWatermarkExtendCanvas.isEnabled()).thenReturn(true);
        when(conversionPropertiesWatermarkExtendCanvas.getBackgroundColorRGB()).thenReturn("11,22,33");
        when(conversionPropertiesWatermarkExtendCanvas.getAddX()).thenReturn(0);
        when(conversionPropertiesWatermarkExtendCanvas.getAddY()).thenReturn(50);

        // when
        watermarker.perform(operation, masterFile, size);

        // then
        String expected = "-colorspace RGB -quality 100.0 -background rgb(11,22,33) -extent 1000x1409"
            + " -font Arial -gravity southeast -pointsize 16 -fill rgb(55,66,77) -draw text 50,10"
            + " 'Testing out the canvas extension' ";

        assertThat(operation.toString()).isEqualTo(expected);
    }

    @Test
    public void canCalculateScaledValue() throws NumberFormatException {
        // given
        Integer value = 1000;
        Float scale = 75.0f;

        // when
        Integer result = watermarker.getScaledValue(value, scale);

        // then
        assertThat(result).isEqualTo(750);
    }

    @Test
    public void canParseRGBfromConfiguration() throws IllegalArgumentException {
        // given
        String rgbColorStr = "33,44,55";

        // when
        Color color = watermarker.getRGBColor(rgbColorStr);

        // then
        assertThat(color).isInstanceOf(Color.class);
        assertThat(color.getRed()).isEqualTo(33);
        assertThat(color.getGreen()).isEqualTo(44);
        assertThat(color.getBlue()).isEqualTo(55);
    }

}
