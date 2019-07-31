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

package org.kitodo.mediaserver.core.processors.ocr;

import java.nio.file.Path;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

@RunWith(SpringRunner.class)
public class XsltOcrReaderTest {

    private XsltOcrReader xsltOcrReader = new XsltOcrReader();
    private Path testOcrFile_v2;
    private Path testOcrFile_v3;

    @Before
    public void init() throws Exception {
        xsltOcrReader = new XsltOcrReader();
        testOcrFile_v2 = ResourceUtils.getFile("classpath:fulltext/flugblattTestAlto.xml").toPath();
        testOcrFile_v3 = ResourceUtils.getFile("classpath:fulltext/alto_v3.xml").toPath();
    }

    @Test
    public void parseFirstWord_v2() throws Exception {
        //given
        xsltOcrReader.getFormats().put("<alto ", new ClassPathResource("xslt/ocr/alto.xsl"));

        //when
        OcrPage result = xsltOcrReader.read(testOcrFile_v2);

        //then
        assertThat(result).isNotNull();
        assertThat(result.paragraphs).isNotNull();
        assertThat(result.paragraphs).hasSize(28);
        assertThat(result.paragraphs.get(0)).isNotNull();
        assertThat(result.paragraphs.get(0).lines).isNotNull();
        assertThat(result.paragraphs.get(0).lines).hasSize(1);
        assertThat(result.paragraphs.get(0).lines.get(0)).isNotNull();
        assertThat(result.paragraphs.get(0).lines.get(0).words).isNotNull();
        assertThat(result.paragraphs.get(0).lines.get(0).words).hasSize(4);
        assertThat(result.paragraphs.get(0).lines.get(0).words.get(0)).isNotNull();
        assertThat(result.paragraphs.get(0).lines.get(0).words.get(0).word).isEqualTo("J");
        assertThat(result.paragraphs.get(0).lines.get(0).words.get(0).height).isEqualTo(56);
        assertThat(result.paragraphs.get(0).lines.get(0).words.get(0).width).isEqualTo(23);
        assertThat(result.paragraphs.get(0).lines.get(0).words.get(0).x).isEqualTo(2174);
        assertThat(result.paragraphs.get(0).lines.get(0).words.get(0).y).isEqualTo(194);
    }

    @Test
    public void parseFirstWord_v3() throws Exception {
        //given
        xsltOcrReader.getFormats().put("<alto ", new ClassPathResource("xslt/ocr/alto.xsl"));

        //when
        OcrPage result = xsltOcrReader.read(testOcrFile_v3);

        //then
        assertThat(result).isNotNull();
        assertThat(result.paragraphs).isNotNull();
        assertThat(result.paragraphs).hasSize(22);
        assertThat(result.paragraphs.get(10)).isNotNull();
        assertThat(result.paragraphs.get(10).lines).isNotNull();
        assertThat(result.paragraphs.get(10).lines).hasSize(8);
        assertThat(result.paragraphs.get(10).lines.get(0)).isNotNull();
        assertThat(result.paragraphs.get(10).lines.get(0).words).isNotNull();
        assertThat(result.paragraphs.get(10).lines.get(0).words).hasSize(11);
        assertThat(result.paragraphs.get(10).lines.get(0).words.get(0)).isNotNull();
        assertThat(result.paragraphs.get(10).lines.get(0).words.get(0).word).isEqualTo("form");
        assertThat(result.paragraphs.get(10).lines.get(0).words.get(0).height).isEqualTo(31);
        assertThat(result.paragraphs.get(10).lines.get(0).words.get(0).width).isEqualTo(89);
        assertThat(result.paragraphs.get(10).lines.get(0).words.get(0).x).isEqualTo(906);
        assertThat(result.paragraphs.get(10).lines.get(0).words.get(0).y).isEqualTo(2056);
    }

    @Test(expected = Exception.class)
    public void parseWithWrongIdentifier() throws Exception {
        //given
        xsltOcrReader.getFormats().put("<document ", new ClassPathResource("xslt/ocr/alto.xsl"));

        //when
        OcrPage result = xsltOcrReader.read(testOcrFile_v2);

        //then
    }
}
