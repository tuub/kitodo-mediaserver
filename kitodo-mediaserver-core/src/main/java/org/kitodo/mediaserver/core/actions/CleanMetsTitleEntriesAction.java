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

import java.io.File;
import java.util.AbstractMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.kitodo.mediaserver.core.api.IAction;
import org.kitodo.mediaserver.core.api.IMetsTransformer;
import org.kitodo.mediaserver.core.db.entities.Work;
import org.kitodo.mediaserver.core.util.MediaServerUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Action cleaning title entries in the mets/mods file using a regular expression with placeholder.
 */
public class CleanMetsTitleEntriesAction implements IAction {

    private MediaServerUtils mediaServerUtils;
    private IMetsTransformer metsTransformer;

    @Autowired
    public void setMediaServerUtils(MediaServerUtils mediaServerUtils) {
        this.mediaServerUtils = mediaServerUtils;
    }

    public void setMetsTransformer(IMetsTransformer metsTransformer) {
        this.metsTransformer = metsTransformer;
    }

    /**
     * Cleans title entries in the mets file.
     *
     * <p>
     * This action requires a parameter "pattern", e.g. "<<(.*?)>>".
     *
     * @param work      a work entity
     * @param parameter a map of parameter
     * @return null
     * @throws Exception by fatal errors
     */
    @Override
    public Object perform(Work work, Map<String, String> parameter) throws Exception {

        mediaServerUtils.checkForRequiredParameter(parameter, "pattern");

        File metsFile = mediaServerUtils.getMetsFileForWork(work);

        metsTransformer.transform(metsFile, metsFile, new AbstractMap.SimpleEntry<>("pattern", parameter.get("pattern")));

        if (StringUtils.equals(parameter.get("performOnAnchor"), "true")) {
            File metsAnchorFile = mediaServerUtils.getAnchorMetsFileForWork(work);
            if (metsAnchorFile.exists()) {
                metsTransformer.transform(
                        metsAnchorFile,
                        metsAnchorFile,
                        new AbstractMap.SimpleEntry<>("pattern", parameter.get("pattern")));
            }
        }

        return null;
    }
}
