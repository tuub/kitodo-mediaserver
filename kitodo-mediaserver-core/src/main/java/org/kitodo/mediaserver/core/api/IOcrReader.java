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
import org.kitodo.mediaserver.core.processors.ocr.OcrPage;

/**
 * A reader to parse OCR text files.
 */
public interface IOcrReader {

    /**
     * Read an OCR file and get the text.
     *
     * @param file OCR text file
     * @return OcrPage object matching the file contents
     * @throws Exception on errors
     */
    OcrPage read(Path file) throws Exception;
}
