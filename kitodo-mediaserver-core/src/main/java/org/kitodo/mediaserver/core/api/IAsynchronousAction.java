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
import org.kitodo.mediaserver.core.models.ActionControl;

/**
 * Interface for persistent actions.
 */
public interface IAsynchronousAction extends IAction {

    /**
     * Request an action.
     *
     * <p>
     * Requesting an action is basically just creating an ActionControl object and making it persistent.
     *
     * @param work    the work
     * @param action    the action name
     * @param parameter a map with parameter
     * @throws Exception by fatal errors
     */
    default void request(Work work, String action, Map<String, String> parameter) throws Exception {

        // TODO Check that the action can be mapped to an implementation

        // TODO Check that the action is not already requested

        // create an entity
        ActionControl actionControl = new ActionControl(work, action, parameter);

        // TODO make it persistent

    }

    /**
     * Performs an action previously requested.
     *
     * <p>
     * Classes implementing this interface should see to it that the result is saved in the
     * perform method, or otherwise override this method.
     *
     * @param actionControl the actionControl object with the definition of the specific action.
     * @throws Exception by fatal errors
     */
    default void performRequested(ActionControl actionControl) throws Exception {
        // TODO Set the begun_at attribute of the actionControl object and persist it.

        perform(actionControl.getWork(), actionControl.getParameter());

        // TODO Set ended_at attribute of the actionControl object and persist it.

    }


}
