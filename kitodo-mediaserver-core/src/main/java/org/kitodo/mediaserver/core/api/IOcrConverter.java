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

import java.nio.file.Path;

/**
 * A converter to transform an OCR file from one format to another (e.g. Finereader to ALTO)
 */
public interface IOcrConverter {

    /**
     * Convert one file to another format.
     *
     * @param sourceFile file to be converted
     * @param destFile file to save the new formatted to
     * @throws Exception on conversion errors or file system errors
     */
    void convert(Path sourceFile, Path destFile) throws Exception;
}
