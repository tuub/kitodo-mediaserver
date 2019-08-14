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

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import org.kitodo.mediaserver.core.conversion.FileEntry;

/**
 * Converter interface.
 */
public interface IConverter {

    /**
     * Converts a file from a given uri. Returns an input stream with the result.
     *
     * @param pages a map (key=sorting order of files) of maps (key={master,fulltext,...}) with work files
     * @param parameter a map of parameter
     * @return an output stream of the converted file
     * @throws Exception by fatal errors
     */
    InputStream convert(TreeMap<Integer, Map<String, FileEntry>> pages, Map<String, Object> parameter) throws Exception;
}
