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

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for [key][operator][value] items in a string.
 */
public class ConditionParser {

    private static final String patternstr = "(?:(?:([\\w\\d]+)(:|!:|=|!=|>=|<=|<|>)\\s*)?(?:\"([^\"]*)\"|([^\\s]+)))+";

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
    public ConditionParser() {
        this(new ArrayList<String>());
    }

    /**
     * Constructor with given list of allowed keys.
     * @param allowedKeys keys that are allowed in the string
     */
    public ConditionParser(List<String> allowedKeys) {
        this.allowedKeys = allowedKeys;
    }

    /**
     * Parse a string, search for values and their optional keys and operators.
     * @param subject the string that should be parsed
     * @return list of keys with operator and value
     */
    public List<Map.Entry<String, Map.Entry<Operator, String>>> parse(String subject) {

        Pattern pattern = Pattern.compile(patternstr);
        Matcher matcher = pattern.matcher(subject);
        List<Map.Entry<String, Map.Entry<Operator, String>>> terms = new ArrayList<>();

        // walk through all key-operator-value pairs
        while (matcher.find()) {

            String key = matcher.group(1);
            String operator = matcher.group(2);
            String valueQuoted = matcher.group(3);
            String value = matcher.group(4);

            if (key != null && !allowedKeys.contains(key)) {
                // this key is not allowed; ignore this value
                continue;
            }

            Operator operatorEnum;
            try {
                operatorEnum = Operator.get(operator);
            } catch (Exception ex) {
                // unknown operator; ignore this value
                continue;
            }

            if (value == null) {
                value = valueQuoted;
            }

            Map.Entry<Operator, String> opAndValue = new AbstractMap.SimpleEntry<>(operatorEnum, value);
            Map.Entry<String, Map.Entry<Operator, String>> term = new AbstractMap.SimpleEntry<>(key, opAndValue);

            terms.add(term);
        }

        return terms;
    }
}
