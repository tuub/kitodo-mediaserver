package org.kitodo.mediaserver.core.processors;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.kitodo.mediaserver.core.api.IMetsTransformer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

/**
 *
 */
public class CleanMetsTitleEntriesTest {

    private IMetsTransformer metsTransformer;
    private File testMetsFile;
    private File testMetsResult;

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Before
    public void init() throws Exception {
        metsTransformer = new XsltMetsTransformer(new ClassPathResource("xslt/cleanMetsTitleEntries.xsl"));
        testMetsResult = tempFolder.newFile();
    }

    @Test
    public void testClean() throws Exception {

        // given
        testMetsFile = ResourceUtils.getFile("classpath:mets/BV024625242.xml");

        // Build a string from the file and make sure, the pattern to replace is in there
        String testMetsString = file2String(testMetsFile);
        assertThat(testMetsString).contains("<mods:title>&lt;&lt;Die&gt;&gt; Aufgabe");
        assertThat(testMetsString).doesNotContain("<mods:title>Die Aufgabe");
        assertThat(testMetsString).contains("LABEL=\"&lt;&lt;Die>> Aufgaben ");

        // when
        metsTransformer.transform(testMetsFile, testMetsResult, new AbstractMap.SimpleEntry<>("pattern", "<<(.*?)>>"));

        //then
        String resultMetsString = file2String(testMetsResult);
        assertThat(resultMetsString).doesNotContain("<mods:title>&lt;&lt;Die&gt;&gt; Aufgabe");
        assertThat(resultMetsString).contains("<mods:title>Die Aufgabe");
        assertThat(resultMetsString).doesNotContain("LABEL=\"&lt;&lt;Die>> Aufgaben ");
        assertThat(resultMetsString).contains("LABEL=\"Die Aufgaben ");

    }

    @Test
    public void testCleanDoesNotChangeAlreadyCleanTitles() throws Exception {

        // given
        testMetsFile = ResourceUtils.getFile("classpath:mets/flugblattTestMets.xml");
        String testMetsString = file2String(testMetsFile);
        assertThat(testMetsString).contains("<mods:title>Aufruf. In Besorgnis um den wachsenden Bildungsnotstand");

        //when
        metsTransformer.transform(testMetsFile, testMetsResult, new AbstractMap.SimpleEntry<>("pattern", "<<(.*?)>>"));
        //then
        String resultMetsString = file2String(testMetsResult);
        assertThat(resultMetsString).contains("<mods:title>Aufruf. In Besorgnis um den wachsenden Bildungsnotstand");

    }


    private String file2String(File file) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        Stream<String> stream = Files.lines(file.toPath());
        stream.forEach(s -> stringBuilder.append(s));

        return stringBuilder.toString();
    }
}
