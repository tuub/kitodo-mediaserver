package org.kitodo.mediaserver.core.processors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SimpleList2MapParser.
 */
@RunWith(SpringRunner.class)
public class SimpleList2MapParserTest {

    SimpleList2MapParser parser = new SimpleList2MapParser();

    @Before
    public void init() {
        parser.setMapSeparator("=");
        parser.setValueConcatSeparator(";");
    }

    @Test
    public void listIsParsedToMapUsingMapSeparator() {
        //given
        List<String> input = Arrays.asList("key1=value1", "key2=value2");

        //when
        Map<String, String> result = parser.parse(input);

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get("key1")).isNotNull();
        assertThat(result.get("key1")).isEqualTo("value1");
        assertThat(result.get("key2")).isNotNull();
        assertThat(result.get("key2")).isEqualTo("value2");
    }

    @Test
    public void valueIsEmptyWhenNoSeparatorOrSeparatorAtEnd() {
        //given
        List<String> input = Arrays.asList("key1=", "key2");

        //when
        Map<String, String> result = parser.parse(input);

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get("key1")).isNotNull();
        assertThat(result.get("key1")).isEqualTo("");
        assertThat(result.get("key2")).isNotNull();
        assertThat(result.get("key2")).isEqualTo("");
    }

    @Test
    public void mapShouldConcatenateValuesByDuplicateKeys() {

        //given
        List<String> input = Arrays.asList("key=value", "key=value2", "key=value3");

        //when
        Map<String, String> result = parser.parse(input);

        //then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get("key")).isNotNull();
        assertThat(result.get("key")).isEqualTo("value;value2;value3");
    }

    @Test(expected = RuntimeException.class)
    public void runtimeExceptionByNullInput() {
        //when
        parser.parse(null);
    }

}
