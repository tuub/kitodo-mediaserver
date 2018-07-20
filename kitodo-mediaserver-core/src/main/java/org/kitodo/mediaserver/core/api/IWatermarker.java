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
import org.im4java.core.IMOperation;

/**
 * Interface for adding watermarks.
 */
public interface IWatermarker {

    /**
     * Takes an IMOperation object and adds watermark operation commands.
     *
     * @param operation An existing IMOperation object that the method works on.
     * @param masterFile A master file object, needed for calculating x and y values.
     * @param size The requested x size in pixels
     */
    void perform(IMOperation operation, File masterFile, Integer size) throws Exception;
}
