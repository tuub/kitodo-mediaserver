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

package org.kitodo.mediaserver.core.conversion.ocr;

import java.io.File;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.processors.ocr.OcrPage;
import org.kitodo.mediaserver.core.processors.ocr.XsltOcrReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

@RunWith(SpringRunner.class)
public class AbbyyToAltoOcrConverterTest {

    @Test
    public void convertAndValidateAlto() throws Exception {
        // given
        File abbyyFile = ResourceUtils.getFile("classpath:fulltext/diaudeb_bv024625242_0001.xml");
        File altoFile = File.createTempFile("alto_test_", ".xml");

        XsltOcrReader reader = new XsltOcrReader();
        reader.getFormats().put("<alto ", new ClassPathResource("xslt/ocr/alto.xsl"));

        AbbyyToAltoOcrConverter converter = new AbbyyToAltoOcrConverter();

        // when
        converter.convert(abbyyFile.toPath(), altoFile.toPath());

        // then
        OcrPage page = reader.read(altoFile.toPath());
        assertThat(page).isNotNull();
        assertThat(page.paragraphs).hasSize(2);
        assertThat(page.paragraphs.get(0)).isNotNull();
        assertThat(page.paragraphs.get(0).lines).hasSize(5);
        assertThat(page.paragraphs.get(0).lines.get(0)).isNotNull();
        assertThat(page.paragraphs.get(0).lines.get(0).words).hasSize(4);
        assertThat(page.paragraphs.get(0).lines.get(0).words.get(0)).isNotNull();
        assertThat(page.paragraphs.get(0).lines.get(0).words.get(0).word).isEqualTo("Die");
        assertThat(page.paragraphs.get(0).lines.get(0).words.get(0).x).isEqualTo(346.0f);
        assertThat(page.paragraphs.get(0).lines.get(0).words.get(0).y).isEqualTo(465.0f);
        assertThat(page.paragraphs.get(0).lines.get(0).words.get(0).height).isEqualTo(56.0f);
        assertThat(page.paragraphs.get(0).lines.get(0).words.get(0).width).isEqualTo(137.0f);

        altoFile.delete();
    }

}
