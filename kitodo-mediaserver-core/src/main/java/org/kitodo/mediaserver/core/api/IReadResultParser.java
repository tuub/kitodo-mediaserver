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

import java.util.List;

/**
 * Interface for parsing read results.
 */
public interface IReadResultParser {

    /**
     * Parses a list of strings and returns an object with the result.
     *
     * @param input the list to parse
     * @return a result object
     */
    Object parse(List<String> input) throws Exception;
}
