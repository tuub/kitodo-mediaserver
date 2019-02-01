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

package org.kitodo.mediaserver.core.processors;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConditionParserTest {

    @Test
    public void parseValuesOnly() {
        ConditionParser conditionParser = new ConditionParser();
        List<Map.Entry<String, Map.Entry<Operator, String>>> fields = conditionParser.parse(
            "abc key:not identifier:something title:\"a b:c\" \"d e:f\" xyz "
            + "title!=t title<1 title>\"12 3\" title!:eq title=w title>=f title<= l");

        assertThat(fields.size()).isEqualTo(3);
        assertThat(fields.get(0)).isEqualTo(new AbstractMap.SimpleEntry<>(
            null, new AbstractMap.SimpleEntry<>(null, "abc")));
        assertThat(fields.get(1)).isEqualTo(new AbstractMap.SimpleEntry<>(
            null, new AbstractMap.SimpleEntry<>(null, "d e:f")));
        assertThat(fields.get(2)).isEqualTo(new AbstractMap.SimpleEntry<>(
            null, new AbstractMap.SimpleEntry<>(null, "xyz")));
    }

    @Test
    public void parseWithKeys() {
        ConditionParser conditionParser = new ConditionParser(
            Arrays.asList("identifier", "title", "enabled", "z", "u", "i", "r", "q", "c", "f"));
        List<Map.Entry<String, Map.Entry<Operator, String>>> fields = conditionParser.parse(
            "abc key:not identifier:something title:\"a b:c\" \"d e:f\" xyz "
            + "z!=t u<1 i>\"12 3\" r!:eq q=w c>=f f<= l");

        assertThat(fields.size()).isEqualTo(12);
        assertThat(fields.get(0)).isEqualTo(new AbstractMap.SimpleEntry<>(
            null, new AbstractMap.SimpleEntry<>(null, "abc")));
        assertThat(fields.get(1)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "identifier", new AbstractMap.SimpleEntry<>(Operator.CONTAINS, "something")));
        assertThat(fields.get(2)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "title", new AbstractMap.SimpleEntry<>(Operator.CONTAINS, "a b:c")));
        assertThat(fields.get(3)).isEqualTo(new AbstractMap.SimpleEntry<>(
            null, new AbstractMap.SimpleEntry<>(null, "d e:f")));
        assertThat(fields.get(4)).isEqualTo(new AbstractMap.SimpleEntry<>(
            null, new AbstractMap.SimpleEntry<>(null, "xyz")));
        assertThat(fields.get(5)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "z", new AbstractMap.SimpleEntry<>(Operator.NOT_EQUAL, "t")));
        assertThat(fields.get(6)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "u", new AbstractMap.SimpleEntry<>(Operator.LESS, "1")));
        assertThat(fields.get(7)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "i", new AbstractMap.SimpleEntry<>(Operator.GREATER, "12 3")));
        assertThat(fields.get(8)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "r", new AbstractMap.SimpleEntry<>(Operator.NOT_CONTAINS, "eq")));
        assertThat(fields.get(9)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "q", new AbstractMap.SimpleEntry<>(Operator.EQUAL, "w")));
        assertThat(fields.get(10)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "c", new AbstractMap.SimpleEntry<>(Operator.GREATER_OR_EQUAL, "f")));
        assertThat(fields.get(11)).isEqualTo(new AbstractMap.SimpleEntry<>(
            "f", new AbstractMap.SimpleEntry<>(Operator.LESS_OR_EQUAL, "l")));
    }
}
