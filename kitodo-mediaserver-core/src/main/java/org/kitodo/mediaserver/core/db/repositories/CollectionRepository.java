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

package org.kitodo.mediaserver.core.db.repositories;

import org.kitodo.mediaserver.core.db.entities.Collection;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for the collections.
 */
public interface CollectionRepository extends CrudRepository<Collection, String> {

}
