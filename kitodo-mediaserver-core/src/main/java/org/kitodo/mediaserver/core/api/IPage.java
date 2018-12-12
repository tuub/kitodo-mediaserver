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

import javax.validation.constraints.NotNull;

/**
 * A page representation for conversion process.
 */
public interface IPage {

    /**
     * Get the page object of the underlying implementation.
     *
     * @return the page object
     */
    Object getPage();

    /**
     * Set an image file for that page.
     *
     * @param path path to the image file
     */
    void setImagePath(@NotNull String path);

    /**
     * Set maximum page size to resize images.
     *
     * @param size maximum page width and height in pixels
     */
    void setSize(int size);

    /**
     * Render all page contents.
     *
     * @param document the document the page belongs to
     * @throws Exception on errors
     */
    void renderPage(@NotNull IDocument document) throws Exception;
}
