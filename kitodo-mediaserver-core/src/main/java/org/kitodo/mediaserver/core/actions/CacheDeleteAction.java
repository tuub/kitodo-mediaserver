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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.config.FileserverProperties;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.util.FileDeleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Delete cached derivatives.
 */
public class CacheDeleteAction implements IAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheDeleteAction.class);

    private FileserverProperties fileserverProperties;

    private FileDeleter fileDeleter;

    @Autowired
    public void setFileserverProperties(FileserverProperties fileserverProperties) {
        this.fileserverProperties = fileserverProperties;
    }

    @Autowired
    public void setFileDeleter(FileDeleter fileDeleter) {
        this.fileDeleter = fileDeleter;
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

        Path cachePath = Paths.get(fileserverProperties.getCachePath());
        Path workCachePath = Paths.get(cachePath + cachePath.getFileSystem().getSeparator() + work.getId());

        // delete files older than...
        Long age = null;
        if (parameter.containsKey("age")) {
            try {
                age = Long.parseLong(parameter.get("age"), 10);
            } catch (NumberFormatException ex) {
                // ignore "age" parameter
            }
        }

        try {
            if (age != null) {
                fileDeleter.delete(workCachePath, age);
            } else {
                fileDeleter.delete(workCachePath);
            }
        } catch (IOException ex) {
            LOGGER.error("Could not clear cache of workId " + work.getId() + ": " + ex.toString());
            throw ex;
        }

        return null;
    }

}
