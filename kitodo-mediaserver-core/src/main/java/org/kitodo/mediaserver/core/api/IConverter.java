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

import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

/**
 * Converter interface.
 */
public interface IConverter {

    /**
     * Converts a file from a given uri. Returns an output stream with the result.
     *
     * @param master the uri of the master file
     * @param parameter a map of parameter
     * @return an output stream of the converted file
     * @throws Exception by fatal errors
     */
    OutputStream convert(URI master, Map<String, String> parameter) throws Exception;
}
