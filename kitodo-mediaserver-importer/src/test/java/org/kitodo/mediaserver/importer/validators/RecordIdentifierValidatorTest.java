package org.kitodo.mediaserver.importer.validators;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kitodo.mediaserver.core.exceptions.ValidationException;
import org.kitodo.mediaserver.core.processors.XsltMetsReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.ResourceUtils;

/**
 * Unit tests for the record identifier validator.
 */
@RunWith(SpringRunner.class)
public class RecordIdentifierValidatorTest {

    private XsltMetsReader xsltMetsReader = new XsltMetsReader();
    private RecordIdentifierValidation recordIdentifierValidation = new RecordIdentifierValidation();
    private File testMetsFile;

    @Autowired
    private ResourceLoader resourceLoader;

    @Before
    public void init() throws Exception {
        File xsltFile  = ResourceUtils.getFile("classpath:xslt/getRecordIdentifier.xsl");
        xsltMetsReader.setXslt(xsltFile);
        recordIdentifierValidation.setRecordIdentifierReader(xsltMetsReader);
    }

    @Test(expected = ValidationException.class)
    public void exceptionThrownWhenNoIdentifier() throws Exception {
        //given
        testMetsFile = resourceLoader.getResource("metsfiles/noRecordIdentifier.xml").getFile();

        //when
        recordIdentifierValidation.validate(testMetsFile, null);
    }

    @Test(expected = ValidationException.class)
    public void exceptionThrownWhenMultipleIdentifier() throws Exception {
        //given
        testMetsFile = resourceLoader.getResource("metsfiles/multipleRecordIdentifiers.xml").getFile();

        //when
        recordIdentifierValidation.validate(testMetsFile, null);
    }

    @Test
    public void distinctResultWhenMultipleEqualIdentifier() throws Exception {
        //given
        testMetsFile = resourceLoader.getResource("metsfiles/multipleEqualRecordIdentifiers.xml").getFile();

        //when
        String result = recordIdentifierValidation.validate(testMetsFile, null);

        //then
        assertThat(result).isEqualTo("UATUB_717-0006");
    }

    @Test
    public void distinctResultWhenOneIdentifier() throws Exception {
        //given
        testMetsFile = resourceLoader.getResource("metsfiles/oneRecordIdentifier.xml").getFile();

        //when
        String result = recordIdentifierValidation.validate(testMetsFile, null);

        //then
        assertThat(result).isEqualTo("UATUB_717-0008");
    }

}
