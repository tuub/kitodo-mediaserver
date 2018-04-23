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

import java.io.File;
import java.util.List;
import java.util.Map;


/**
 * Interface for readers of METS/MODS files.
 */
public interface IMetsReader {

    /**
     * Reads data from a mets file and returns it as a list of strings.
     *
     * @param mets the mets file
     * @param parameter optional key-value pairs
     * @return a list of strings with the result
     */
    List<String> read(File mets, Map.Entry<String, String>... parameter) throws Exception;

}
