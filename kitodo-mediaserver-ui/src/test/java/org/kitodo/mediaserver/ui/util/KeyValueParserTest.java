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

package org.kitodo.mediaserver.ui.util;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KeyValueParserTest {

    @Test
    public void parseValuesOnly() {
        KeyValueParser keyValueParser = new KeyValueParser();
        List<Map.Entry<String, String>> fields = keyValueParser.parse("abc key:not identifier:something title:\"a b:c\" \"d e:f\" xyz");

        assertThat(fields.size()).isEqualTo(3);
        assertThat(fields.get(0)).isEqualTo(new AbstractMap.SimpleEntry<String, String>(null, "abc"));
        assertThat(fields.get(1)).isEqualTo(new AbstractMap.SimpleEntry<String, String>(null, "d e:f"));
        assertThat(fields.get(2)).isEqualTo(new AbstractMap.SimpleEntry<String, String>(null, "xyz"));
    }

    @Test
    public void parseWithKeys() {
        KeyValueParser keyValueParser = new KeyValueParser(Arrays.asList("identifier", "title", "enabled"));
        List<Map.Entry<String, String>> fields = keyValueParser.parse("abc key:not identifier:something title:\"a b:c\" \"d e:f\" xyz");

        assertThat(fields.size()).isEqualTo(5);
        assertThat(fields.get(0)).isEqualTo(new AbstractMap.SimpleEntry<String, String>(null, "abc"));
        assertThat(fields.get(1)).isEqualTo(new AbstractMap.SimpleEntry<String, String>("identifier", "something"));
        assertThat(fields.get(2)).isEqualTo(new AbstractMap.SimpleEntry<String, String>("title", "a b:c"));
        assertThat(fields.get(3)).isEqualTo(new AbstractMap.SimpleEntry<String, String>(null, "d e:f"));
        assertThat(fields.get(4)).isEqualTo(new AbstractMap.SimpleEntry<String, String>(null, "xyz"));
    }
}
