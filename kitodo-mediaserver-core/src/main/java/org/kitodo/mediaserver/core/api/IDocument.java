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
import javax.validation.constraints.NotNull;

/**
 * A document representation for conversion process.
 */
public interface IDocument {

    /**
     * The document object for the underlying implementation.
     *
     * @return the document
     */
    Object getDocument();

    /**
     * Get a list of all pages.
     */
    List<IPage> getPages();

    /**
     * Saves the whole document and its pages to a file.
     *
     * @param path the file path
     * @throws Exception on errors
     */
    void save(@NotNull String path) throws Exception;
}
