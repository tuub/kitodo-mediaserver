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

import java.util.List;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Repository for works.
 */
public interface WorkRepository extends PagingAndSortingRepository<Work, String>, JpaSpecificationExecutor<Work> {

    /**
     * Finds all works where the title contains the keyword.
     *
     * @param keyword any word or string
     * @return a list of works
     */
    List<Work> findByTitleContaining(String keyword);

    /**
     * Finds all works belonging to a collection.
     *
     * @param collectionName the name of the collection
     * @return a list of works
     */
    List<Work> findByCollectionsName(String collectionName);
}

