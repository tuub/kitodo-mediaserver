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
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.ConfigurationException;
import org.kitodo.mediaserver.core.api.IExtractor;
import org.springframework.util.CollectionUtils;


/**
 * Extracts a value from a string using regex regexList.
 */
public class PatternExtractor implements IExtractor {

    List<String> regexList;

    public void setRegexList(List<String> regexList) {
        this.regexList = regexList;
    }

    /**
     * Extracts a value from a string using a list of regexList.
     * Returns a value as soon as a pattern matches.
     * If no pattern matches, null is returned.
     *
     * @param input the input string
     * @return a string with the extracted value.
     */
    @Override
    public String extract(String input) throws ConfigurationException {

        if (CollectionUtils.isEmpty(regexList)) {
            throw new ConfigurationException("There is no list of regular expressions for the pattern extractors, "
                    + "please check your spring configuration.");
        }

        Optional<String> matchingRegex = regexList.stream()
                .filter(input::matches)
                .findFirst();

        if (matchingRegex.isPresent()) {
            Pattern pattern = Pattern.compile(matchingRegex.get());
            Matcher matcher = pattern.matcher(input);
            matcher.find();
            return matcher.group(1);
        }

        return null;
    }
}
