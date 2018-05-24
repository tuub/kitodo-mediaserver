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

package org.kitodo.mediaserver.importer.api;

import org.kitodo.mediaserver.core.db.entities.Work;

/**
 * Interface for checking if a work or any of its identifiers is already present.
 */
public interface IWorkChecker {

    /**
     * Checks if a work or any of its identifiers is already present in the database.
     *
     * @param work the work to be checked
     * @return a work object of the already present work or null, if non present
     * @throws Exception if a severe error occurs
     */
    Work check(Work work) throws Exception;

}
