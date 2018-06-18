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

package org.kitodo.mediaserver.cli.converter;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;
import picocli.CommandLine.ITypeConverter;

/**
 * Type converter for CommandLine to convert human readable timespans to seconds.
 */
public class TimespanConverter implements ITypeConverter<Long> {

    private static final String patternstr = "^([1-9][0-9]*)([smhd]?)$";

    private static final LinkedHashMap<String, Integer> factors = new LinkedHashMap<String, Integer>() {
        {
            put("s",0);
            put("m",60);
            put("h",60 * 60);
            put("d",24 * 60 * 60);
        }
    };

    @Override
    public Long convert(String value) throws Exception {

        // parse string
        Pattern pattern = Pattern.compile(patternstr);
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            String number = matcher.group(1);
            String unit = matcher.group(2);
            Long seconds = Long.parseLong(number, 10);
            if (StringUtils.hasText(unit)) {
                seconds *= factors.get(unit);
            }
            return seconds;
        }

        throw new IllegalArgumentException("Value needs to be in format <number>[unit]. Unit is s, m, h or d.");
    }
}
