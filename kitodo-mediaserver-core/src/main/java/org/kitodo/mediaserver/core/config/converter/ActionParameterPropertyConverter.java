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

/**
 * Convert empty property in YAML to empty Map.
 * Parses this:
 * {@code
 *  actionsBeforeIndexing:
 *  - testAction:  -->  Map<String, String>
 * }
 */
@Component
@ConfigurationPropertiesBinding
public class ActionParameterPropertyConverter implements Converter<String, Map<String, String>> {

    /**
     * Creates an empty map if requested in the properties class and none given in the yaml configuration.
     *
     * @param source ignored
     * @return an empty map
     */
    @Override
    public Map<String, String> convert(String source) {
        return new HashMap<>();
    }
}
