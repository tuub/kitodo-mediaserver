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
import org.kitodo.mediaserver.core.processors.Toc;

/**
 * Read documents table of content from file.
 */
public interface ITocReader {

    /**
     * Read and parse the TOC.
     * @param file File to read from.
     * @return the TOC
     * @throws Exception on errors
     */
    Toc read(Path file) throws Exception;
}
