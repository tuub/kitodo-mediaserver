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
import org.kitodo.mediaserver.core.db.entities.Work;


/**
 * Interface for reading data form a METS file.
 */
public interface IDataReader {

    /**
     * Method read.
     *
     * @param mets The given METS file
     * @return A work entity
     * @throws Exception If error occurs
     */
    Work read(File mets) throws Exception;

}
