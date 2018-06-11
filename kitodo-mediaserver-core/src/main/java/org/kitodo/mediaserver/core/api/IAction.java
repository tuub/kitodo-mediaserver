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

import java.util.Map;
import org.kitodo.mediaserver.core.db.entities.Work;


/**
 * Interface of all actions.
 */
public interface IAction {

    /**
     * Performs the action.
     *
     * <p>
     * Implementations of these method may or may not return an object.
     *
     * @param work a work entity
     * @param parameter a map of parameter
     * @return an object with the result of the action, if any.
     * @throws Exception by fatal errors
     */
    Object perform(Work work, Map<String, String> parameter) throws Exception;

}
