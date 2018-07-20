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

package org.kitodo.mediaserver.core.actions;

import java.util.Map;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Example action.
 */
@Component("testAction")
public class TestAction implements IAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestAction.class);

    /**
     * Performs an action for test purposes.
     *
     * @param work      a work entity
     * @param parameter a map of parameter
     * @return an object with the result of the action, if any.
     * @throws Exception by fatal errors
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        LOGGER.info("TestAction performed on Work: " + work.getId() + " with parameter map: " + parameter);
        return null;
    }
}
