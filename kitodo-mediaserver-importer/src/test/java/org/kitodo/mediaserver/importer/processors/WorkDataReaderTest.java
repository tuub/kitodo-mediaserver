package org.kitodo.mediaserver.importer.processors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;

import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Unit tests for the work data reader.
 */
@RunWith(SpringRunner.class)
public class WorkDataReaderTest {

    private XsltMetsReader xsltMetsReader = new XsltMetsReader();
    private WorkDataReader workDataReader = new WorkDataReader();
    private SimpleList2MapParser readResultParser = new SimpleList2MapParser();

    @Before
    public void init() throws Exception {
        xsltMetsReader.setXslt(new ClassPathResource("xslt/getWorkData.xsl"));
        workDataReader.setMetsReader(xsltMetsReader);
        readResultParser.setMapSeparator(":");
        workDataReader.setReadResultParser(readResultParser);
    }

    @Test(expected = FileNotFoundException.class)
    public void exceptionThrownWhenFileNotPresent() throws Exception {
        File testMetsFile  = ResourceUtils.getFile("classpath:metsfiles/notHere.xml");

        workDataReader.read(testMetsFile);
    }

    @Test
    public void generatesWorkObject() throws Exception {
        // given
        File testMetsFile  = ResourceUtils.getFile("classpath:metsfiles/singleIdentifierMets.xml");

        // when
        Work workData = workDataReader.read(testMetsFile);

        // then
        assertThat(workData).isInstanceOf(Work.class);
        assertThat(workData.getId()).isNotNull();
    }

    @Test
    public void hasCorrectSingleTitle() throws Exception {
        // given
        File testMetsFile  = ResourceUtils.getFile("classpath:metsfiles/multipleIdentifiersMets.xml");

        // when
        Work workData = workDataReader.read(testMetsFile);
        String expected = "Colloquia Lutheri conscripta a quibusdam et alia quaedam addita sunt. Thesaurus theologiae - Mscr.Dresd.A.180.d";

        assertThat(workData.getTitle()).isEqualTo(expected);
    }

    @Test
    public void hasCorrectMultipleTitle() throws Exception {
        // given
        File testMetsFile  = ResourceUtils.getFile("classpath:metsfiles/noIdentifierMets.xml");

        // when
        Work workData = workDataReader.read(testMetsFile);
        String expected1 = "Jg. 1.1886";
        String expected2 = "illustrierte Wochenschrift für Gärtner, Gartenliebhaber und Landwirte";

        // then
        assertThat(workData.getTitle()).contains(expected1, expected2);
    }
}
