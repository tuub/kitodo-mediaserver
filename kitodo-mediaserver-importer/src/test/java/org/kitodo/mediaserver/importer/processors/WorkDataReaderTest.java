package org.kitodo.mediaserver.importer.processors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.kitodo.mediaserver.core.db.entities.Identifier;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.processors.SimpleList2MapParser;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;

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
        File xsltFile  = ResourceUtils.getFile("classpath:xslt/getWorkData.xsl");
        xsltMetsReader.setXslt(xsltFile);
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
    public void includesMultipleIdentifiers() throws Exception {
        // given
        File testMetsFile  = ResourceUtils.getFile("classpath:metsfiles/multipleIdentifiersMets.xml");

        // when
        Work workData = workDataReader.read(testMetsFile);

        // then
        assertThat(workData).hasFieldOrProperty("identifiers");
        assertThat(workData.getIdentifiers().size() > 1).isTrue();
        assertThat(workData.getIdentifiers()).containsExactlyInAnyOrder(
                new Identifier("156804", "goobi"),
                new Identifier("urn:nbn:de:bsz:14-db-id4570526782", "urn"),
                new Identifier("http://digital.slub-dresden.de/id457052678", "purl"),
                new Identifier("457052678", "swb-ppn")
        );
    }

    @Test
    public void includesNoIdentifier() throws Exception {
        // given
        File testMetsFile  = ResourceUtils.getFile("classpath:metsfiles/noIdentifierMets.xml");

        // when
        Work workData = workDataReader.read(testMetsFile);

        // then
        assertThat(workData).hasFieldOrProperty("identifiers");
        assertThat(workData.getIdentifiers().size()).isEqualTo(0);
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
