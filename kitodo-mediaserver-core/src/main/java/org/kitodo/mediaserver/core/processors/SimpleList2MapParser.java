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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.kitodo.mediaserver.core.api.IReadResultParser;


/**
 * A simple parser turning a list to a string-string-map.
 */
public class SimpleList2MapParser implements IReadResultParser {

    private String mapSeparator = "=";
    private String valueConcatSeparator = " ; ";

    public void setMapSeparator(String mapSeparator) {
        this.mapSeparator = mapSeparator;
    }

    public void setValueConcatSeparator(String valueConcatSeparator) {
        this.valueConcatSeparator = valueConcatSeparator;
    }

    /**
     * Parses a list of strings and returns a map with the result.
     * The map concatenates values of duplicate keys.
     *
     * @param input the list to parse
     * @return a string-string-map
     */
    @Override
    public Map<String, String> parse(List<String> input) {

        if (input == null) {
            throw new IllegalArgumentException("The input list must not be null");
        }

        return input.stream()
                .collect(Collectors.toMap(
                    key -> StringUtils.trim(StringUtils.substringBefore(key, mapSeparator)),
                    value -> StringUtils.trim(StringUtils.substringAfter(value, mapSeparator)),
                    (oldValue, newValue) -> (oldValue + valueConcatSeparator + newValue))
                );
    }
}
