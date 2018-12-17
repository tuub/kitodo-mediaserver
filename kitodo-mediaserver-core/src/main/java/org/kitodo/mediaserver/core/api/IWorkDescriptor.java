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

import org.kitodo.mediaserver.core.db.entities.Work;

/**
 * Interface for string descriptors of a work.
 */
public interface IWorkDescriptor {

    /**
     * Generates a string describing of identifying a work.
     *
     * @param work the work entity
     * @return a string
     */
    String describe(Work work);
}
