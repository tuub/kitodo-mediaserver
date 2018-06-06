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
import org.kitodo.mediaserver.core.db.repositories.WorkRepository;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Locks or unlocks a work.
 */
public class WorkLockAction implements IAction {

    private WorkRepository workRepository;

    private MediaServerUtils mediaServerUtils;

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    /**
     * Locks or unlocks the work.
     * @param work      a work entity
     * @param parameter a map of parameter; enabled: lock or unlock, comment: lock comment
     * @return always null
     * @throws Exception no exception thrown in this implementation
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        mediaServerUtils.checkForRequiredParameter(parameter, "enabled");

        Boolean enabled = Boolean.parseBoolean(parameter.get("enabled"));
        work.setEnabled(enabled);
        workRepository.save(work);

        return null;
    }
}
