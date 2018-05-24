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
 * Interface for for inserting / updating work data.
 */
public interface IWorkDataWriter {

    /**
     * Writes the data work to the database, updating the work if already present. Replaces all identifiers.
     *
     * @param work the work entity
     * @throws Exception if a severe error occurs
     */
    void write(Work work);
}
