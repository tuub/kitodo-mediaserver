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

package org.kitodo.mediaserver.core.api;

import java.util.Map;

/**
 * Interface of a path pattern processor.
 */
public interface IPathPatternProcessor {

    /**
     * Resolves a file path to a parameter map.
     *
     * @param path a file path
     * @return a map with the parameter
     */
    Map<String, String> resolve(String path);

    /**
     * Composes an file path from a parameter map.
     *
     * @param parameterMap a map of parameter
     * @return a file path
     */
    String compose(Map<String, String> parameterMap);

}
