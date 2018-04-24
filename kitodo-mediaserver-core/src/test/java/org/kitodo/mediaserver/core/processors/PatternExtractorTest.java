package org.kitodo.mediaserver.core.processors;

import javax.naming.ConfigurationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the pattern extractor.
 */
@RunWith(SpringRunner.class)
public class PatternExtractorTest {

    private PatternExtractor patternExtractor;

    private List<String> regexList = Arrays.asList(
            ".*/(max|min|thumb|default)/.*",
            ".*/(150|600|1000|2000)/.*",
            "https?://.*?/.*?/.*?/(\\d{3,4})/.*"
    );

    @Before
    public void init() {
        patternExtractor = new PatternExtractor();
        patternExtractor.setRegexList(regexList);
    }

    @Test
    public void shouldMatchThirdRegexAndReturn621() throws Exception {
        // given
        String input = "http://ubsrvgoobi2.ub.tu-berlin.de/viewer/content/990005943940302884/621/0/"
                + "molopomo_990005943940302884_0001.jpg";

        // when
        String value = patternExtractor.extract(input);

        //then
        assertThat(value).isEqualTo("621");
    }

    @Test
    public void shouldMatchFirstRegexAndReturnMax() throws Exception {
        // given
        String input = "https://digimedia.ub.tu-berlin.de/files/1234/jpeg/max/"
                + "molopomo_990005943940302884_0001.jpg";

        // when
        String value = patternExtractor.extract(input);

        //then
        assertThat(value).isEqualTo("max");
    }

    @Test
    public void shouldMatchSecodRegexAndReturn600() throws Exception {
        // given
        String input = "https://digimedia.ub.tu-berlin.de/files/1234/jpeg/600/"
                + "molopomo_990005943940302884_0001.jpg";

        // when
        String value = patternExtractor.extract(input);

        //then
        assertThat(value).isEqualTo("600");
    }

    @Test
    public void shouldNotMatchAnyRegexAndReturnNull() throws Exception {
        // given
        String input = "https://digimedia.ub.tu-berlin.de/files/1234/jpeg/maxsize/"
                + "molopomo_990005943940302884_0001.jpg";

        // when
        String value = patternExtractor.extract(input);

        //then
        assertThat(value).isNull();
    }

    @Test(expected = ConfigurationException.class)
    public void shouldThrowConfigurationExceptionDueToNoRegexList() throws Exception {
        PatternExtractor patternExtractor1 = new PatternExtractor();

        patternExtractor1.extract("something");
    }

    @Test(expected = ConfigurationException.class)
    public void shouldThrowConfigurationExceptionDueToNoEmpzyList() throws Exception {
        PatternExtractor patternExtractor1 = new PatternExtractor();
        patternExtractor1.setRegexList(new ArrayList<>());

        patternExtractor1.extract("something");
    }

}
