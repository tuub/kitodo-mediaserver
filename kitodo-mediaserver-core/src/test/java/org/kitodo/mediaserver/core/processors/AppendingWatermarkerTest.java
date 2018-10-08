package org.kitodo.mediaserver.core.processors;

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
public class AppendingWatermarkerTest {

    private AppendingWatermarker watermarker = new AppendingWatermarker();

    private IMOperation operation;
    private File watermarkFile;
    private ConversionProperties.Watermark conversionPropertiesWatermark;
    private ConversionProperties.Watermark.ImageMode conversionPropertiesWatermarkImageMode;
    private ConversionProperties.Watermark.CanvasExtension conversionPropertiesWatermarkExtendCanvas;

    @Before
    public void init() throws FileNotFoundException {

        conversionPropertiesWatermark = mock(ConversionProperties.Watermark.class);
        conversionPropertiesWatermarkImageMode = mock(ConversionProperties.Watermark.ImageMode.class);
        conversionPropertiesWatermarkExtendCanvas = mock(ConversionProperties.Watermark.CanvasExtension.class);

        watermarker.setConversionPropertiesWatermark(conversionPropertiesWatermark);
        watermarker.setConversionPropertiesWatermarkImageMode(conversionPropertiesWatermarkImageMode);
        watermarker.setConversionPropertiesCanvasExtension(conversionPropertiesWatermarkExtendCanvas);

        watermarkFile = ResourceUtils.getFile("classpath:watermark/watermark.png");

        operation = new IMOperation();
    }

    @Test
    public void doesNothingWhenNoWatermarkFile() throws Exception {
        // given
        when(conversionPropertiesWatermarkImageMode.getPath()).thenReturn(null);

        // when
        watermarker.perform(operation, null, null);

        // then
        assertThat(operation.toString()).isEqualTo("");
    }

    @Test
    public void doesNothingWhenWatermarkFileNotReadable() throws Exception {
        // given
        when(conversionPropertiesWatermarkImageMode.getPath()).thenReturn("/some/idiotic/non/existing/path");

        // when
        watermarker.perform(operation, null, null);

        // then
        assertThat(operation.toString()).isEqualTo("");
    }

    @Test
    public void operationContainsCertainSettings() throws Exception {
        // given
        when(conversionPropertiesWatermarkImageMode.getPath()).thenReturn(watermarkFile.getAbsolutePath());
        when(conversionPropertiesWatermarkExtendCanvas.getBackgroundColorRGB()).thenReturn("55,66,77");

        // when
        watermarker.perform(operation, null, null);

        // then
        assertThat(operation.toString()).contains("-colorspace RGB"); // Needed for firefox
        assertThat(operation.toString()).contains("-background rgb(55,66,77)");
        assertThat(operation.toString()).contains("-flatten");
        assertThat(operation.toString()).contains("-append");
        assertThat(operation.toString()).contains("-opaque none");
    }

    @Test
    public void backgroundColorSetToDefaultWhenInvalid() throws Exception {
        // given
        when(conversionPropertiesWatermarkImageMode.getPath()).thenReturn(watermarkFile.getAbsolutePath());
        when(conversionPropertiesWatermarkExtendCanvas.getBackgroundColorRGB()).thenReturn("#34A7F9");

        // when
        watermarker.perform(operation, null, null);

        // then
        assertThat(operation.toString()).contains("-background rgb(32,32,32)");
    }

}
