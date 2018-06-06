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
import org.kitodo.mediaserver.core.db.entities.ActionData;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for ActionData.
 */
public interface ActionRepository extends CrudRepository<ActionData, Integer> {

    /**
     * Finds the last finished actions.
     * @param work the Work
     * @param actionName the action bean name
     * @return the ActionDatas
     */
    List<ActionData> findByWorkAndActionNameOrderByEndTimeDesc(Work work, String actionName);

    /**
     * Finds the unfinished actions (not running and running).
     * @param work the Work
     * @param actionName the action bean name
     * @return the ActionDatas
     */
    List<ActionData> findByWorkAndActionNameAndEndTimeIsNull(Work work, String actionName);

    /**
     * Finds the currently running actions.
     * @param work the Work
     * @param actionName the action bean name
     * @return the ActionDatas
     */
    List<ActionData> findByWorkAndActionNameAndStartTimeIsNotNullAndEndTimeIsNull(Work work, String actionName);
}
