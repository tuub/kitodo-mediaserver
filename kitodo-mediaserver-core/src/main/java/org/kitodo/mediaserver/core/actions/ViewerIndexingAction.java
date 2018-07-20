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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.config.IndexingProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.exceptions.IndexingException;
import org.kitodo.mediaserver.core.services.WorkService;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * An action calling an indexing script of a viewer.
 */
@Component("viewerIndexingAction")
public class ViewerIndexingAction implements IAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ViewerIndexingAction.class);

    private IndexingProperties indexingProperties;
    private FileserverProperties fileserverProperties;
    private MediaServerUtils mediaServerUtils;
    private WorkService workService;

    @Autowired
    public void setIndexingProperties(IndexingProperties indexingProperties) {
        this.indexingProperties = indexingProperties;
    }

    @Autowired
    public void setFileserverProperties(FileserverProperties fileserverProperties) {
        this.fileserverProperties = fileserverProperties;
    }

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    @Autowired
    public void setWorkService(WorkService workService) {
        this.workService = workService;
    }

    /**
     * Runs a cache deletion action.
     * @param work      a work entity
     * @param parameter a map of parameter
     * @return always null
     * @throws Exception mainly on IO errors
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        Map<String, String> indexingArgs = new HashMap<>();
        String urlString = mediaServerUtils.getUrlStringForMetsFile(
                fileserverProperties.getRootUrl(),
                work.getId()
        );
        indexingArgs.put(indexingProperties.getIndexScriptMetsUrlArgName(), urlString);

        LOGGER.debug("Calling " + indexingProperties.getIndexScriptUrl() + " with args " + indexingArgs);

        int status = mediaServerUtils.callUrlWithArgs(indexingProperties.getIndexScriptUrl(), indexingArgs);
        if (status > 299) {
            throw new IndexingException("The work " + work.getId() + " could not be indexed, http status code: " + status);
        } else {
            work.setIndexTime(Instant.now());
            workService.updateWork(work);
            LOGGER.info("The work " + work.getId() + " successfully indexed, http status code: " + status);
        }

        return null;
    }

}
