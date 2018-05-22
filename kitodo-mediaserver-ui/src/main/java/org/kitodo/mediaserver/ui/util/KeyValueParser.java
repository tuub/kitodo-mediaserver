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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for key:value items in a string.
 */
public class KeyValueParser {

    private static final String patternstr = "(?:(?:([\\w\\d]+):)?(?:\"([^\"]*)\"|([^\\s]+)))+";

    private List<String> allowedKeys;

    public List<String> getAllowedKeys() {
        return allowedKeys;
    }

    public void setAllowedKeys(ArrayList<String> allowedKeys) {
        this.allowedKeys = allowedKeys;
    }

    /**
     * Default constructor with no allowed keys - keys are disabled.
     */
    public KeyValueParser() {
        this(new ArrayList<String>());
    }

    /**
     * Constructor with given list of allowed keys.
     * @param allowedKeys keys that are allowed in the string
     */
    public KeyValueParser(List<String> allowedKeys) {
        this.allowedKeys = allowedKeys;
    }

    /**
     * Parse a string, search for values and their optional keys.
     * @param subject the string that should be parsed
     * @return list of key:value
     */
    public List<Map.Entry<String, String>> parse(String subject) {

        Pattern pattern = Pattern.compile(patternstr);
        Matcher matcher = pattern.matcher(subject);
        List<Map.Entry<String, String>> terms = new ArrayList<>();

        while (matcher.find()) {

            String key = matcher.group(1);
            String valueQuoted = matcher.group(2);
            String value = matcher.group(3);

            if (key != null && !allowedKeys.contains(key)) {
                continue;
            }

            Map.Entry<String, String> term = new AbstractMap.SimpleEntry<>(key, value);
            if (value == null) {
                term.setValue(valueQuoted);
            }

            terms.add(term);
        }

        return terms;
    }

}
