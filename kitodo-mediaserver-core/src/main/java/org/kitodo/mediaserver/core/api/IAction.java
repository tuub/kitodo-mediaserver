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
import org.kitodo.mediaserver.core.models.ActionControl;


/**
 * Interface of all actions.
 */
public interface IAction {

    /**
     * Request an action.
     *
     * <p>
     * Requesting an action is basically just creating an ActionControl object and making it persistent.
     *
     * @param workId    the id of the work
     * @param action    the action name
     * @param parameter a map with parameter
     */
    default void request(int workId, String action, Map<String, String> parameter) throws Exception {

        // Check that the action can be mapped to an implementation

        // Check that the action is not already requested

        // create an entity
        ActionControl actionControl = new ActionControl(workId, action, parameter);

        // make it persistent

    }

    /**
     * Performs the action.
     *
     * <p>
     * Implementations of these method may or may not return an object.
     *
     * @param actionControl the actionControl object with the definition of the specific action.
     * @return  an object with the result of the action, if any.
     */
    Object perform(ActionControl actionControl) throws Exception;

}
