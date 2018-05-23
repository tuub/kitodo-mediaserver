package org.kitodo.mediaserver.core.processors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import javax.naming.ConfigurationException;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the XsltMetsReader.
 */
@RunWith(SpringRunner.class)
public class XsltMetsReaderTest {

    private XsltMetsReader xsltMetsReader = new XsltMetsReader();
    private File testMetsFile;
    private String param1 = "request_url";
    private String param2 = "original_id";

    @Autowired
    private ResourceLoader resourceLoader;

    @Before
    public void init() throws Exception {
        File xsltFile = ResourceUtils.getFile("classpath:xslt/masterFileFromMets.xsl");
        xsltMetsReader.setXslt(xsltFile);

        testMetsFile = ResourceUtils.getFile("classpath:mets/flugblattTestMets.xml");
    }

    @Test
    public void returnListWithThreeEntriesByCorrectInput() throws Exception {

        //given
        String paramValue1 = "http://example.com/files/Flugblatt_1_717_0006_tif/Flugblatt_1_717_0006_2000_0003.jpg";
        String paramValue2 = "PRESENTATION";

        //when
        List<String> result = xsltMetsReader.read(
                testMetsFile,
                new AbstractMap.SimpleEntry<>(param1, paramValue1),
                new AbstractMap.SimpleEntry<>(param2, paramValue2)
        );

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).matches("source_url:\\s*http://example.com/files/Flugblatt_1_717_0006_tif/"
                + "Flugblatt_1_717_0006_0003.tif");
        assertThat(result.get(1)).matches("source_mime:\\s*image/tiff");
        assertThat(result.get(2)).matches("target_mime:\\s*image/jpeg");
    }

    @Test
    public void getCorrectReturnValuesForPdfFile() throws Exception {
        //given
        String paramValue1 = "http://example.com/files/Flugblatt_1_717_0006_txt/Flugblatt_1_717_0006_0001.pdf";
        String paramValue2 = "PRESENTATION";

        //when
        List<String> result = xsltMetsReader.read(
                testMetsFile,
                new AbstractMap.SimpleEntry<>(param1, paramValue1),
                new AbstractMap.SimpleEntry<>(param2, paramValue2)
        );

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(3);
        assertThat(result.get(0)).matches("source_url:\\s*http://example.com/files/Flugblatt_1_717_0006_tif/"
                + "Flugblatt_1_717_0006_0001.tif");
        assertThat(result.get(1)).matches("source_mime:\\s*image/tiff");
        assertThat(result.get(2)).matches("target_mime:\\s*application/pdf");

    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionWhenMetsFileMissing() throws Exception {
        xsltMetsReader.read(new File("fake/path"));
    }

    @Test(expected = RuntimeException.class)
    public void throwExceptionWhenMetsFileNull() throws Exception {
        xsltMetsReader.read(null);
    }

    @Test(expected = ConfigurationException.class)
    public void throwExceptionWhenXsltTransformerNull() throws Exception {
        xsltMetsReader.setXslt(null);
        xsltMetsReader.read(null);
    }

}
