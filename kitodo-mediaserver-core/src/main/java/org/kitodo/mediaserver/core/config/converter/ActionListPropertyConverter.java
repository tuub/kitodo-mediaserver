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

package org.kitodo.mediaserver.core.config.converter;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Convert empty property in YAML to empty Map.
 * Parses this:
 * {@code
 *   actionsBeforeIndexing:  -->  Map<String, Map<String, String>>
 * }
 */
@Component
@ConfigurationPropertiesBinding
public class ActionListPropertyConverter implements Converter<String, Map<String, Map<String, String>>> {

    /**
     * Creates an entry if requested in a properties class and none given in the yaml configuration.
     *
     * @param source the yaml key
     * @return a map with the source as key
     */
    @Override
    public Map<String, Map<String, String>> convert(String source) {

        Map<String, Map<String, String>> map = new HashMap<>();

        if (StringUtils.hasText(source)) {
            /*
             * parse action name without ending colon:
             *
             *   actionsBeforeIndexing:
             *   - testAction  -->  Map<String, String>
             */
            map.put(source, new HashMap<>());
        }

        return map;
    }
}
